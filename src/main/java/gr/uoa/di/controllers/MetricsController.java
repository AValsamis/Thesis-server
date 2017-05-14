package gr.uoa.di.controllers;

import gr.uoa.di.entities.*;
import gr.uoa.di.repository.*;
import gr.uoa.di.utils.FallDetectionUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
public class MetricsController {

    @Autowired
    private ZoneRepository zoneRepository;
    @Autowired
    private WifiRepository wifiRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private WifiInZoneRepository wifiInZoneRepository;

    @Autowired
    private FallDetectionUtil fallDetectionUtil;

    @Autowired
    private AccelerometerStatsRepository accelerometerStatsRepository;

    @Autowired
    private DataCollectionServiceStatusRepository dataCollectionServiceStatusRepository;

    @Autowired
    private ElderlyResponsibleRepository elderlyResponsibleRepository;

    @Autowired
    private UserInZoneRepository userInZoneRepository;

    @Autowired
    private RecognizedActivityStorageRepository recognizedActivityStorageRepository;

    private static final int maximum = 360;
    private static final int minimum = 0;

    @PostConstruct
    void initializeFallDetectionProcesses() throws IOException {
        List<User> elderlyUsers = dataCollectionServiceStatusRepository.findUsersThatFallDetectionShouldRun();
        for(User elderlyUser : elderlyUsers)
        {
            ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
            exec.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    SimpleResponse response = shouldRun(elderlyUser.getUsername());
                    if(response.getOk())
                    {
                        System.out.println("FALL DETECTION IS ON");
                        fallDetectionUtil.startFallDetection(elderlyUser.getUsername());
                    }
                    else
                    {
                        System.out.println("FALL DETECTION IS OFF");
                        Thread.currentThread().interrupt();
                    }
                }
            }, 0, 60, TimeUnit.SECONDS);

        }
    }

    @ApiOperation(value = "Send Danger zone", tags = "Zone")
    @RequestMapping(value = "/registerZone", method = RequestMethod.POST )
    public SimpleResponse registerZone(@RequestPart(name="wifi") List<Wifi> wifis, @RequestPart(name="zone")Zone zone) throws IOException {
        System.out.println("Saving zone with signal strength list:");

        User user = zone.getUser();
        User userFromDB = userRepository.findByUsername(user.getUsername());
        if(userFromDB==null || userFromDB.getUsername()==null)
            userRepository.save(user);
        else{
            zone.setUser(userFromDB);
        }
        Zone zone1 = null;
        Zone zoneFromDB = zoneRepository.findByFriendlyName(zone.getFriendlyName());
        if(zoneFromDB==null || zoneFromDB.getFriendlyName()==null)
            zone1 = zoneRepository.save(zone);
        else
            return new SimpleResponse("Name " + zone.getFriendlyName() + " already assigned to a zone. Please try another name", false);

        for (Wifi wifi: wifis ) {
            try {
                Wifi wifiFromDB = wifiRepository.findByName(wifi.getName());
                Wifi wifi1 = null;
                WifiInZone wifiInZone = new WifiInZone();
                if(wifiFromDB==null || wifiFromDB.getName()==null) {
                    wifi1 = wifiRepository.save(wifi);
                    wifiInZone.setWifi(wifi1);
                }
                else {
                    wifiInZone.setWifi(wifiFromDB);
                }
                wifiInZone.setZone(zone1);

                Double signalStrengthSum = 0.0;
                for(int i = 0; i < wifi.getSignalStrength().size(); i++)
                {
                    signalStrengthSum += wifi.getSignalStrength().get(i);
                }
                Double signalStrength = (signalStrengthSum/wifi.getSignalStrength().size());
                if(signalStrength > -80.0) {
                    wifiInZone.setSignalStrength(signalStrength);
                    wifiInZoneRepository.save(wifiInZone);
                }
            } catch (Exception ex) {
                System.out.println("Error creating the zone: " + ex.toString());
                ex.printStackTrace();
                return new SimpleResponse("Error creating the zone: " + ex.toString(),false);
            }
        }
        return new SimpleResponse("Zone succesfully created with id = " + zone1.getZoneId() + " and name = " + zone1.getFriendlyName(),true);
    }

    @ApiOperation(value = "Get zone the user is currently in", tags = "Zone")
    @RequestMapping(value = "/getZone", method = RequestMethod.POST)
    public SimpleResponse getZone(@RequestPart("wifi") List<Wifi> wifis, @RequestPart("user") User user) {

        System.out.println("SENT LIST OF ZONES: " + Arrays.asList(wifis));
        User userfromDb = userRepository.findByUsername(user.getUsername());
        Long guardianId = elderlyResponsibleRepository.findAssociatedGuardian(userfromDb.getUserId());
        // handle case where no zones exist for use
        List <Zone> zones = zoneRepository.findZonesByUserId(guardianId);
        if(zones==null || zones.size()==0)
            return null;
        User guardianFromDb = userRepository.findOne(guardianId);
        List<String> closestZones = new ArrayList<>();
        for(Wifi wifi : wifis)
        {
            Wifi wifiFromDB = wifiRepository.findByName(wifi.getName());
            Double min = Double.MAX_VALUE;
            Double signalStrengthSum = 0.0;
            for(int i = 0; i < wifi.getSignalStrength().size(); i++)
            {
                signalStrengthSum += wifi.getSignalStrength().get(i);
            }
            Double closest = (signalStrengthSum/wifi.getSignalStrength().size());
            System.out.println("Wifi name: " + wifi.getName() + " ss: " + closest);
            if(closest <= -80.0)
            {
                System.out.println("Bypassing wifi: " + wifi.getName() + "...");
                continue;
            }
            String closestByName = "";
            int finalmin = -1;
            if(wifiFromDB!=null)
            {
                WifiInZone[] wifiInZones = wifiInZoneRepository.findZonesByWifiId(wifiFromDB, guardianFromDb);
                System.out.println("TEST: " + Arrays.asList(wifiInZones));
                for(int i = 0; i < wifiInZones.length; i++)
                {
                    System.out.println("Searching for wifi: " + wifiInZones[i].getWifi().getName() + " ss from db in zone: " + wifiInZones[i].getZone().getFriendlyName() + " is : " +wifiInZones[i].getSignalStrength());
                    final Double diff = Math.abs(wifiInZones[i].getSignalStrength() - closest);
                    System.out.println("diff for zone " + wifiInZones[i].getZone().getFriendlyName() + " is: " + diff);
                    if (diff < min) {
                        min = diff;
                        if(min <= 5.0)
                        {
                            finalmin = i;
                        }
                    }
                }
                if(finalmin != -1) {
                    closestByName = zoneRepository.findFrienldyNameByZoneId(wifiInZones[finalmin].getZone().getZoneId());
                    System.out.println("Closest zone is: " + closestByName);
                    closestZones.add(closestByName);

                    for(int i = 0; i < wifiInZones.length; i++)
                    {
                        if (i==finalmin) continue;
                        final Double diff = Math.abs(wifiInZones[i].getSignalStrength() - closest);
                        if (diff - min <= 1.5) {
                            System.out.println("I ve also added "+wifiInZones[i].getZone().getFriendlyName() +" as closest");
                            closestByName = zoneRepository.findFrienldyNameByZoneId(wifiInZones[i].getZone().getZoneId());
                            closestZones.add(closestByName);
                        }
                    }
                }
                else
                {
                    closestZones.add("unknown");
                }
            }
        }

        Map<String, Long> counts =
                closestZones.stream().collect(Collectors.groupingBy(e -> e, Collectors.counting()));
        System.out.println(counts);

        Long maxCount =  Collections.max(counts.values());
        System.out.println(maxCount);

        Zone zoneFromDb = null;
        UserInZone userInZone = new UserInZone();
        boolean checkUnknown=true;
        boolean unknown = false;
        for (Object o : counts.entrySet()) {
            Map.Entry pair = (Map.Entry) o;
            if (pair.getValue() == maxCount){
                if(!pair.getKey().toString().equals("unknown")) {
                    checkUnknown=false;
                    if(unknown) unknown=false;
                    System.out.println(pair.getKey().toString() + " " + pair.getValue().toString());
                    userInZone.setElderlyUser(userfromDb);
                    zoneFromDb = zoneRepository.findByFriendlyName(pair.getKey().toString());
                    // If max votes are for danger zones, return it immediately, else if it a safe zone
                    // continue to see if it draws with any danger zone, and if so return this zone instead
                    if (zoneFromDb.getIsSafe() == 0) {
                        userInZone.setZone(zoneFromDb);
                        userInZone.setTimestamp(new Date());
                        userInZoneRepository.save(userInZone);
                        return new SimpleResponse(pair.getKey().toString());
                    }
                }
                else
                {
                    if(checkUnknown)
                    {
                        unknown = true;
                    }
                }
            }
        }

        if(unknown)
        {
            userInZone.setElderlyUser(userfromDb);
            userInZone.setZone(null);
            userInZone.setTimestamp(new Date());
            userInZoneRepository.save(userInZone);
            return new SimpleResponse("unknown");
        }

        if(zoneFromDb!=null)
        {
            userInZone.setZone(zoneFromDb);
            userInZone.setTimestamp(new Date());
            userInZoneRepository.save(userInZone);
            return new SimpleResponse(zoneFromDb.getFriendlyName());
        }

        return null;
    }

    @ApiOperation(value = "Send if user is in danger zone", tags = "Zone")
    @RequestMapping(value = "/isInDangerZone", method = RequestMethod.POST ,consumes="application/json")
    public boolean isInDangerZone(@RequestParam(value="Signal Strength List") List<Zone> signalStrengths) {
        //check if user is in danger zone and send true if he is/false otherwise
        return true;
    }

    @ApiOperation(value = "Send safe zones of user", tags = "Zone")
    @RequestMapping(value = "/safeZones/{user}", method = RequestMethod.GET ,produces="application/json")
    public ResponseEntity<List<Zone>> safeZonesForUser(@PathVariable(value="user") String user) {
        List<Zone> zones = zoneRepository.findUserSafeZones(user);
        return  new ResponseEntity<List<Zone>>(zones, HttpStatus.OK);

    }

    @ApiOperation(value = "Send danger zones of user", tags = "Zone")
    @RequestMapping(value = "/dangerZones/{user}", method = RequestMethod.GET ,produces="application/json")
    public ResponseEntity<List<Zone>> dangerZonesForUser(@PathVariable(value="user") String user) {
        List<Zone> zones = zoneRepository.findUserDangerZones(user);
        zones.forEach(System.out::println);

        return  new ResponseEntity<List<Zone>>(zones, HttpStatus.OK);

    }
    @ApiOperation(value = "Send Data Packet", tags = "Data Collection")
    @RequestMapping(value = "/sendDataPacket", method = RequestMethod.POST, consumes="application/json")
    public SimpleResponse sendDataPacket(@RequestBody DataPacket dataPacket) throws IOException {
        //check if user is in danger zone and send true if he is/false otherwise

//        System.out.println("Putting data from packet in DB for user: " + dataPacket.getUser().getUsername());

        ArrayList<AccelerometerStats> accelerometerStats = dataPacket.getAccelerometerStats();

        for(AccelerometerStats accelerometerStats1 : accelerometerStats)
        {
            User userFromDB = userRepository.findByUsername(dataPacket.getUser().getUsername());
            if(userFromDB==null || userFromDB.getUsername()==null)
                userRepository.save(dataPacket.getUser());
            else{
                accelerometerStats1.setUser(userFromDB);
            }

            accelerometerStats1.setUser(userFromDB);
            accelerometerStatsRepository.save(accelerometerStats1);
        }

//        System.out.println(dataPacket.toString());
        return new SimpleResponse("Invoked with: " + dataPacket.toString(),true);
    }

    @ApiOperation(value = "Start data collection service", tags = "Data Collection")
    @RequestMapping(value = "/startDataCollection", method = RequestMethod.POST ,consumes="application/json")
    // This is the responsible user
    public SimpleResponse startDataCollection(@RequestBody User user) throws IOException {

        System.out.println("Start data collection");

        DataCollectionServiceStatus dataCollectionServiceStatus = new DataCollectionServiceStatus();

        User responsible = userRepository.findByUsername(user.getUsername());

        Long elderlyId = elderlyResponsibleRepository.findAssociatedElderly(responsible.getUserId());

        User elderly = userRepository.findOne(elderlyId);

        dataCollectionServiceStatus.setUser(elderly);
        dataCollectionServiceStatus.setShouldRun(true);
        dataCollectionServiceStatus.setTimestamp(new Date());

        dataCollectionServiceStatusRepository.save(dataCollectionServiceStatus);

        ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();

        exec.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                SimpleResponse response = shouldRun(user.getUsername());
                if(response.getOk())
                {
                    fallDetectionUtil.startFallDetection(elderly.getUsername());
                }
                else
                {
                    Thread.currentThread().interrupt();
                }
            }
        }, 0, 60, TimeUnit.SECONDS);

        return new SimpleResponse("Will start data collection", true);
    }

    @ApiOperation(value = "Stop data collection service", tags = "Data Collection")
    @RequestMapping(value = "/stopDataCollection", method = RequestMethod.POST ,consumes="application/json")
    // This is the responsible user
    public SimpleResponse stopDataCollection(@RequestBody User user) {

        System.out.println("Stop data collection");

        DataCollectionServiceStatus dataCollectionServiceStatus = new DataCollectionServiceStatus();

        User responsible = userRepository.findByUsername(user.getUsername());

        Long elderlyId = elderlyResponsibleRepository.findAssociatedElderly(responsible.getUserId());

        User elderly = userRepository.findOne(elderlyId);

        dataCollectionServiceStatus.setUser(elderly);
        dataCollectionServiceStatus.setShouldRun(false);
        dataCollectionServiceStatus.setTimestamp(new Date());

        dataCollectionServiceStatusRepository.save(dataCollectionServiceStatus);

        return new SimpleResponse("Will stop data collection", true);
    }

    @ApiOperation(value = "Get if data collection service should run", tags = "Data Collection")
    @RequestMapping(value = "/shouldRun/{username}", method = RequestMethod.GET ,produces="application/json")
    // This is the elderly username
    public SimpleResponse shouldRun(@PathVariable(value="username") String username) {

        System.out.println("Should run?"+username);

        User user = userRepository.findByUsername(username);
        if(elderlyResponsibleRepository.findAssociatedGuardian(user.getUserId())!=null) {
            List<DataCollectionServiceStatus> serviceStatus = dataCollectionServiceStatusRepository.getLatestTimestampForUser(user.getUserId());
            if (serviceStatus != null && serviceStatus.size() > 0 && serviceStatus.get(0) != null) {
                return new SimpleResponse("", serviceStatus.get(0).getShouldRun());
            }
            else
                return new SimpleResponse("", false);
        }
        else
        {
            Long elderlyId = elderlyResponsibleRepository.findAssociatedElderly(user.getUserId());
            User elderly = userRepository.findOne(elderlyId);
            List<DataCollectionServiceStatus> serviceStatus = dataCollectionServiceStatusRepository.getLatestTimestampForUser(elderly.getId());
            if (serviceStatus != null && serviceStatus.size() > 0 && serviceStatus.get(0) != null)
                return new SimpleResponse("", serviceStatus.get(0).getShouldRun());
            else
                return new SimpleResponse("", false);
        }
    }

    @ApiOperation(value = "Send activity of user", tags = "Activity Recognition")
    @RequestMapping(value = "/activity", method = RequestMethod.POST ,consumes="application/json")
    // This is the elderly username
    public SimpleResponse activity(@RequestBody RecognizedActivity recognizedActivity) {

        User userFromDb = userRepository.findByUsername(recognizedActivity.getUser().getUsername());
        recognizedActivity.setUser(userFromDb);

        RecognizedActivityStorage recognizedActivityStorage = new RecognizedActivityStorage();
        recognizedActivityStorage.setUser(recognizedActivity.getUser());
        recognizedActivityStorage.setCertainty(recognizedActivity.getCertainty());
        recognizedActivityStorage.setState(recognizedActivity.getState());

        String dateString = recognizedActivity.getTimestamp();
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            recognizedActivityStorage.setTimestamp(format1.parse(dateString));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        recognizedActivityStorage.setUser(recognizedActivity.getUser());

        recognizedActivityStorageRepository.save(recognizedActivityStorage);
        return new SimpleResponse("Successfully saved recognized activity: " + recognizedActivity.toString(), true);
    }


}

package gr.uoa.di.controllers;

import gr.uoa.di.entities.*;
import gr.uoa.di.repository.*;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Angelos on 9/18/2016.
 */
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
    private AccelerometerStatsRepository accelerometerStatsRepository;
    @Autowired
    private OrientationStatsRepository orientationStatsRepository;


    private static final int maximum = 360;
    private static final int minimum = 0;

    @ApiOperation(value = "Dummy getter of single accelerometer Metrics", tags = "Metrics")
    @RequestMapping(value = "/accelerometerInstance", method = RequestMethod.GET)
    public AccelerometerStats accelerometerGetLast() {

        AccelerometerStats accelerometerStats = new AccelerometerStats();
        Integer randomNum = minimum + (int)(Math.random() * maximum);
        accelerometerStats.setX(randomNum.toString());
        randomNum = minimum + (int)(Math.random() * maximum);
        accelerometerStats.setY(randomNum.toString());
        randomNum = minimum + (int)(Math.random() * maximum);
        accelerometerStats.setZ(randomNum.toString());

        return accelerometerStats;
    }

    @ApiOperation(value = "Send Danger zone", tags = "Metrics")
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
                // TODO ???
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


    @ApiOperation(value = "Get zone the user is currently in", tags = "Metrics")
    @RequestMapping(value = "/getZone", method = RequestMethod.POST, consumes="application/json")
    public SimpleResponse getZone(@RequestBody List<Wifi> wifis) {

        System.out.println("SENT LIST OF ZONES: " + Arrays.asList(wifis));
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
            // TODO ???
            if(closest <= -80.0)
            {
                System.out.println("Bypassing wifi: " + wifi.getName() + "...");
                continue;
            }
            String closestByName = "";
            int finalmin = -1;
            if(wifiFromDB!=null)
            {
                WifiInZone[] wifiInZones = wifiInZoneRepository.findZonesByWifiId(wifiFromDB);
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
                    System.out.println("Wifi " + wifi.getName() + " not counted!");

            }
        }
        Map<String, Long> counts =
                closestZones.stream().collect(Collectors.groupingBy(e -> e, Collectors.counting()));
        System.out.println(counts);

        Long maxCount =  Collections.max(counts.values());
        System.out.println(maxCount);

        for (Object o : counts.entrySet()) {
            Map.Entry pair = (Map.Entry) o;
            if (pair.getValue() == maxCount){
                System.out.println(pair.getKey().toString() + " " + pair.getValue().toString());
                return new SimpleResponse(pair.getKey().toString());
            }
        }
        return null;
    }

    @ApiOperation(value = "Send if user is in danger zone", tags = "Metrics")
    @RequestMapping(value = "/isInDangerZone", method = RequestMethod.POST ,consumes="application/json")
    public boolean isInDangerZone(@RequestParam(value="Signal Strength List") List<Zone> signalStrengths) {
        //check if user is in danger zone and send true if he is/false otherwise
        return true;
    }

    @ApiOperation(value = "Send safe zones of user", tags = "Metrics")
    @RequestMapping(value = "/safeZones/{user}", method = RequestMethod.GET ,produces="application/json")
    public ResponseEntity<List<Zone>> safeZonesForUser(@PathVariable(value="user") String user) {
        List<Zone> zones = zoneRepository.findUserSafeZones(user);
        return  new ResponseEntity<List<Zone>>(zones, HttpStatus.OK);

    }




    @ApiOperation(value = "Send danger zones of user", tags = "Metrics")
    @RequestMapping(value = "/dangerZones/{user}", method = RequestMethod.GET ,produces="application/json")
    public ResponseEntity<List<Zone>> dangerZonesForUser(@PathVariable(value="user") String user) {
        List<Zone> zones = zoneRepository.findUserDangerZones(user);
        zones.forEach(System.out::println);

        return  new ResponseEntity<List<Zone>>(zones, HttpStatus.OK);

    }
    @ApiOperation(value = "Send Data Packet", tags = "Metrics")
    @RequestMapping(value = "/sendDataPacket", method = RequestMethod.POST, consumes="application/json")
    public SimpleResponse sendDataPacket(@RequestBody DataPacket dataPacket) throws IOException {
        //check if user is in danger zone and send true if he is/false otherwise

        System.out.println("Putting data from packet in DB for user: " + dataPacket.getUser().getUsername());

        ArrayList<AccelerometerStats> accelerometerStats = dataPacket.getAccelerometerStats();
        ArrayList<OrientationStats> orientationStats = dataPacket.getOrientationStats();


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
        for(OrientationStats orientationStats1 : orientationStats)
        {
            User userFromDB = userRepository.findByUsername(dataPacket.getUser().getUsername());
            if(userFromDB==null || userFromDB.getUsername()==null)
                userRepository.save(dataPacket.getUser());
            else{
                orientationStats1.setUser(userFromDB);
            }

            orientationStats1.setUser(userFromDB);
            orientationStatsRepository.save(orientationStats1);
        }

        System.out.println(dataPacket.toString());
        return new SimpleResponse("Invoked with: " + dataPacket.toString(),true);
    }

    @ApiOperation(value = "Find if user is elderly by username", tags = "Metrics")
    @RequestMapping(value = "/isElderly/{username}", method = RequestMethod.GET ,produces="application/json")
    public boolean isElderly(@PathVariable(value="username") String username) {
        User userFromDB = userRepository.findByUsername(username);
        return userFromDB.getResponsibleUserName() != null;
    }


}

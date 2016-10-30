package gr.uoa.di.controllers;

import gr.uoa.di.entities.*;
import gr.uoa.di.repository.UserRepository;
import gr.uoa.di.repository.WifiInZoneRepository;
import gr.uoa.di.repository.WifiRepository;
import gr.uoa.di.repository.ZoneRepository;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
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
    public String getZone(@RequestBody List<Wifi> wifis) {

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
                        closestByName = zoneRepository.findFrienldyNameByZoneId(wifiInZones[i].getZone().getZoneId());
                    }

                }
                System.out.println("Closest zone is: " + closestByName);
                closestZones.add(closestByName);
            }
        }
        Map<String, Long> counts =
                closestZones.stream().collect(Collectors.groupingBy(e -> e, Collectors.counting()));
        System.out.println(counts);

        closestZones.sort(String::compareToIgnoreCase);
        String currentMax = "";
        int maxCount = 0;
        String current = "";
        int count = 0;
        for(int i = 0; i < closestZones.size(); i++) {
            String item = closestZones.get(i);
            if(item.equals(current)) {
                count++;
            }
            else {
                if(count > maxCount) {
                    maxCount = count;
                    currentMax = current;
                }
                count = 1;
                current = item;
            }
        }

        return currentMax;
    }

    @ApiOperation(value = "Send if user is in danger zone", tags = "Metrics")
    @RequestMapping(value = "/isInDangerZone", method = RequestMethod.POST ,consumes="application/json")
    public boolean isInDangerZone(@RequestParam(value="Signal Strength List") List<Zone> signalStrengths) {
        //check if user is in danger zone and send true if he is/false otherwise
        return true;
    }


}

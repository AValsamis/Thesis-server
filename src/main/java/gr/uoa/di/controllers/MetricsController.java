package gr.uoa.di.controllers;

import gr.uoa.di.entities.*;
import gr.uoa.di.repository.UserRepository;
import gr.uoa.di.repository.WifiRepository;
import gr.uoa.di.repository.ZoneRepository;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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
    @RequestMapping(value = "/registerDangerZone", method = RequestMethod.POST ,consumes="application/json")
    public SimpleResponse registerDangerZone(@RequestBody List<Zone> signalStrengths) {
        System.out.println("Saving danger zone with signal strength list:");
        String zoneId = UUID.randomUUID().toString();
        for (Zone zone: signalStrengths ) {
            System.out.println(zone.toString());
            try {
                User user = zone.getUser();
                User userFromDB = userRepository.findByUsername(user.getUsername());
                if(userFromDB==null || userFromDB.getUsername()==null)
                    zone.setUser(userRepository.save(user));
                else
                    zone.setUser(userFromDB);
                Wifi wifi = zone.getWifi();
                Wifi wifiFromDB = wifiRepository.findByMacAddress(wifi.getMacAddress());
                if(wifiFromDB==null || wifiFromDB.getMacAddress()==null)
                    zone.setWifi(wifiRepository.save(wifi));
                else
                    zone.setWifi(wifiFromDB);
                zone.setZoneId(zoneId);
                zoneRepository.save(zone);
            } catch (Exception ex) {
                System.out.println("Error creating the zone: " + ex.toString());
                ex.printStackTrace();
                return new SimpleResponse("Error creating the zone: " + ex.toString());
            }
        }
        System.out.println("Danger Zone succesfully created with id = " + zoneId);
        return new SimpleResponse("Danger Zone succesfully created with id = " + zoneId);
    }

    @ApiOperation(value = "Send Safe zone", tags = "Metrics")
    @RequestMapping(value = "/registerSafeZone", method = RequestMethod.POST ,consumes="application/json")
    public SimpleResponse registerSafeZone(@RequestBody List<Zone> signalStrengths) {
        System.out.println("Saving safe zone with signal strength list:");
        String zoneId = UUID.randomUUID().toString();
        for (Zone zone: signalStrengths ) {
            System.out.println(zone.toString());
            try {
                User user = zone.getUser();
                User userFromDB = userRepository.findByUsername(user.getUsername());
                if(userFromDB==null || userFromDB.getUsername()==null)
                    zone.setUser(userRepository.save(user));
                else
                    zone.setUser(userFromDB);
                Wifi wifi = zone.getWifi();
                Wifi wifiFromDB = wifiRepository.findByMacAddress(wifi.getMacAddress());
                if(wifiFromDB==null || wifiFromDB.getMacAddress()==null)
                    zone.setWifi(wifiRepository.save(wifi));
                else
                    zone.setWifi(wifiFromDB);
                zone.setZoneId(zoneId);
                zoneRepository.save(zone);
            } catch (Exception ex) {
                System.out.println("Error creating the zone: " + ex.toString());
                ex.printStackTrace();
                return new SimpleResponse("Error creating the zone: " + ex.toString());
            }
        }
        System.out.println("Safe Zone succesfully created with id = " + zoneId);
        return new SimpleResponse("Safe Zone succesfully created with id = " + zoneId);
    }

    @ApiOperation(value = "Send if user is in danger zone", tags = "Metrics")
    @RequestMapping(value = "/isInDangerZone", method = RequestMethod.POST ,consumes="application/json")
    public boolean isInDangerZone(@RequestParam(value="Signal Strength List") List<Zone> signalStrengths) {
        //check if user is in danger zone and send true if he is/false otherwise
        return true;
    }


}

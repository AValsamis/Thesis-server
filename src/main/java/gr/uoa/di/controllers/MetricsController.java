package gr.uoa.di.controllers;

import gr.uoa.di.entities.AccelerometerStats;
import gr.uoa.di.entities.SignalStrength;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by Angelos on 9/18/2016.
 */
@RestController
public class MetricsController {

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
    public void registerDangerZone(@RequestParam(value="Signal Strength List") List<SignalStrength> signalStrengths) {
        System.out.println("Saving danger zone with signal strength list:");
        for (SignalStrength signalStrength: signalStrengths )
        {
            System.out.println("Saving " + signalStrength.getWifi().getName() + " "+ signalStrength.getSignalStrength());
        }
    }

    @ApiOperation(value = "Send Safe zone", tags = "Metrics")
    @RequestMapping(value = "/registerSafeZone", method = RequestMethod.POST ,consumes="application/json")
    public void registerSafeZone(@RequestParam(value="Signal Strength List") List<SignalStrength> signalStrengths) {
        System.out.println("Saving safe zone with signal strength list:");
        for (SignalStrength signalStrength: signalStrengths )
        {
            System.out.println("Saving " + signalStrength.getWifi().getName() + " "+ signalStrength.getSignalStrength());
        }
    }

    @ApiOperation(value = "Send if user is in danger zone", tags = "Metrics")
    @RequestMapping(value = "/isInDangerZone", method = RequestMethod.POST ,consumes="application/json")
    public boolean isInDangerZone(@RequestParam(value="Signal Strength List") List<SignalStrength> signalStrengths) {
        //check if user is in danger zone and send true if he is/false otherwise
        return true;
    }


}

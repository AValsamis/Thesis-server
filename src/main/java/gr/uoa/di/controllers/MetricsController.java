package gr.uoa.di.controllers;

import gr.uoa.di.entities.AccelerometerStats;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Angelos on 9/18/2016.
 */
@RestController
public class MetricsController {

    private static final int maximum = 360;
    private static final int minimum = 0;

    @ApiOperation(value = "Dummy getter of single accelerometer Metrics", tags = "Metrics" +
            "")
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
}

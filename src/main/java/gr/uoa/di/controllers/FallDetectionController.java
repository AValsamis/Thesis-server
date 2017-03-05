package gr.uoa.di.controllers;

import gr.uoa.di.entities.AccelerometerStats;
import gr.uoa.di.entities.SimpleResponse;
import gr.uoa.di.entities.User;
import gr.uoa.di.entities.Zone;
import gr.uoa.di.repository.AccelerometerStatsRepository;
import gr.uoa.di.repository.ElderlyResponsibleRepository;
import gr.uoa.di.repository.UserRepository;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by skand on 2/5/2017.
 */

@RestController
public class FallDetectionController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ElderlyResponsibleRepository elderlyResponsibleRepository;

    @Autowired
    private AccelerometerStatsRepository accelerometerStatsRepository;
    private static List<AccelerometerStats> accelerometerDatas = new LinkedList<AccelerometerStats>();
    private static double G = 9.81;
    private static double LOWER_THRESHOLD = 0.5*G;
    private static double MAX_DIFFERENCE = 2.5*G;
    private static int MAX_TRIES = 10;
    private static int MAX_TRIES_AFTER_FALL = 20;
    private static int MAX_STABLE_INTERVAL = 5;

    @ApiOperation(value = "Start fall detection algorithm", tags = "Fall Detection")
    @RequestMapping(value = "/startFallDetection/{userId}",method = RequestMethod.GET , produces="application/json")
    public int startFallDetection(@PathVariable(value="userId") String userId) {

        int fallCertainty = 0;
        Long elderly = elderlyResponsibleRepository.findAssociatedElderly(Long.parseLong(userId));
//        System.out.println(elderly);
//        return new ResponseEntity<SimpleResponse>(new SimpleResponse("Invoked with: " + elderly.toString(),true), HttpStatus.OK);
        try {

            Date dNow = new Date( ); // Instantiate a Date object
            Calendar cal = Calendar.getInstance();
            cal.setTime(dNow);
            cal.add(Calendar.MINUTE, -20);
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            System.out.println(cal.getTime());
            dNow = cal.getTime();
            String timestamp = format1.format(dNow);
            accelerometerDatas = accelerometerStatsRepository.findByTimeStamp(timestamp);


            LinkedList<AccelerometerStats> remainingAccelerometerDatas = new LinkedList<>();

            while(accelerometerDatas.size()>1)

            {
                System.out.println(accelerometerDatas);

                Double preFallAcceleration = preFallPhaseDetected(accelerometerDatas);


                System.out.println(accelerometerDatas);



                boolean fallDetected = false;
                boolean fallDetectedAgain = false;
                if (preFallAcceleration != -1.0) {
                    remainingAccelerometerDatas = new LinkedList<>();
                    remainingAccelerometerDatas.addAll(accelerometerDatas);
                    fallDetected = fallDetected(accelerometerDatas, preFallAcceleration);
                    if (fallDetected) {

                        System.out.println("DANGER");
                        fallCertainty = 1;
                        fallDetectedAgain=afterFallPhaseDetected(accelerometerDatas);
                        if(fallDetectedAgain)
                        {
                            System.out.println("SURE FALL");
                            fallCertainty = 2;
                            continue;
                        }
                    }
                    accelerometerDatas = new LinkedList<>();
                    accelerometerDatas.addAll(remainingAccelerometerDatas);
                }
                else
                    break;

            }
            System.out.println(accelerometerDatas);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return fallCertainty;

    }

    private static double preFallPhaseDetected(List<AccelerometerStats> accelerometerData)
    {
        for (int i=0; i < accelerometerData.size(); i++) {
            AccelerometerStats accelerometerData1 = accelerometerData.get(i);
            double squareSum = Math.sqrt(Math.pow(Double.parseDouble(accelerometerData1.getX()), 2) + Math.pow(Double.parseDouble(accelerometerData1.getY()), 2) + Math.pow(Double.parseDouble(accelerometerData1.getZ()), 2));
            System.out.println(squareSum);
            if (squareSum < LOWER_THRESHOLD) {
                // Possible fall
                System.out.println("possible fall here");
                accelerometerDatas = accelerometerData.subList(i + 1, accelerometerData.size());
                return squareSum;
            }
        }
        return -1.0;
    }

    private static boolean fallDetected(List<AccelerometerStats> accelerometerData, double preFallAcceleration)
    {
        int count=0;

        for (int i=0; i < accelerometerData.size(); i++) {
            if(count==MAX_TRIES)
                break;
            AccelerometerStats accelerometerData1 = accelerometerData.get(i);
            double squareSum = Math.sqrt(Math.pow(Double.parseDouble(accelerometerData1.getX()), 2) + Math.pow(Double.parseDouble(accelerometerData1.getY()), 2) + Math.pow(Double.parseDouble(accelerometerData1.getZ()), 2));
            System.out.println("DANGER?? " + (squareSum-preFallAcceleration));
            if (squareSum - preFallAcceleration > MAX_DIFFERENCE) {
                accelerometerDatas = accelerometerData.subList(i + 1, accelerometerData.size());
                return true;
            }
            count++;
        }
        return false;
    }

    private static boolean afterFallPhaseDetected(List<AccelerometerStats> accelerometerData)
    {
        int count = 0;
        int tries = 0;
        for (int i=0; i < accelerometerData.size(); i++) {
            tries ++;
            if(tries>MAX_TRIES_AFTER_FALL)
                return false;
            AccelerometerStats accelerometerData1 = accelerometerData.get(i);
            double squareSum = Math.sqrt(Math.pow(Double.parseDouble(accelerometerData1.getX()), 2) + Math.pow(Double.parseDouble(accelerometerData1.getY()), 2) + Math.pow(Double.parseDouble(accelerometerData1.getZ()), 2));
            System.out.println(squareSum);
            if (squareSum > 0.8*G && squareSum < 1.2*G) {
                count++;
                accelerometerDatas = accelerometerData.subList(i + 1, accelerometerData.size());
            }
            else
            {
                count=0;
            }
            if(count==MAX_STABLE_INTERVAL)
                return true;
        }
        return false;
    }

}

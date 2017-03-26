package gr.uoa.di.utils;

import gr.uoa.di.entities.AccelerometerStats;
import gr.uoa.di.entities.User;
import gr.uoa.di.messaging.GuardianNotification;
import gr.uoa.di.repository.AccelerometerStatsRepository;
import gr.uoa.di.repository.ElderlyResponsibleRepository;
import gr.uoa.di.repository.UserRepository;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by skand on 3/26/2017.
 */
@Component
public class FallDetectionUtil {

    private final GuardianNotification guardianNotification = new GuardianNotification();

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
    private static int LAST_X_MINUTES = 2;

    public FallDetectionUtil() throws IOException {
    }

    @ApiOperation(value = "Runs fall detection algorithm", tags = "Fall Detection")
    @RequestMapping(value = "/fallDetection/{username}",method = RequestMethod.POST, produces="application/json")
    public int startFallDetection(@PathVariable(value="username") String username) {

        System.out.println("------------------------");
        System.out.println("FALL DETECTION RUNNING");
        System.out.println("------------------------");
        int fallCertainty = 0;

        try {

            Date dNow = new Date(); // Instantiate a Date object
            Calendar cal = Calendar.getInstance();
            cal.setTime(dNow);
            cal.add(Calendar.MINUTE, -LAST_X_MINUTES);
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
//            System.out.println(cal.getTime());
            dNow = cal.getTime();
            String timestamp = format1.format(dNow);
            accelerometerDatas = accelerometerStatsRepository.findByTimeStamp(timestamp, username);


            LinkedList<AccelerometerStats> remainingAccelerometerDatas = new LinkedList<>();

            while(accelerometerDatas!=null && accelerometerDatas.size()>1)

            {
//                System.out.println(accelerometerDatas);

                Double preFallAcceleration = preFallPhaseDetected(accelerometerDatas);

//                System.out.println(accelerometerDatas);

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
                            break;
                        }
                    }
                    accelerometerDatas = new LinkedList<>();
                    accelerometerDatas.addAll(remainingAccelerometerDatas);
                }
                else
                    break;

            }
            User elderly = userRepository.findByUsername(username);
            User guardian = userRepository.findByUsername(elderly.getResponsibleUserName());

            if(fallCertainty==1)
            {
                try {
                    guardianNotification.sendAndroidNotification(guardian.getToken(),"Possible fall for "+elderly.getUsername(),"Possible danger");
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            else if(fallCertainty==2)
            {
                try {
                    guardianNotification.sendAndroidNotification(guardian.getToken(),"Sure fall for "+elderly.getUsername()+"!!!Please check on him/her!!!","DANGER!!!");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
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
//            System.out.println(squareSum);
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
//            System.out.println("DANGER?? " + (squareSum-preFallAcceleration));
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
//            System.out.println(squareSum);
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

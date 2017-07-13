package gr.uoa.di.utils;

import gr.uoa.di.entities.*;
import gr.uoa.di.messaging.GuardianNotification;
import gr.uoa.di.repository.*;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class FallDetectionUtil {

    private final GuardianNotification guardianNotification = new GuardianNotification();

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ElderlyResponsibleRepository elderlyResponsibleRepository;
    @Autowired
    private AccelerometerStatsRepository accelerometerStatsRepository;
    @Autowired
    private UserInZoneRepository userInZoneRepository;
    @Autowired
    private RecognizedActivityStorageRepository recognizedActivityStorageRepository;

    private static List<AccelerometerStats> accelerometerDatas = new LinkedList<AccelerometerStats>();
    private static Date impactTimestamp;
    private static double G = 9.81;
    private static double LOWER_THRESHOLD = 0.5*G;
    private static double MAX_DIFFERENCE = 2.5*G;
    private static int MAX_TIME_AFTER_FREE_FALL = 3;
    private static int MAX_TIME_AFTER_IMPACT = 5;
    private static int MAX_STABLE_INTERVAL = 5;
    private static int LAST_X_MINUTES = 2;
    private static int DEGREES_THRESHOLD = 35;
    private static double ACCELERATION_THRESHOLD_AFTER_FALL = 0.2;

    public FallDetectionUtil() throws IOException {
    }

    @ApiOperation(value = "Runs fall detection algorithm", tags = "Fall Detection")
    @RequestMapping(value = "/fallDetection/{username}",method = RequestMethod.POST, produces="application/json")
    public int startFallDetection(@PathVariable(value="username") String username) {

        System.out.println("------------------------");
        System.out.println("FALL DETECTION RUNNING");
        System.out.println("------------------------");
        int fallCertainty = 0;
        boolean safeZone = false;

        try {
            User elderly = userRepository.findByUsername(username);
            List<UserInZone> userInZones = userInZoneRepository.getCurrentZoneForUser(elderly.getUserId());
            if(userInZones!=null && userInZones.size()>0 && userInZones.get(0).getZone()!=null) {
                if (userInZones.get(0).getZone().getIsSafe() == 1) {
                    safeZone = true;
                    MAX_DIFFERENCE = 2.7*G;
                } else {
                    MAX_DIFFERENCE = 2.3*G;
                }
            }
            System.out.println("final value of MAX_DIFFERENCE: " + MAX_DIFFERENCE);
            Date dNow = new Date(); // Instantiate a Date object
            Calendar cal = Calendar.getInstance();
            cal.setTime(dNow);
            cal.add(Calendar.MINUTE, -LAST_X_MINUTES);
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
//            System.out.println(cal.getTime());
            dNow = cal.getTime();
            String timestamp = format1.format(dNow);
            accelerometerStatsRepository.deleteOldData(timestamp);
            accelerometerDatas = accelerometerStatsRepository.findByTimeStamp(timestamp, username);

            if(accelerometerDatas!=null && accelerometerDatas.size()>0) {
                System.out.println("Started from id: " + accelerometerDatas.get(0).getId() + " until id: " + accelerometerDatas.get(accelerometerDatas.size() - 1).getId());
                System.out.println("Started from timestamp: " + accelerometerDatas.get(0).getTimeStamp() + " until timestamp: " + accelerometerDatas.get(accelerometerDatas.size() - 1).getTimeStamp());
            }
            LinkedList<AccelerometerStats> remainingAccelerometerDatas = new LinkedList<>();
            while(accelerometerDatas!=null && accelerometerDatas.size()>1)
            {
                Double preFallAcceleration = preFallPhaseDetected(accelerometerDatas);


                boolean fallDetected = false;
                boolean fallDetectedAgain = false;
                if (preFallAcceleration != -1.0) {
                    remainingAccelerometerDatas = new LinkedList<>();
                    remainingAccelerometerDatas.addAll(accelerometerDatas);
                    fallDetected = fallDetected(accelerometerDatas, preFallAcceleration);
                    if (fallDetected) {
                        fallCertainty = 1;
                        fallDetectedAgain=afterFallPhaseDetected(accelerometerDatas);
                        if(fallDetectedAgain)
                        {
                            System.out.println("SureFallDetected");
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

            if(fallCertainty!=0) {
                Long guardianId = elderlyResponsibleRepository.findAssociatedGuardian(elderly.getUserId());
                User guardian = userRepository.findOne(guardianId);

                Timer timer = new Timer();


                int finalFallCertainty = fallCertainty;
                boolean finalSafeZone = safeZone;
                TimerTask delayedThreadStartTask = new TimerTask() {
                    @Override
                    public void run() {

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {

                                    String fallConfidence = "Possible fall.";
                                    if (finalFallCertainty == 2)
                                        fallConfidence = "Sure fall.";

                                    System.out.println("Fall confidence: " + fallConfidence);
                                    List<RecognizedActivityStorage> recognizedActivitiesBefore = recognizedActivityStorageRepository.getActivityBeforeImpact(elderly.getUserId(), impactTimestamp);
                                    List<RecognizedActivityStorage> recognizedActivitiesAfter = recognizedActivityStorageRepository.getActivityAfterImpact(elderly.getUserId(), impactTimestamp);

                                    Boolean conscious = null;
                                    Boolean fromActivity = null;

                                    if (recognizedActivitiesBefore != null && recognizedActivitiesBefore.size() > 0 &&
                                            recognizedActivitiesAfter != null && recognizedActivitiesAfter.size() > 0) {
                                        System.out.println("Activity before: " + recognizedActivitiesBefore.get(0).getState());
                                        System.out.println("Activity after: " + recognizedActivitiesAfter.get(0).getState());

                                        String activityBefore = recognizedActivitiesBefore.get(0).getState();
                                        String activityAfter = recognizedActivitiesAfter.get(0).getState();
                                        if (!activityBefore.equals("still") && activityAfter.equals("still")) {
                                            conscious = false;
                                            fromActivity = true;
                                        } else if (activityBefore.equals("still") && activityAfter.equals("still")) {
                                            conscious = false;
                                            fromActivity = false;
                                        } else if (!activityBefore.equals("still") && !activityAfter.equals("still")) {
                                            conscious = true;
                                            fromActivity = true;
                                        } else if (activityBefore.equals("still") && !activityAfter.equals("still")) {
                                            conscious = true;
                                            fromActivity = false;
                                        }
                                    }
                                    List<UserInZone> userInZones = userInZoneRepository.getZonesAfterImpact(elderly.getUserId(), impactTimestamp);
                                    String consciousString = "", fromActivityString = "";
                                    if (conscious != null)
                                        if (conscious)
                                            consciousString = " Probably conscious";
                                        else
                                            consciousString = " Probably unconscious";
                                    if (fromActivity != null)
                                        if (fromActivity)
                                            fromActivityString = " and while light activity.";
                                        else
                                            fromActivityString = " and while sitting/sleeping.";

                                    // don't send notification in case of "possible fall" && safe zone
                                    if(!fallConfidence.equals("Possible fall.") || !finalSafeZone ) {
                                        if (userInZones != null && userInZones.size() > 0) {
                                            guardianNotification.sendAndroidNotification(guardian.getToken(), fallConfidence + consciousString + fromActivityString + " for " + elderly.getUsername() + " in zone: saloni", fallConfidence);
                                            System.out.println(fallConfidence + consciousString + fromActivityString + " for " + elderly.getUsername() + " in zone: " + (userInZones.get(0).getZone()!=null?userInZones.get(0).getZone().getFriendlyName():"unknown"));
                                        } else {
                                            guardianNotification.sendAndroidNotification(guardian.getToken(), fallConfidence + consciousString + fromActivityString + " for " + elderly.getUsername() + " in zone: saloni", fallConfidence);
                                            System.out.println(fallConfidence + consciousString + fromActivityString + " for " + elderly.getUsername() + " in zone: unknown");
                                        }
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                };

                timer.schedule(delayedThreadStartTask, 60 * 1000); //1 minute
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
                 System.out.println("PrefallPhaseDetected: Acceleration: "+squareSum/G+"G");
                accelerometerDatas = accelerometerData.subList(i + 1, accelerometerData.size());
                return squareSum;
            }
        }
        return -1.0;
    }

    private boolean fallDetected(List<AccelerometerStats> accelerometerData, double preFallAcceleration)
    {
        Date firstTimestamp = null;
        String firstTimestampString = accelerometerData.get(0).getTimeStamp();
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            firstTimestamp = format1.parse(firstTimestampString);
            System.out.println("Measuring from firstTimestamp : " + firstTimestamp.toString());

        } catch (ParseException e) {
            e.printStackTrace();
        }

        double maxImpact =0.0;
        for (int i=0; i < accelerometerData.size(); i++) {
            Date thisTimestamp;
            String thisTimestampString = accelerometerData.get(i).getTimeStamp();
            try {
                thisTimestamp = format1.parse(thisTimestampString);
                if((thisTimestamp.getTime()-firstTimestamp.getTime())/1000 > MAX_TIME_AFTER_FREE_FALL)
                    break;

            } catch (ParseException e) {
                e.printStackTrace();
            }
            AccelerometerStats accelerometerData1 = accelerometerData.get(i);
            double squareSum = Math.sqrt(Math.pow(Double.parseDouble(accelerometerData1.getX()), 2) + Math.pow(Double.parseDouble(accelerometerData1.getY()), 2) + Math.pow(Double.parseDouble(accelerometerData1.getZ()), 2));

//            System.out.println("Impact detected: Acceleration "+(squareSum-preFallAcceleration)/G+"G");
            if (maxImpact<((squareSum-preFallAcceleration)/G))
                maxImpact= ((squareSum-preFallAcceleration)/G);
            if (squareSum - preFallAcceleration > MAX_DIFFERENCE) {
                String timestampString = accelerometerData1.getTimeStamp();
                try {
                    impactTimestamp = format1.parse(timestampString);
                    System.out.println("Above threshold Impact detected : Acceleration "+maxImpact+"G at timestamp: "+impactTimestamp);

                } catch (ParseException e) {
                    e.printStackTrace();
                }

                accelerometerDatas = accelerometerData.subList(i + 1, accelerometerData.size());
                return true;
            }
        }
        System.out.println("Below threshold Impact detected : Acceleration "+maxImpact+"G");
        return false;
    }

    private static boolean afterFallPhaseDetected(List<AccelerometerStats> accelerometerData)
    {
        int count = 0, count2=0;
        Date firstTimestamp = null;
        String firstTimestampString = accelerometerData.get(0).getTimeStamp();
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            firstTimestamp = format1.parse(firstTimestampString);
            System.out.println("Measuring from firstTimestamp : " + firstTimestamp.toString());

        } catch (ParseException e) {
            e.printStackTrace();
        }

        for (int i=0; i < accelerometerData.size(); i++) {
            Date thisTimestamp;
            String thisTimestampString = accelerometerData.get(i).getTimeStamp();
            try {
                thisTimestamp = format1.parse(thisTimestampString);
                if((thisTimestamp.getTime()-firstTimestamp.getTime())/1000 > MAX_TIME_AFTER_IMPACT)
                    break;

            } catch (ParseException e) {
                e.printStackTrace();
            }
            AccelerometerStats accelerometerData1 = accelerometerData.get(i);
            double squareSum = Math.sqrt(Math.pow(Double.parseDouble(accelerometerData1.getX()), 2) + Math.pow(Double.parseDouble(accelerometerData1.getY()), 2) + Math.pow(Double.parseDouble(accelerometerData1.getZ()), 2));
//            System.out.println(squareSum);

            double cosf = Double.parseDouble(accelerometerData1.getY()) / Math.sqrt(Math.pow(Double.parseDouble(accelerometerData1.getX()),2)+Math.pow(Double.parseDouble(accelerometerData1.getY()),2)+Math.pow(Double.parseDouble(accelerometerData1.getZ()),2));

            double f = Math.acos(cosf);

            double angleDegrees  = f * (180 / Math.PI);

            if (squareSum > (1-ACCELERATION_THRESHOLD_AFTER_FALL)*G && squareSum < (1+ACCELERATION_THRESHOLD_AFTER_FALL)*G) {
                count++;
                accelerometerDatas = accelerometerData.subList(i + 1, accelerometerData.size());
            }
            else
            {
                count=0;
            }

            if(angleDegrees > DEGREES_THRESHOLD || angleDegrees < -DEGREES_THRESHOLD)
                count2++;
            else
            {
                count2=0;
            }
            System.out.println("AfterFallPhase: angleDegrees: "+angleDegrees+" degrees, acceleration: "+ squareSum/G+"G");

            if(count>=MAX_STABLE_INTERVAL && count2>=MAX_STABLE_INTERVAL) {
                return true;
            }
        }
        return false;
    }

}

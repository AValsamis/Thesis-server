package gr.uoa.di;

import gr.uoa.di.entities.AccelerometerStats;
import gr.uoa.di.entities.User;
import gr.uoa.di.repository.AccelerometerStatsRepository;
import gr.uoa.di.repository.WifiInZoneRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

public class FallDetection {

    private static double G = 9.81;
    private static double LOWER_THRESHOLD = 0.4*G;
    private static double MAX_DIFFERENCE = 2.5*G;
    private static int MAX_TRIES = 10;
    private static int MAX_TRIES_AFTER_FALL = 20;
    private static int MAX_STABLE_INTERVAL = 5;

    private static List<AccelerometerStats> accelerometerDatas = new LinkedList<AccelerometerStats>();
//    @Autowired
//    private static AccelerometerStatsRepository accelerometerStatsRepository;

    public static void main(String[] args){

        try {
//            accelerometerDatas = accelerometerStatsRepository.findByTimeStamp("2017-01-15 17:08:00.000");
            Class.forName("com.mysql.jdbc.Driver") ;
            Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/diplomatikh", "root", "5576@@@@") ;
            Statement stmt = conn.createStatement() ;
            String query = "select s.x,s.y,s.z from accelerometer_stats s where s.user_id = '1'" ;
            ResultSet rs = stmt.executeQuery(query) ;

            while(rs.next()) {
                AccelerometerStats accelerometerStats = new AccelerometerStats();
                accelerometerStats.setX(rs.getString(1));
                accelerometerStats.setY(rs.getString(2));
                accelerometerStats.setZ(rs.getString(3));
                accelerometerDatas.add(accelerometerStats);
            }
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
                        fallDetectedAgain=afterFallPhaseDetected(accelerometerDatas);
                        if(fallDetectedAgain)
                        {
                            System.out.println("SURE FALL");
                            continue;
                        }
                    }
                    accelerometerDatas = new LinkedList<>();
                    accelerometerDatas.addAll(remainingAccelerometerDatas);
                }
                else
                    break;

            }
//            System.out.println(preFallPhaseDetected(accelerometerDatas));
            System.out.println(accelerometerDatas);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
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

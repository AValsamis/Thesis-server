package gr.uoa.di.messaging;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

@Component
public class GuardianNotification {

    private String AUTH_KEY_FCM = "";
    private final static String API_URL_FCM = "https://fcm.googleapis.com/fcm/send";

    public GuardianNotification() throws IOException {
        InputStream inputStream;
        Properties prop = new Properties();
        String propFileName = "keys.properties";

        inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);

        if (inputStream != null) {
            prop.load(inputStream);
        } else {
            try {
                throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        AUTH_KEY_FCM= prop.getProperty("AUTH_KEY_FCM");

    }

    public Boolean sendAndroidNotification(String deviceToken, String message, String title) throws IOException {

            Boolean result;
            URL url = new URL(API_URL_FCM);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setUseCaches(false);
            conn.setDoInput(true);
            conn.setDoOutput(true);

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "key=" + AUTH_KEY_FCM);
            conn.setRequestProperty("Content-Type", "application/json");

            JSONObject json = new JSONObject();

            json.put("to", deviceToken.trim());
            JSONObject info = new JSONObject();
            info.put("title", title); // Notification title
            info.put("body", message); // Notification
            // body
            json.put("data", info);
            try {
                OutputStreamWriter wr = new OutputStreamWriter(
                        conn.getOutputStream());
                wr.write(json.toString());
                wr.flush();

                BufferedReader br = new BufferedReader(new InputStreamReader(
                        (conn.getInputStream())));

                String output;
                System.out.println("Output from Server .... \n");
                while ((output = br.readLine()) != null) {
                    System.out.println(output);
                }
                result = true;
            } catch (Exception e) {
                e.printStackTrace();
                result = false;
            }
            System.out.println("GCM Notification is sent successfully");
            return result;
    }
}

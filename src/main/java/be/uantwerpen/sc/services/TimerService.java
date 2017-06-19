package be.uantwerpen.sc.services;

import be.uantwerpen.sc.controllers.BotController;
import be.uantwerpen.sc.models.Bot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Timer;

/**
 * Created by Dries on 10-6-2017.
 */
@Service
public class TimerService implements Runnable {

    public TimerService(){

    }

    @Override
    public void run() {

        while (true) {
            long startTime = System.currentTimeMillis();
            long elapsedTime = 0L;
            while (elapsedTime < 24*60*60*1000) {
                elapsedTime = (new Date()).getTime() - startTime;
            }

            String data = "";
            try {
                URL url = new URL("http://143.129.39.151:8083/bot/checkTimer");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");

                if (conn.getResponseCode() != 200) {
                    throw new RuntimeException("Failed : HTTP error code : "
                            + conn.getResponseCode());
                }
                conn.disconnect();

            } catch (MalformedURLException e) {

                e.printStackTrace();

            } catch (IOException e) {

                e.printStackTrace();

            }
        }
    }
}

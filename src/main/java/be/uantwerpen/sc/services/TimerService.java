package be.uantwerpen.sc.services;

import be.uantwerpen.sc.controllers.BotController;
import be.uantwerpen.sc.models.Bot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
 * Runnable Timer Service
 * Checks if bot is alive every day
 * TODO: Multiple Bots?
 * TODO: Daily? Too long?
 */
@Service
public class TimerService implements Runnable {
    /**
     * Autowired Bot Controller
     */
    @Autowired
    private BotController botController;

    /**
     * Own Port
     */
    @Value("${server.port:default}")
    String port;
    @Override
    public void run() {

        while (true) {
            long startTime = System.currentTimeMillis();
            long elapsedTime = 0L;
            //Wait 1 minute
            while (elapsedTime < 6*1000) {
                elapsedTime = (new Date()).getTime() - startTime;
            }
          //  botController.checkTimer();
/*
            try {
                URL url = new URL("http://localhost:"+port+"/bot/checkTimer");
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
            */
        }
    }
}

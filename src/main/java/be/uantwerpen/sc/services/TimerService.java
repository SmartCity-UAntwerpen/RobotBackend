package be.uantwerpen.sc.services;

import be.uantwerpen.sc.controllers.BotController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Runnable Timer Service
 * Checks if bot is alive every minute
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
            while (elapsedTime < 60 * 1000) {
                elapsedTime = (new Date()).getTime() - startTime;
            }
            botController.checkTimer();
        }
    }
}

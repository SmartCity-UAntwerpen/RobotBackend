package be.uantwerpen.sc.services;

import be.uantwerpen.sc.controllers.BotController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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

        final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(() -> botController.checkTimer(), 0, 60, TimeUnit.SECONDS);
    }
}

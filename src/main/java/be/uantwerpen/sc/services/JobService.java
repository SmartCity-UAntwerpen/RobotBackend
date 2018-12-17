package be.uantwerpen.sc.services;

import be.uantwerpen.rc.models.Bot;
import be.uantwerpen.sc.controllers.mqtt.MqttJobPublisher;
import be.uantwerpen.rc.models.Job;
import be.uantwerpen.sc.services.newMap.PointControlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.awt.*;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Job Service
 */
@Service
public class JobService implements Runnable
{
    /**
     * Autowired MQTT Publisher
     */
    @Autowired
    MqttJobPublisher mqttJobPublisher;


    /**
     * Autowired Botcontrol Service
     */
    @Autowired
    private BotControlService botControlService;

    /**
     * Autowired Pathplannig service
     */
    @Autowired
    private PathPlanningService pathPlanningService;

    /**
     * Autowired PointControlService service
     */
    @Autowired
    private PointControlService pointControlService;

    private BlockingQueue<Job> jobQueue = null;

    @PostConstruct
    public void init() {
        jobQueue = new ArrayBlockingQueue<Job>(100);
    }


    /**
     * Send job over MQTT
     * @param botId ID of bot for job
     * @param jobId ID of job for bot
     * @param idStart ID start Point
     * @param idStop ID stop Point
     * @return Success
     */
    private boolean sendJob(Long jobId, Long botId, long idStart, long idStop)
    {
        Job job = new Job(jobId);
        job.setIdStart(idStart);
        job.setIdEnd(idStop);
        job.setIdVehicle(botId);

        return mqttJobPublisher.publishJob(job, botId);
    }

    /**
     * Adds a job to the blocking queue
     * @param jobId ID of the job
     * @param idStart ID of starting point
     * @param idStop ID of end point
     * @return
     */
    public boolean queueJob(Long jobId, long idStart, long idStop){
        //Check if points exist
        try {
            pointControlService.getPoint(idStart);
        }catch (Exception e){
            return false;
        }
        try {
            pointControlService.getPoint(idStop);
        }catch (Exception e){
            return false;
        }

        //Create new job and add to queue
        Job job = new Job(jobId);
        job.setIdStart(idStart);
        job.setIdEnd(idStop);
        try{
            boolean tmp = jobQueue.add(job);
            return true;
        }catch(IllegalStateException e){
            System.err.println("Error adding job to job queue!");
            return false;
        }
    }

    @Override
    public void run() {
        if(jobQueue == null){
            return;
        }

        System.out.println("Starting Job Service...");

        while(true){
            //Process that checks the queue and seeks a bot that can execute the job
            try{
                Job job = jobQueue.take();
                if(!botControlService.getAllAvialableBots().isEmpty()){
                    //Find closest bot
                    List<Bot> bots = botControlService.getAllAvialableBots();
                    TreeMap<Integer,Bot> sortedBots = new TreeMap<>();
                    for (Bot b: bots) {
                        sortedBots.put((int)pathPlanningService.CalculatePathWeight(b.getPoint().getId().intValue(),job.getIdStart().intValue()),b);
                    }

                    //Get closest bot == first entry and assign job
                    Bot bot = sortedBots.firstEntry().getValue();
                    bot.setBusy(true);
                    bot.setIdStart(job.getIdStart());
                    bot.setIdStop(job.getIdEnd());
                    bot.setJobId(job.getJobId());
                    job.setIdVehicle(bot.getIdCore());
                    botControlService.saveBot(bot);
                    //Send MQTT message to bot
                    this.sendJob(job.getJobId(),bot.getIdCore(),job.getIdStart(),job.getIdEnd());
                }else{
                    //Place job back in queue TODO: optimize this (remove this else)
                    jobQueue.put(job);
                }
            }catch(Exception e){
                System.err.println("Error taking job from queue: " +e.getMessage());
            }

        }

    }
}

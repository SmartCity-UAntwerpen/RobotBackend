package be.uantwerpen.sc.services;

import be.uantwerpen.rc.models.Bot;
import be.uantwerpen.sc.controllers.mqtt.MqttJobPublisher;
import be.uantwerpen.rc.models.Job;
import be.uantwerpen.sc.repositories.newMap.JobRepository;
import be.uantwerpen.sc.services.newMap.PointControlService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.awt.*;
import java.util.HashMap;
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
     * Autowired Job repository
     */
    @Autowired
     JobRepository jobs;

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

    //TODO: remove blocking queue ==> use database
    private BlockingQueue<Job> jobQueue = null;

    Logger logger = LoggerFactory.getLogger(JobService.class);

    @PostConstruct
    public void init() {
        //Initialize job queue and add all jobs from database
        jobQueue = new ArrayBlockingQueue<Job>(100);
        List<Job> allJobs = jobs.findAll();
        jobQueue.addAll(allJobs);
    }


    /**
     * Send job over MQTT
     * @param bot The bot that executes the job
     * @param jobId ID of job for bot
     * @param idStart ID start Point
     * @param idStop ID stop Point
     * @return Success
     */
    private boolean sendJob(Long jobId, Bot bot, long idStart, long idStop)
    {
        Job job = new Job(jobId);
        job.setIdStart(idStart);
        job.setIdEnd(idStop);
        job.setBot(bot);
        return mqttJobPublisher.publishJob(job, bot.getIdCore());
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
        if(jobId == null){
            jobId = 9999L;
        }
        Job job = new Job(jobId);
        job.setIdStart(idStart);
        job.setIdEnd(idStop);
        try{
            boolean tmp = jobQueue.add(job);
            jobs.save(job);
            logger.info("New job queued!\tId: "+job.getJobId()+"\tStart: "+job.getIdStart()+"\tEnd: "+job.getIdEnd());
            return true;
        }catch(IllegalStateException e){
            logger.error("Error adding job to job queue!");
            return false;
        }
    }

    @Override
    public void run() {
        if(jobQueue == null){
            return;
        }

        logger.info("Starting Job Service...");

        while(true){
            //Process that checks the queue and seeks a bot that can execute the job
            try{
                Job job = jobQueue.take();
                if(!botControlService.getAllAvialableBots().isEmpty()){
                    //Find closest bot
                    List<Bot> bots = botControlService.getAllAvialableBots();
                    TreeMap<Integer,Bot> sortedBots = new TreeMap<>();
                    for (Bot b: bots) {
                        int targetId = -1;
                        //Depending on the type of job, calculate how far the bot is
                        if(job.getIdStart() == -1L){
                            //Go to point job ==> which bot is closest to end
                            targetId = job.getIdEnd().intValue();
                        }else{
                            //Normal job ==> which bot is closest to start point
                            targetId = job.getIdStart().intValue();
                        }
                        sortedBots.put((int)pathPlanningService.CalculatePathWeight(b.getPoint().intValue(),targetId),b);
                    }

                    //Get closest bot == first entry and assign job
                    Bot bot = sortedBots.firstEntry().getValue();
                    //If the start point is -1L than this is a goToPoint job ==> set start point to current location of the bot
                    if(job.getIdStart() == -1L){
                        job.setIdStart(bot.getPoint());
                    }
                    bot.setBusy(true);
                    bot.setIdStart(job.getIdStart());
                    bot.setIdStop(job.getIdEnd());
                    bot.setJobId(job.getJobId());
                    job.setBot(bot);
                    botControlService.saveBot(bot);
                    jobs.save(job);
                    //Send MQTT message to bot
                    this.sendJob(job.getJobId(),bot,job.getIdStart(),job.getIdEnd());
                }else{
                    //Place job back in queue TODO: optimize this (remove this else)
                    jobQueue.put(job);
                }
            }catch(Exception e){
                logger.error("Error taking job from queue: " +e.getMessage());
            }

        }

    }
}

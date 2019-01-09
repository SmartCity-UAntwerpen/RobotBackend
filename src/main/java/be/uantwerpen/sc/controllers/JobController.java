package be.uantwerpen.sc.controllers;

import be.uantwerpen.rc.models.Bot;
import be.uantwerpen.rc.models.BotState;
import be.uantwerpen.rc.models.Job;
import be.uantwerpen.sc.repositories.JobRepository;
import be.uantwerpen.sc.services.*;
import be.uantwerpen.sc.services.PointControlService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author  Niels on 4/05/2016.
 * @author Reinout
 * @author Dieter 2018-2019
 *
 * Job Controller
 */
@RestController
@RequestMapping("/job/")
public class JobController
{
    /**
     * Autowired Bot Control Service
     */
    @Autowired
    private BotControlService botControlService;

    /**
     * Autowired Job Service
     */
    @Autowired
    private JobControlService jobControlService;

    /**
     * Job Repository
     */
    @Autowired
    private JobRepository jobs;

    /**
     * Autowired Point Control Service
     */
    @Autowired
    private PointControlService pointControlService;

    /**
     * Maas IP
     */
    @Value("${backbone.ip:default}")
    private String backbone;

    /**
     * Maas Port
     */
    @Value("${backbone.ip.port:default}")
    private String backbonePort;

    private Logger logger = LoggerFactory.getLogger(JobController.class);

    /**
     * Queues a job in the job queue for the job service
     * @param idJob, job id from backbone
     * @param idstart, start point
     * @param idstop, end point
     * @return http status
     */
    @RequestMapping(value = "execute/{idStart}/{idStop}/{idJob}",method = RequestMethod.GET)
    public String executeJob(@PathVariable("idJob") long idJob, @PathVariable("idStart") long idstart, @PathVariable("idStop") long idstop)
    {
        if(!jobControlService.queueJob(idJob,idstart,idstop)){
            return "HTTP status : 400";
        }
        return "HTTP status : 200";
    }

    /**
     * Queues a job in the job queue for the job service
     * @param idJob, job id from backbone
     * @param idstart, start point
     * @param idstop, end point
     * @return http status
     */
    @RequestMapping(value = "execute/{idStart}/{idStop}/{idJob}",method = RequestMethod.POST)
    public String executeJobPOST(@PathVariable("idJob") long idJob, @PathVariable("idStart") long idstart, @PathVariable("idStop") long idstop)
    {
        if(!jobControlService.queueJob(idJob,idstart,idstop)){
            return "HTTP status : 400";
        }
        return "HTTP status : 200";
    }

    /**
     * Go to point command from backbone
     * @param pid, The target point id
     * @return http status
     */
    @RequestMapping(value = "gotopoint/{pid}",method = RequestMethod.GET)
    public String goToPoint(@PathVariable("pid") long pid)
    {
        if(!jobControlService.queueJob(null,-1L,pid)){
            return "HTTP status : 400";
        }
        return "HTTP status : 200";
    }

    /**
     *  Finished -> Robot sends to this end-point to notify it finished the job
     * @param robotId
     */
    @RequestMapping(value = "finished/{robotId}",method = RequestMethod.GET)
    public void finished(@PathVariable("robotId") long robotId)
    {
        try{
            Bot bot = botControlService.getBot( robotId);
            Job job = jobs.findOne(bot.getJobId());
            bot.setBusy(false);
            bot.setStatus(BotState.Alive.ordinal());
            botControlService.saveBot(bot);
            completeJob(bot.getJobId());
            logger.info("Job with id: "+job.getJobId() +" is done! Bot with id: "+bot.getIdCore() +" is available again!");
            jobs.delete(job.getJobId());
        }catch(NullPointerException e){
            logger.warn("Robot "+robotId +" has no job assigned! Nothing to complete!");
        }
    }

    /**
     * Get the progress of a job
     * @param jobid jobid
     */
    @RequestMapping(value = "getprogress/{jobid}",method = RequestMethod.GET)
    public int getProgress(@PathVariable("jobid") long jobid)
    {
        Job job = jobs.findOne(jobid);
        logger.info("Progress of job "+jobid+" requested");
        if(job == null){
            //If job not found return 100%
            return 100;
        }
        return 0; //TODO return actual progress
    }

    /**
     * Send vehicle close by to backbone
     * @param jobid
     */
    @RequestMapping(value = "closeBy/{jobid}", method = RequestMethod.GET)
    public void sendCloseBy(@PathVariable("jobid") long jobid){
        logger.info("Sending close by message for job "+jobid);
        try {
            String u = "http://"+backbone+":"+backbonePort+"/jobs/vehiclecloseby/" + jobid;
            URL url = new URL(u);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }
            conn.disconnect();

        } catch (IOException e) {
            logger.warn("Backbone not available! Job "+jobid+" cannot send closeby command to backbone.");
        }
    }

    /**
     * Notifies backbone that job is completed
     * @param id, the job id
     */
    private void completeJob(long id){
        logger.info("Sending complete message for job "+id);
        try {
            String u = "http://"+backbone+":"+backbonePort+"/jobs/complete/" + id;
            URL url = new URL(u);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }
            conn.disconnect();

        } catch (IOException e) {
            logger.warn("Backbone not available! Job "+id+" cannot send complete command to backbone.");
        }
    }
}

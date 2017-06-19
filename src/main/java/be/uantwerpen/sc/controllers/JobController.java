package be.uantwerpen.sc.controllers;

import be.uantwerpen.sc.models.Bot;
import be.uantwerpen.sc.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Niels on 4/05/2016.
 */
@RestController
@RequestMapping("/job/")
public class JobController
{
    @Autowired
    private PathPlanningService pathPlanningService;

    @Autowired
    private LinkControlService linkControlService;

    @Autowired
    private BotControlService botControlService;

    @Autowired
    private JobService jobService;

    @Autowired
    private PointControlService pointControlService;

    //NOG AANPASSEN
    String maasIp = "143.129.39.151";
    String maasPort = "8090";

    public void sendJob(String robotUri, String job)
    {
        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.postForObject(robotUri, job, String.class);
        System.out.println(result);
    }

    @RequestMapping(value = "executeJob/{idJob}/{idVehicle}/{idStart}/{idStop}",method = RequestMethod.GET)
    public String executeJob(@PathVariable("idJob") int idJob, @PathVariable("idVehicle") int idVehicle, @PathVariable("idStart") int idstart, @PathVariable("idStop") int idstop)
    {
        Bot b;
        try {
            b = botControlService.getBotWithCoreId((long) idVehicle);
        }catch(Exception e){
            return "HTTP status : 404";
        }
        if (b.getBusy()==1){
            return "HTTP status : 403";
        }
        try {
            pointControlService.getPoint((long) idstart);
        }catch (Exception e){
            return "HTTP status : 404";
        }
        try {
            pointControlService.getPoint((long) idstop);
        }catch (Exception e){
            return "HTTP status : 404";
        }

        b.setJobId((long) idJob);
        b.setBusy(1);
        b.setIdStart((long) idstart);
        b.setIdStop((long) idstop);
        botControlService.saveBot(b);

        jobService.sendJob((long) idVehicle,  (long) idJob, (long) idstart, (long) idstop);

        return "HTTP status : 200";
    }

    @RequestMapping(value = "finished/{robotId}",method = RequestMethod.GET)
    public void finished(@PathVariable("robotId") int robotId)
    {
        Bot bot = botControlService.getBotWithCoreId((long) robotId);
        completeJob(bot.getJobId());
    }

    public void completeJob(long id){
        try {
            // Ander ip adres en poort - Maas
            String u = "http://"+maasIp+":"+maasPort+"/completeJob/" + id;
            URL url = new URL(u);
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

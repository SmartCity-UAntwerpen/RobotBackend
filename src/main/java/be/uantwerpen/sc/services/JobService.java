package be.uantwerpen.sc.services;

import be.uantwerpen.sc.controllers.mqtt.MqttJobPublisher;
import be.uantwerpen.sc.models.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Thomas on 01/06/2016.
 */
@Service
public class JobService
{
    @Autowired
    MqttJobPublisher mqttJobPublisher;

    public boolean sendJob(Long botId, Long jobId, long idStart, long idStop)
    {
        Job job = new Job();
        job.setIdEnd(idStop);
        job.setIdStart(idStart);
        job.setIdVehicle(botId);
        job.setJobId(jobId);

        return mqttJobPublisher.publishJob(job, botId);
    }

}

package be.uantwerpen.sc.controllers.mqtt;

import be.uantwerpen.rc.models.Job;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Random;

/**
 * MQTT Job publisher
 */
@Service
public class MqttJobPublisher
{
    /**
     * MQTT IP, Default value got from config file
     */
    @Value("${mqtt.ip:localhost}")
    private String mqttIP;

    /**
     * MQTT Port, Default value got from config file
     */
    @Value("#{new Integer(${mqtt.port}) ?: 1883}")
    private int mqttPort;

    /**
     * MQTT Username, Default value got from config file
     */
    @Value("${mqtt.username:default}")
    private String mqttUsername;

    /**
     * MQTT PWD, Default value got from config file
     */
    @Value("${mqtt.password:default}")
    private String mqttPassword;

    private Logger logger = LoggerFactory.getLogger(MqttJobPublisher.class);

    /**
     * Publish Job over MQTT
     * @param job Job to publish
     * @param botID Target bot ID
     * @return Success
     */
    public boolean publishJob(Job job, long botID)
    {
        logger.info("Publishing Job");
        StringBuilder content  = new StringBuilder("Job:{jobId:"+job.getJobId().toString()+"/ botId:"+job.getBot().getIdCore().toString()+"/ idStart:"+job.getIdStart().toString()+"/ idEnd:"+job.getIdEnd().toString());
        if(job.getDriveDirections() != null && !job.getDriveDirections().isEmpty())
            content.append("/ driveDirections:"+job.getDriveDirections().toString() +"}"); //TODO: convert to proper sequence of strings
        else
            content.append("}");
        logger.info(content.toString());
        int qos         = 2;
        String topic    = "BOT/" + botID + "/Job";
        String broker   = "tcp://" + mqttIP + ":" + mqttPort;

        MemoryPersistence persistence = new MemoryPersistence();

        try
        {
            //Generate unique client ID
            MqttClient client = new MqttClient(broker, "Robot Backend Publisher" + new Random().nextLong(), persistence);
            MqttConnectOptions connectOptions = new MqttConnectOptions();
            connectOptions.setCleanSession(true);
            connectOptions.setUserName(mqttUsername);
            connectOptions.setPassword(mqttPassword.toCharArray());
            client.connect(connectOptions);
            MqttMessage message = new MqttMessage(content.toString().getBytes());
            message.setQos(qos);
            client.publish(topic, message);
            client.disconnect();
        }
        catch(MqttException e)
        {
            logger.error("Could not publish topic: " + topic + " to mqtt service!");
            logger.error("Reason: " + e.getReasonCode());
            logger.error("Message: " + e.getMessage());
            logger.error(e.getLocalizedMessage());
            logger.error("Cause: " + e.getCause());
            logger.error("Exception: " + e);

            return false;
        }

        return true;
    }
}

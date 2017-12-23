package be.uantwerpen.sc.controllers.mqtt;

import be.uantwerpen.sc.models.Job;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
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

    /**
     * Publish Job over MQTT
     * @param job Job to publish
     * @param botID Target bot ID
     * @return Success
     */
    public boolean publishJob(Job job, long botID)
    {
        System.out.println("Publishing Job");
        String content  = "Job:{jobId:"+job.getJobId().toString()+"/ botId:"+job.getIdVehicle().toString()+"/ idStart:"+job.getIdStart().toString()+"/ idEnd:"+job.getIdEnd().toString()+"}";
        System.out.println(content);
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
            MqttMessage message = new MqttMessage(content.getBytes());
            message.setQos(qos);
            client.publish(topic, message);
            client.disconnect();
        }
        catch(MqttException e)
        {
            System.err.println("Could not publish topic: " + topic + " to mqtt service!");
            System.err.println("Reason: " + e.getReasonCode());
            System.err.println("Message: " + e.getMessage());
            System.err.println(e.getLocalizedMessage());
            System.err.println("Cause: " + e.getCause());
            System.err.println("Exception: " + e);

            return false;
        }

        return true;
    }
}

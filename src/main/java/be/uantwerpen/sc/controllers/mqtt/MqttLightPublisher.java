package be.uantwerpen.sc.controllers.mqtt;

import be.uantwerpen.sc.models.TrafficLight;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Random;

/**
 * @author  Dries on 10-5-2017.
 * @author Reinout
 */
@Service
public class MqttLightPublisher
{
    /**
     * MQTT IP
     */
    @Value("${mqtt.ip:localhost}")
    private String mqttIP;

    /**
     * MQTT Port
     */
    @Value("#{new Integer(${mqtt.port}) ?: 1883}")
    private int mqttPort;

    /**
     * MQTT Username
     */
    @Value("${mqtt.username:default}")
    private String mqttUsername;

    /**
     * MQTT Pwd
     */
    @Value("${mqtt.password:default}")
    private String mqttPassword;

    /**
     * Fuck decent naming amirite
     * Publish Trafficlight over MQTT
     * @param light Trafficlight
     * @param tlID Traffic Light ID
     * @return
     */
    public boolean publishLight(TrafficLight light, long tlID)
    {
        String content  = "Light{id:"+tlID+"/ state:"+light.getState()+"}";
        int qos         = 2;
        String topic    = "LIGHT/" + tlID + "/Light";
        String broker   = "tcp://" + mqttIP + ":" + mqttPort;

        MemoryPersistence persistence = new MemoryPersistence();

        try
        {
            //Generate unique client ID
            MqttClient client = new MqttClient(broker, "SmartCity_Core_Publisher_" + new Random().nextLong(), persistence);
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

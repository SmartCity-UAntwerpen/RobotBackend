package be.uantwerpen.sc.controllers.mqtt;

import be.uantwerpen.sc.controllers.TrafficLightController;
import be.uantwerpen.sc.tools.Terminal;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Random;

/**
 * @author Dries on 10-5-2017.
 * @author Reinout
 * TODO: Check egality with Location Subscriber
 */
@Service
public class MqttLightSubscriber {


    /**
     * Autowired Traffic Light Controller
     */
    @Autowired
    private TrafficLightController trafficLightController;

    /**
     * Autowired Environment
     * TODO What Dis
     */
    @Autowired
    private Environment environment;

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
     * MQTT Subscriber
     */
    @Value("${mqtt.password:default}")
    private String mqttPassword;

    /**
     * MQTT Disabled Status
     */
    @Value("${mqtt.disabled:false}")
    private boolean mqttDisable;

    /**
     * MQTT Client
     */
    private MqttClient mqttSubscribeClient;

    @PostConstruct
    private void postConstruct()
    {
        //Check to disable MQTT service
        for(String profile : environment.getActiveProfiles())
        {
            if(profile.equals("dev") && mqttDisable)
            {
                //Disable MQTT
                Terminal.printTerminalInfo("MQTT will not be available! System will not be operational.");
                return;
            }
        }

        //IP / port-values are initialised at the end of the constructor
        String brokerURL = "tcp://" + mqttIP + ":" + mqttPort;

        Terminal.printTerminalInfo("Connecting to MQTT service");

        try
        {
            //Generate unique client ID
            mqttSubscribeClient = new MqttClient(brokerURL, "SmartCity Core Subscriber_" + new Random().nextLong());
        }
        catch(MqttException e)
        {
            Terminal.printTerminalError("Could not connect to MQTT Broker!");
            e.printStackTrace();

            Terminal.printTerminalError("System will exit!");
            System.exit(1);
        }

        try
        {
            start();
        }
        catch(Exception e)
        {
            Terminal.printTerminalError(e.getMessage());
            e.printStackTrace();

            boolean devMode = false;
            for(String profile : environment.getActiveProfiles())
            {
                if(profile.equals("dev"))
                {
                    devMode = true;
                }
            }

            if(devMode)
            {
                Terminal.printTerminalInfo("MQTT is not available! System will not be operational.");
            }
            else
            {
                Terminal.printTerminalError("System will exit!");
                System.exit(1);
            }
        }
    }

    public void updateLocation()
    {

    }

    private void start() throws Exception
    {
        try
        {
            mqttSubscribeClient.setCallback(new MqttLightSubscriberCallback(trafficLightController));
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            connOpts.setUserName(mqttUsername);
            connOpts.setPassword(mqttPassword.toCharArray());
            mqttSubscribeClient.connect(connOpts);
            //Subscribe to all subtopics of bots

            mqttSubscribeClient.subscribe("LIGHT/#");
        }
        catch(MqttException e)
        {
            System.out.println(e);
            throw new Exception("Could not subscribe to topics of MQTT service!");
        }
    }
}


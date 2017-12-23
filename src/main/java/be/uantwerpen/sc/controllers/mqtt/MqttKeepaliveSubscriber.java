package be.uantwerpen.sc.controllers.mqtt;

import be.uantwerpen.sc.services.BotControlService;
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
 * @author Reinout
 */
@Service
public class MqttKeepaliveSubscriber {


    /**
     * Autowired Traffic Light Controller
     */
    @Autowired
    private BotControlService botControlService;

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

    private void start() throws Exception
    {
        try
        {
            mqttSubscribeClient.setCallback(new MqttBotSubscriberCallback(botControlService));
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            connOpts.setUserName(mqttUsername);
            connOpts.setPassword(mqttPassword.toCharArray());
            mqttSubscribeClient.connect(connOpts);
            mqttSubscribeClient.subscribe("BOT/alive/#");
        }
        catch(MqttException e)
        {
            System.out.println(e);
            throw new Exception("Could not subscribe to Keepalive topic of MQTT service!");
        }
    }
}


package be.uantwerpen.sc.controllers.mqtt;

import be.uantwerpen.rc.models.Bot;
import be.uantwerpen.rc.models.BotState;
import be.uantwerpen.sc.controllers.BotController;
import be.uantwerpen.sc.controllers.TrafficLightController;
import be.uantwerpen.sc.services.BotControlService;
import be.uantwerpen.sc.tools.Terminal;
import org.eclipse.paho.client.mqttv3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Objects;
import java.util.Random;

@Service
public class MqttSubscriber implements MqttCallback {

    /**
     * Autowired Bot Controller
     */
    @Autowired
    private BotController botController;

    /**
     * Autowired BotControllerService
     */
    @Autowired
    private BotControlService botControlService;

    /**
     * Autowired Traffic Light Controller
     */
    @Autowired
    private TrafficLightController trafficLightController;

    /**
     * Autowired Environment
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

    Logger logger = LoggerFactory.getLogger(MqttSubscriber.class);


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
            mqttSubscribeClient.setCallback(this);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            connOpts.setUserName(mqttUsername);
            connOpts.setPassword(mqttPassword.toCharArray());
            mqttSubscribeClient.connect(connOpts);

           logger.info("Subscribing to topics...");

            //Subscribe to traffic light control topic

            mqttSubscribeClient.subscribe("BOT/#");
            mqttSubscribeClient.subscribe("LIGHT/#");
        }
        catch(MqttException e)
        {
            logger.error(e.toString());
            throw new Exception("Could not subscribe to topics of MQTT service!");
        }
    }


    @Override
    public void connectionLost(Throwable throwable) {

    }

    /**
     * Message arrived
     *
     * @param topic,       String of the topic received
     * @param mqttMessage, the message contents
     */
    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {


        //Location TOPIC
        //Topic: BOT/{id}/loc
        if(topic.matches("(BOT\\/[0-9]+\\/loc)")) {
            String botIDString = topic.split("/")[1];
            Long botID = Long.parseLong(botIDString);

            String payloadString = new String(mqttMessage.getPayload());
            logger.info("MQTT message for location received: "+payloadString);
            logger.info("ID :"+botID);

            if(!topic.endsWith("loc")){
                logger.warn("no location");
                return;
            }

            try
            {
                logger.info("MQTT LOCATION ARRIVED");
                String content=payloadString.split("\\{")[1];
                String temp = content.split("id:")[1];
                String id = temp.split("/")[0];
                temp = temp.split("vertex:")[1];
                String point = temp.split("/")[0];
                temp = temp.split("progress:")[1];
                String progress = temp.split("}")[0];
                int Id = Integer.parseInt(id);
                int pointId = Integer.parseInt(point);
                int Progress = Integer.parseInt(progress);
                botController.updateLocation((long) Id, (long) pointId, Progress);
            }
            catch(Exception e)
            {
                logger.error("Could not parse integer from payloadString: " + payloadString);
                e.printStackTrace();
            }
        }

        //Light TOPIC
        //Topic: LIGHT/{id}
        else if(topic.matches("(LIGHT\\/[0-9]+)")){
            logger.info("MQTT LIGHT ARRIVED");
            String lightIDString = topic.split("/")[1];
            Long lightID = Long.parseLong(lightIDString);

            String payloadString = new String(mqttMessage.getPayload());
            logger.info("LIGHT :"+payloadString);

            String temp = payloadString.split("id:")[1];
            String id = temp.split("/")[0];
            temp = temp.split("state:")[1];
            String state = temp.split("}")[0];

            if(Objects.equals(state, ""))
            {
                return;
            }
            trafficLightController.updateState(Integer.parseInt(id), state);
        }

        //Alive TOPIC
        //Topic: BOT/alive
        else if(topic.matches("(BOT/alive)")) {
            logger.info("MQTT KEEPALIVE MESSAGE ARRIVED");

            String payloadString = new String(mqttMessage.getPayload());
            logger.info("Bot: " +payloadString +" is alive");

            String temp = payloadString.split("botid:")[1];
            Long id = Long.parseLong(temp.split("/")[0]);

            Bot bot= botControlService.getBot(id);
            bot.updateStatus(BotState.Alive.ordinal());
            botControlService.saveBot(bot);
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }
}

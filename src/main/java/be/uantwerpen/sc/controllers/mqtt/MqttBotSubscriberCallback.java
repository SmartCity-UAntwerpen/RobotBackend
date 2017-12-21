package be.uantwerpen.sc.controllers.mqtt;

import be.uantwerpen.sc.models.Bot;
import be.uantwerpen.sc.models.BotState;
import be.uantwerpen.sc.services.BotControlService;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * @author  Dries on 10-5-2017.
 * @author Reinout
 */
public class MqttBotSubscriberCallback implements MqttCallback
{
    /**
     * bot Controller
     */
    BotControlService service;

    /**
     * Constructor
     * @param service
     */
    public MqttBotSubscriberCallback(BotControlService service)
    {
        this.service = service;
    }

    /**
     * TODO
     * @param cause
     */
    @Override
    public void connectionLost(Throwable cause)
    {
        //This is called when the connection is lost. We could reconnect here.
    }

    /**
     * Message arrived: Heartbeat
     * @param topic
     * @param mqttMessage
     * @throws Exception
     */
    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception
    {
        System.out.println("MQTT KeepAlive ARRIVED");

        String payloadString = new String(mqttMessage.getPayload());
        System.out.println("Bot :"+payloadString);

        String temp = payloadString.split("botid:")[1];
        Long id = Long.parseLong(temp.split("/")[0]);

        Bot bot=service.getBotWithCoreId(id);
        bot.updateStatus(BotState.Alive.ordinal());
        service.saveBot(bot);
    }

    /**
     * TODO
     * @param iMqttDeliveryToken
     */
    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken)
    {

    }
}

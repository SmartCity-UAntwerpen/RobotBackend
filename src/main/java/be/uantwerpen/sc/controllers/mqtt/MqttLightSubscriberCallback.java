package be.uantwerpen.sc.controllers.mqtt;

import be.uantwerpen.sc.controllers.BotController;
import be.uantwerpen.sc.controllers.TrafficLightController;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * @author  Dries on 10-5-2017.
 * @author Reinout
 */
public class MqttLightSubscriberCallback implements MqttCallback
{
    /**
     * TrafficLight Controller
     * TODO Why not autowired?
     */
    TrafficLightController trafficLightController;

    /**
     * Constructor
     * TODO why not autowired?
     * @param trafficLightController
     */
    public MqttLightSubscriberCallback(TrafficLightController trafficLightController)
    {
        this.trafficLightController = trafficLightController;
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
        System.out.println("MQTT LIGHT ARRIVED");
        String lightIDString = topic.split("/")[1];
        Long lightID = Long.parseLong(lightIDString);

        String payloadString = new String(mqttMessage.getPayload());
        System.out.println("LIGHT :"+payloadString);

        if(!topic.endsWith("Heartbeat")){
            return;
        }

        String temp = payloadString.split("id:")[1];
        String id = temp.split("/")[0];
        temp = temp.split("state:")[1];
        String state = temp.split("}")[0];

        if(state == "")
        {
            return;
        }
        trafficLightController.updateState(Integer.parseInt(id), state);
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

package be.uantwerpen.sc.services;

import be.uantwerpen.rc.models.map.Point;
import be.uantwerpen.sc.controllers.mqtt.MqttLightPublisher;
import be.uantwerpen.rc.models.TrafficLight;
import be.uantwerpen.sc.repositories.TrafficLightRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Control Service For Traffic Light
 * TODO: Comments, Function
 */
@Service
public class TrafficLightControlService
{

    /**
     * Autowired TrafficLightRepo
     */
    @Autowired
    private TrafficLightRepository trafficLightRepository;

    /**
     * Autowired Mqtt Publisher
     */
    @Autowired
    MqttLightPublisher mqttLightPublisher;

    /**
     * Encapsulator for finding all available traffic lights
     * @return List of TrafficLights
     */
    public List<TrafficLight> getAllTrafficLights()
    {
        return trafficLightRepository.findAll();
    }

    /**
     * Encapsulator for finding a specific traffic light based on ID
     * @param id ID of traffic light
     * @return Traffic Light
     */
    public TrafficLight getTrafficLight(long id)
    {
        return trafficLightRepository.findOne(id);
    }

    /**
     * Save given trafficlight into database
      * @param tl TrafficLight to save
     */
    public void saveTl(TrafficLight tl)
    {
        trafficLightRepository.save(tl);
    }

    /**
     * Update given state to a specific traffic light in the database
     * @param id ID of the traffic light
     * @param state State to give to the traffic light
     */
    public void updateState(long id, String state){
        TrafficLight tl = getTrafficLight(id);
        tl.setState(state);
        trafficLightRepository.save(tl);
    }

    /**
     * Publish Traffic Light information on MQTT
     * TODO: Traffic light as param instead of ID and state?
     * @param tlId ID of the Traffic Light
     * @param state State of the Traffic Light
     * @return MQTT Send Success
     */
    public boolean sendLight(Long tlId, String state)
    {
        TrafficLight trafficLight = new TrafficLight();
        trafficLight.setId(tlId);
        trafficLight.setState(state);
        return mqttLightPublisher.publishLight(trafficLight, tlId);
    }

    /**
     * Delete a tl from the db
     * @param id the tl id
     */
    public void deleteTl(Long id){
        trafficLightRepository.delete(id);
    }

    /**
     * Find a traffic light on a point
     * @param point, the point to check
     * @return trafficlight, the trafficlight entity on that point
     */
    public TrafficLight findTrafficLightByPoint(Point point){
        return trafficLightRepository.findByPoint(point);
    }
}

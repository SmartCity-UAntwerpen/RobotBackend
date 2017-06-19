package be.uantwerpen.sc.services;

import be.uantwerpen.sc.controllers.mqtt.MqttJobPublisher;
import be.uantwerpen.sc.controllers.mqtt.MqttLightPublisher;
import be.uantwerpen.sc.models.Job;
import be.uantwerpen.sc.models.TrafficLight;
import be.uantwerpen.sc.repositories.TrafficLightRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Niels on 2/04/2016.
 */
@Service
public class TrafficLightControlService
{

    @Autowired
    private TrafficLightRepository trafficLightRepository;

    @Autowired
    MqttLightPublisher mqttLightPublisher;

    public List<TrafficLight> getAllTrafficLights()
    {
        return trafficLightRepository.findAll();
    }

    public TrafficLight getTrafficLight(long id)
    {
        return trafficLightRepository.findOne(id);
    }

    public void updateTL(TrafficLight trafficlightEntity){
        TrafficLight dbTL = trafficLightRepository.findOne(trafficlightEntity.getId());
        dbTL = trafficlightEntity;
        trafficLightRepository.save(dbTL);
    }

    public void saveTl(TrafficLight tl)
    {
        trafficLightRepository.save(tl);
    }

    public void updateState(long id, String state){
        TrafficLight tl = getTrafficLight(id);
        tl.setState(state);
        trafficLightRepository.save(tl);
    }

    public boolean sendLight(Long tlId, String state)
    {
        TrafficLight trafficLight = new TrafficLight();
        trafficLight.setId(tlId);
        trafficLight.setState(state);
        return mqttLightPublisher.publishLight(trafficLight, tlId);
    }

}

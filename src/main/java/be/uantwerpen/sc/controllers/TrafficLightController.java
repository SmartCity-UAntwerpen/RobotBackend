package be.uantwerpen.sc.controllers;

import be.uantwerpen.sc.models.TrafficLight;
import be.uantwerpen.sc.repositories.TrafficLightRepository;
import be.uantwerpen.sc.services.TrafficLightControlService;
import be.uantwerpen.sc.services.newMap.LinkControlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author  Niels on 2/04/2016.
 * @author Reinout
 */
@RestController
@RequestMapping("/tlight/")
public class TrafficLightController
{
    /**
     * Autowired Traffic Light Control Service
     */
    @Autowired
    private TrafficLightControlService trafficLightControlService;

    /**
     * Autowired Traffic Light Repository
     */
    @Autowired
    private TrafficLightRepository trafficLightRepository;

    /**
     * Autowired Link Control Service
     */
    @Autowired
    private LinkControlService linkControlService;

    /**
     * GET <- WHO
     * Returns all traffic lights
     * @return List of TrafficLights
     */
    @RequestMapping(method = RequestMethod.GET)
    public List<TrafficLight> allTrafficLights()
    {
        return trafficLightControlService.getAllTrafficLights();
    }

    /**
     * GET <- WHO
     * Get specific traffic light by ID
     * @param id ID of traffic light
     * @return TrafficLight
     */
    @RequestMapping(value = "{id}",method = RequestMethod.GET)
    public TrafficLight getTrafficLight(@PathVariable("id") Long id)
    {
        return trafficLightControlService.getTrafficLight(id);
    }

    /**
     * GET <- WHO
     * Save new trafficlight with state "Test"
     * TODO probably test function
     */
    @RequestMapping(value = "savetest",method = RequestMethod.GET)
    public void saveTlTest()
    {
        TrafficLight tl = new TrafficLight();
        tl.setState("Test");
        trafficLightControlService.saveTl(tl);
    }

    /**
     * Set Trafficlight state
     * TODO No mapping?
     * @param id Traffic Light ID
     * @param state Traffic Light new State
     */
    public void updateState(long id, String state){
        trafficLightControlService.updateState(id, state);
    }

    /**
     * Get state of specific ID light
     * GET <- WHO
     * @param id Traffic Light ID
     * @return String State
     */
    @RequestMapping(value = "getState/{idtlight}", method = RequestMethod.GET)
    public String getState(@PathVariable("idtlight") int id){
        return  trafficLightControlService.getTrafficLight(id).getState();
    }

    /**
     * GET <- WHO
     * Create Trafficlight with given parameters
     * @param id Link ID
     * @param progress Link Progress
     * @param direction Trafficlight Direction
     * @param state Trafficlight State
     * @return ID of traffic Light
     */
    @RequestMapping(value = "initiate/{id}/{progress}/{direction}/{state}", method = RequestMethod.GET)
    public long initiate(@PathVariable("id") int linkID, @PathVariable("progress") int progress, @PathVariable("direction") String direction, @PathVariable("state") String state){
        TrafficLight trafficLight = new TrafficLight();
        List<TrafficLight> tlights = trafficLightRepository.findAll();
        long id = (long) (tlights.size()+1);
        trafficLight.setId(id);
        trafficLight.setLink(linkControlService.getLink((long) linkID));
        trafficLight.setPlaceLink(progress);
        trafficLight.setDirection(direction);
        trafficLight.setState(state);
        trafficLightControlService.saveTl(trafficLight);
        return id;
    }


    /**
     * GET <- WHO
     * Send Traffic Light info over MQTT
     * @param id ID of traffic Light
     * @param state State to update
     */
    @RequestMapping(value = "send/{id}/{state}", method = RequestMethod.GET)
    public void sendTrafficLightState(@PathVariable("id")int id, @PathVariable("state") String state){

        TrafficLight trafficLight = trafficLightControlService.getTrafficLight(id);
        trafficLight.setState(state);
        trafficLightControlService.sendLight((long) id, state);
    }

}

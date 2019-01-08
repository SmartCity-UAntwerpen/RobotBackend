package be.uantwerpen.sc.controllers;

import be.uantwerpen.rc.models.TrafficLight;
import be.uantwerpen.rc.models.map.Point;
import be.uantwerpen.sc.repositories.TrafficLightRepository;
import be.uantwerpen.sc.services.TrafficLightControlService;
import be.uantwerpen.sc.services.newMap.LinkControlService;
import be.uantwerpen.sc.services.newMap.PointControlService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
     * Autowired Point Control Service
     */
    @Autowired
    private PointControlService pointControlService;

    Logger logger = LoggerFactory.getLogger(TrafficLightController.class);

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
     * @param point the tl location
     * @param state Trafficlight State
     * @return id, the new ID of the TL
     */
    @RequestMapping(value = "initiate/{point}/{state}", method = RequestMethod.GET)
    public long initiate(@PathVariable("point") Long point, @PathVariable("state") String state){
        Point p = pointControlService.getPoint(point);
        TrafficLight tlight = trafficLightControlService.findTrafficLightByPoint(p);
        if(tlight == null){
            TrafficLight trafficLight = new TrafficLight();
            List<TrafficLight> tlights = trafficLightRepository.findAll();
            long id = (long) (tlights.size()+1);
            trafficLight.setId(id);
            trafficLight.setState(state);
            trafficLight.setPoint(p);
            trafficLightControlService.saveTl(trafficLight);
            logger.info("New TrafficLight on location "+point + " with new ID: "+id);
            return id;
        }else{
            //The trafficlight already exists return its id
            logger.info("TrafficLight already in database (UNGRACEFUL SHUTDOWN?) on location "+point + " with new ID: "+tlight.getId());
            return tlight.getId();

        }
    }

    /**
     * GET <- WHO
     * Delete a traffic light
     * @param id the tl id
     */
    @RequestMapping(value = "delete/{id}", method = RequestMethod.GET)
    public void delete(@PathVariable("id") Long id){
        trafficLightControlService.deleteTl(id);
        logger.info("Deleting TrafficLight "+id);
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

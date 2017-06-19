package be.uantwerpen.sc.controllers;

import be.uantwerpen.sc.models.TrafficLight;
import be.uantwerpen.sc.repositories.TrafficLightRepository;
import be.uantwerpen.sc.services.LinkControlService;
import be.uantwerpen.sc.services.TrafficLightControlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by Niels on 2/04/2016.
 */
@RestController
@RequestMapping("/tlight/")
public class TrafficLightController
{
    @Autowired
    private TrafficLightControlService trafficLightControlService;

    @Autowired
    private TrafficLightRepository trafficLightRepository;

    @Autowired
    private LinkControlService linkControlService;

    @RequestMapping(method = RequestMethod.GET)
    public List<TrafficLight> allTrafficLights()
    {
        return trafficLightControlService.getAllTrafficLights();
    }

    @RequestMapping(value = "{id}",method = RequestMethod.GET)
    public TrafficLight getTrafficLight(@PathVariable("id") Long id)
    {
        return trafficLightControlService.getTrafficLight(id);
    }

    @RequestMapping(value = "savetest",method = RequestMethod.GET)
    public void saveTlTest()
    {
        TrafficLight tl = new TrafficLight();
        tl.setState("Test");
        trafficLightControlService.saveTl(tl);
    }

    public void updateState(long id, String state){
        trafficLightControlService.updateState(id, state);
    }

    @RequestMapping(value = "getState/{idtlight}", method = RequestMethod.GET)
    public String getState(@PathVariable("idtlight") int idtlight){

        TrafficLight trafficLight = trafficLightControlService.getTrafficLight(idtlight);

        return trafficLight.getState();
    }

    @RequestMapping(value = "initiate/{id}/{progress}/{direction}/{state}", method = RequestMethod.GET)
    public long initiate(@PathVariable("id") int id, @PathVariable("progress") int progress, @PathVariable("direction") String direction, @PathVariable("state") String state){
        TrafficLight trafficLight = new TrafficLight();

        List<TrafficLight> tlights = trafficLightRepository.findAll();
        int size = tlights.size();
        System.out.println(size);
        long iD = (long) (size+1);
        trafficLight.setId(iD);
        trafficLight.setLink(linkControlService.getLink((long) id));
        trafficLight.setPlaceLink(progress);
        trafficLight.setDirection(direction);
        trafficLight.setState(state);
        trafficLightControlService.updateTL(trafficLight);

        return id;
    }

    @RequestMapping(value = "send/{id}/{state}", method = RequestMethod.GET)
    public void sendTrafficLightState(@PathVariable("id")int id, @PathVariable("state") String state){

        TrafficLight trafficLight = trafficLightControlService.getTrafficLight(id);
        trafficLight.setState(state);
        trafficLightControlService.sendLight((long) id, state);
    }

}

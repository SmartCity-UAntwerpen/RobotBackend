package be.uantwerpen.sc.controllers;

import be.uantwerpen.sc.services.BotControlService;
import be.uantwerpen.sc.services.PathPlanningService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author  Dries on 11-5-2017.
 * @author Reinout
 * @author Dieter 2018-2019
 *
 * Cost Controller
 */
@RestController
@RequestMapping("/")
public class CostController {

    /**
     * Autowired path planning service
     */
    @Autowired
    private PathPlanningService pathPlanningService;

    /**
     * Autowired Bot Control Service
     */
    @Autowired
    private BotControlService botControlService;

    private Logger logger = LoggerFactory.getLogger(CostController.class);

    /**
     *
     * @param start
     * @param stop
     * @return
     */
    @RequestMapping(value = "{start}/{stop}",method = RequestMethod.GET)
    public String calcCost(@PathVariable("start") int start, @PathVariable("stop") int stop)
    {
        int cost = (int) pathPlanningService.CalculatePathWeight(start, stop);
        if(botControlService.getAllAvailableBots().isEmpty()){
            cost = cost +10;
        }

        /*
        if(start == 9 && stop == 16 || start == 16 && stop == 9){
            return 1000;
        }
        if(start == 10 && stop == 16 || start == 16 && stop == 10){
            return 1000;
        }*/
        logger.info("Cost calculated between "+start +" and "+stop+": "+cost);
        return "{\"cost\":" +cost+"}";
    }
}

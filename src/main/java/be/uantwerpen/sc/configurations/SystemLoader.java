package be.uantwerpen.sc.configurations;

import be.uantwerpen.sc.services.JobControlService;
import be.uantwerpen.sc.services.TerminalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;

/**
 * SystemLoader
 */
@Configuration
public class SystemLoader implements ApplicationRunner
{
    /**
     * Auto Wired terminalservice
     */

    @Autowired
    private TerminalService terminalService;
    /**
     * Auto Wired jobService
     */

    @Autowired
    private JobControlService jobControlService;

    private Logger logger = LoggerFactory.getLogger(SystemLoader.class);

    public SystemLoader(TerminalService terminalService, JobControlService jobControlService)
    {
        this.terminalService = terminalService;
        this.jobControlService = jobControlService;
    }

    public SystemLoader()
    {
        logger.info("Starting thread jobControlService");
    }

    //Run after Spring context initialization
    @Override
    public void run(ApplicationArguments args)
    {
        new Thread(new StartupProcess()).start();
    }

    /**
     * Starts up Terminal after waiting for 200ms
     * Wait is implemented so that the printing is still pretty
     */
    private class StartupProcess implements Runnable
    {
        @Override
        public void run()
        {
            try
            {
                Thread.sleep(200);
            }
            catch(InterruptedException ex)
            {
                //Thread interrupted
            }

            //terminalService.systemReady();

            //Start jobService

            new Thread(jobControlService).start();
        }
    }
}

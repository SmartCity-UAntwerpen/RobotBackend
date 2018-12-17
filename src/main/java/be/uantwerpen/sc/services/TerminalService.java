package be.uantwerpen.sc.services;

import be.uantwerpen.sc.controllers.BotController;
import be.uantwerpen.sc.controllers.CostController;
import be.uantwerpen.sc.controllers.JobController;
import be.uantwerpen.rc.models.Bot;
import be.uantwerpen.rc.models.map.Link;
import be.uantwerpen.sc.services.newMap.PointControlService;
import be.uantwerpen.sc.tools.Terminal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for running the Terminal
 */
@Service
public class TerminalService
{
    @Autowired
    private JobService jobService;

    @Autowired
    private BotControlService botControlService;

    @Autowired
    private PointControlService pointControlService;

    @Autowired
    private CostController costController;

    @Autowired
    private BotController botController;

    @Autowired
    private JobController jobController;
    @Autowired
    private TimerService timerService;

    private Terminal terminal;

    /**
     * Default constructor that binds the Terminal's ExecuteCommand to ParseCommand
     */
    public TerminalService()
    {
        terminal = new Terminal()
        {
            @Override
            public void executeCommand(String commandString)
            {
                parseCommand(commandString);
            }
        };
    }

    /**
     * Starts TimerService and activates Terminal
     */
    public void systemReady()
    {
        try {
            new Thread(timerService).start();
            System.out.println("Timer started");
        }catch(Exception e){
            System.out.println("Timer not started");
        }

        terminal.printTerminal("\nSmartCity Backend [Version " + getClass().getPackage().getImplementationVersion() + "]\n(c) 2015-2017 University of Antwerp. All rights reserved.");
        terminal.printTerminal("Type 'help' to display the possible commands.");
        terminal.activateTerminal();
    }

    /**
     * Parses Terminal commands
     * Commands:
     *      generatebot
     *      job [botID] [command]
     *      showbots
     *      clearbots
     *      clearlocks
     *      clearall
     *      delete [botID]
     *      exit
     *      help
     *      ?
     *      calcweight [start] [stop]
     * @param commandString
     */
    private void parseCommand(String commandString)
    {
        String command = commandString.split(" ", 2)[0].toLowerCase();

        switch(command)
        {
            case "generatebot":
                long id=botController.initiate(9999L,"INDEPENDENT");
                terminal.printTerminal("Bot ID: "+id);
                break;
            case "job":
                if(commandString.split(" ", 4).length <= 2)
                {
                    terminal.printTerminalInfo("Missing arguments! 'job {jobId} {botId} {startId} {stopId}");
                }
                else
                {
                    try
                    {
                        String[] args = commandString.split(" ");
                        long jobid = Long.parseLong(args[1]);
                        long botid = Long.parseLong(args[2]);
                        long start = Long.parseLong(args[3]);
                        long stop = Long.parseLong(args[4]);
                        this.sendJob(jobid,start,stop);

                    }
                    catch(Exception e)
                    {
                        terminal.printTerminalError(e.getMessage());
                    }
                }
                break;
            case "showbots":
                this.printAllBots();
                break;
            case "clearbots":
                this.deleteBots();
                break;
            case "clearlocks":
                this.clearPointLocks();
                break;
            case "clearall":
                this.deleteBots();
                this.clearPointLocks();
                break;
            case "delete":
                if(commandString.split(" ", 2).length <= 1)
                    terminal.printTerminalInfo("Missing arguments! 'delete {botId}'");
                else
                {
                    try
                    {
                        int parsedInt = Integer.parseInt(commandString.split(" ", 2)[1]);
                        this.deleteBot(parsedInt);
                    }
                    catch(Exception e){
                        terminal.printTerminalError(e.getMessage());
                    }
                }
                break;
            case "exit":
                exitSystem();
                break;
            case "help":
            case "?":
                printHelp();
                break;
            case "calcpathweight":
                if(commandString.split(" ", 3).length <= 2)
                    terminal.printTerminalInfo("Missing arguments! 'calcpathweight {start} {stop}");
                else
                {
                    try
                    {
                        String[] ints= (commandString.split(" ", 3));
                        System.out.println(costController.calcPathWeight(Integer.parseInt(ints[1]), Integer.parseInt(ints[2])));

                    }
                    catch(Exception e)
                    {
                        terminal.printTerminalError(e.getMessage());
                    }
                }
                break;
            case "calcweight":
                if(commandString.split(" ", 3).length <= 2)
                    terminal.printTerminalInfo("Missing arguments! 'calcWeight {start} {stop}");
                else
                {
                    try
                    {
                        String[] ints= (commandString.split(" ", 3));
                        System.out.println(costController.calcCost(Integer.parseInt(ints[1]), Integer.parseInt(ints[2])));
                    }
                    catch(Exception e)
                    {
                        terminal.printTerminalError(e.getMessage());
                    }
                }
                break;
            default:
                terminal.printTerminalInfo("Command: '" + command + "' is not recognized.");
                break;
        }
    }

    /**
     * Program exits, used as command
     */
    private void exitSystem()
    {
        System.exit(0);
    }

    /**
     * Prints help, used as command
     * TODO Update
     */
    private void printHelp()
    {
        terminal.printTerminal("Available commands:");
        terminal.printTerminal("-------------------");
        terminal.printTerminal("generatebot: : Generates a new bot ID for testing");
        terminal.printTerminal("calcweight {start} {stop} : Calculate weight for all current bots for a given path");
        terminal.printTerminal("calcpathweight {start} {stop} : Calculate weight for a given path");
        terminal.printTerminal("'job {botId} {command}' : send a job to the bot with the given id.");
        terminal.printTerminal("'showbots' : show all bots in the database.");
        terminal.printTerminal("'clearbots' : Remove all bots from the database.");
        terminal.printTerminal("'clearlocks' : Release all point locks.");
        terminal.printTerminal("'clearall' : Remove all bots from the database and release all point locks.");
        terminal.printTerminal("'delete {botId}' : remove the bot with the given id from the database.");
        terminal.printTerminal("'exit' : shutdown the server.");
        terminal.printTerminal("'help' / '?' : show all available commands.\n");
    }

    /**
     * Prints information about all available Bots
     */
    private void printAllBots()
    {
        List<Bot> bots = botControlService.getAllBots();
        if(bots.isEmpty())
            terminal.printTerminalInfo("There are no bots available to list.");
        else
        {
            terminal.printTerminal("Bot-id\t\tLink-id\t\tStatus");
            terminal.printTerminal("------------------------------");

            for(Bot bot : bots)
            {
                Long linkId = -1L;
                Link link = bot.getLinkId();

                if(link != null)
                    linkId = link.getId();

                terminal.printTerminal("\t" + bot.getIdCore() + "\t\t" + linkId + "\t\t\t" + bot.getStatus());
            }
        }
    }

    /**
     * Deletes Bot, used as command
     * @param botID ID of the Bot to delete
     */
    private void deleteBot(int botID)
    {
        if(botControlService.deleteBot(botID))
            terminal.printTerminalInfo("Bot deleted with id: " + botID + ".");
        else
            terminal.printTerminalError("Could not delete bot with id: " + botID + "!");
    }

    /**
     * Resets available bots, used as command
     */
    private void deleteBots()
    {
        botControlService.deleteBots();
        terminal.printTerminalInfo("All bot entries cleared from database.");
    }

    /**
     * Clears all point locks, used as command
     */
    private void clearPointLocks()
    {
        pointControlService.clearAllLocks();
        terminal.printTerminalInfo("All points are released.");
    }

    /**
     * Adds a job to the job queue
     */
    private void sendJob(Long jobId, long start, long stop)
    {
        if(jobService.queueJob(jobId,start,stop))
            terminal.printTerminalInfo("Job placed in queue!");
        else
            terminal.printTerminalError("Could not queue job!");
    }
}

package be.uantwerpen.sc.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Observable;
import java.util.Observer;

/**
 * Abstract Terminal Class
 * TODO comments
 */
public abstract class Terminal
{
    /**
     * Terminal reader for reading
      */
    private TerminalReader terminalReader;

    /**
     * Constructor creating terminal and observer, and connecting both
     */
    public Terminal()
    {
        terminalReader = new TerminalReader();

        terminalReader.getObserver().addObserver(new Observer()
        {
            @Override
            public void update(Observable source, Object object)
            {
                if(object != null)
                {
                    String command = ((String)object).trim();

                    if(!command.equals(""))
                    {
                        executeCommand((String) object);
                    }
                }

                activateTerminal();
            }
        });
    }

    /**
     * Print to terminal
     * @param message message to print
     */
    public static void printTerminal(String message)
    {
        System.out.println(message);
    }

    /**
     * Print to terminal with a timestamp
     * @param message message to print
     */
    public static void printTerminalInfo(String message)
    {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

        System.out.println("[INFO - " + timeFormat.format(calendar.getTime()) + "] " + message);
    }

    /**
     * Print error to terminal with a timestamp
     * @param message Error to print
     */
    public static void printTerminalError(String message)
    {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

        System.out.println("[ERROR - " + timeFormat.format(calendar.getTime()) + "] " + message);
    }

    /**
     * Create TerminalReader in a new thread
     */
    public void activateTerminal()
    {
        new Thread(terminalReader).start();
    }

    /**
     * Run ? TODO
      * @param commandString
     */
    abstract public void executeCommand(String commandString);

    /**
     * TerminalReader Subclass
     * TODO Function, comments
     */
    private class TerminalReader implements Runnable
    {
        /**
         * Terminal Observer TODO
         */
        private TerminalObserver observer;

        /**
         * Creates Reader
         */
        public TerminalReader()
        {
            this.observer = new TerminalObserver();
        }

        /**
         * Gets this Reader's observer
         * @return
         */
        public TerminalObserver getObserver()
        {
            return this.observer;
        }

        /**
         * Run Reader: Read terminal and throw events every readline
          */
        @Override
        public void run()
        {
            BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

            System.out.print("# ");

            try
            {
                String command = input.readLine();
                this.observer.setChanged();
                this.observer.notifyObservers(command);
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * Terminal Observer
     * Extendable for observing Terminal
     */
    private class TerminalObserver extends Observable
    {
        public void clearChanged()
        {
            super.clearChanged();
        }

        public void setChanged()
        {
            super.setChanged();
        }
    }
}

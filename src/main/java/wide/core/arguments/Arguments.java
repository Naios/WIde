package wide.core.arguments;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import wide.core.WIde;
import wide.core.hooks.Hook;

abstract class DefaultOptions extends Options
{
    DefaultOptions()
    {
        configure();
    }

    protected abstract void configure(); 
}

public class Arguments
{
    private static final Options options = new DefaultOptions()
    {
        @Override
        @SuppressWarnings("static-access")
        protected void configure()
        {
            addOption(OptionBuilder
                    .withLongOpt("config")
                    .withDescription("Uses a specific Config instead the standard one.")
                    .withValueSeparator('=')
                    .hasArg()
                    .create("c"));
            addOption(OptionBuilder
                    .withLongOpt("trace")
                    .withDescription("Enables detailed Tracelogs.")
                    .create("t"));
            addOption(OptionBuilder
                    .withLongOpt("nogui")
                    .withDescription("Prevents WIde from creating a gui. (console mode)")
                    .create("ng"));
            addOption(OptionBuilder.withLongOpt("help")
                    .withDescription("Shows the available arguments.")
                    .create("h"));
        }
    };

    private CommandLine cmd = null;

    public Arguments()
    {
    }

    public boolean parse(String[] args)
    {       
        CommandLineParser parser = new BasicParser();

        try
        {
            cmd = parser.parse(options, args);
        } catch (ParseException exception)
        {
            System.out.println(exception.getMessage() + "\n");
        }

        // Parser failed if cmd == null
        // Display help context then
        if (cmd == null || cmd.hasOption("help"))
        {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("WIde", options);
            return false;
        }

        // Hook.ON_ARGS_FINISHED
        WIde.getHooks().fire(Hook.ON_ARGS_FINISHED);
        return true;
    }

    public boolean hasArgument(String arg)
    {
        return (cmd != null) && cmd.hasOption(arg);
    }

    public boolean isGuiApplication()
    {
        return !hasArgument("nogui");
    }

    public boolean isTraceEnabled()
    {
        return hasArgument("trace");
    }

    public String getConfigName()
    {
        if (!hasArgument("config"))
            return "WIde.xml";
        else
            return cmd.getOptionValue("config");
    }
}

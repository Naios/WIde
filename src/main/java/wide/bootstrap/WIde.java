package wide.bootstrap;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

abstract class ConfigurateableProgramOptions extends Options
{
    ConfigurateableProgramOptions()
    {
        configure();
    }
    
    protected abstract void configure(); 
}

public class WIde
{
    private static final Options options = new ConfigurateableProgramOptions()
    {
        @Override
        @SuppressWarnings("static-access")
        protected void configure()
        {
            addOption(OptionBuilder.withLongOpt("help")
                    .withDescription("Shows the available arguments.")
                    .create("h"));
            addOption(OptionBuilder
                    .withLongOpt("nogui")
                    .withDescription("Prevents WIde from creating a gui. (console mode)")
                    .create("ng"));
        }
    };

    public static void main(String[] args)
    {
        // Debug
        args = new String[] { "", "--nogui" };

        WIde wide = new WIde();
        wide.parseArguments(args);
    }

    private void parseArguments(String[] args)
    {
        CommandLineParser parser = new BasicParser();
        CommandLine cmd = null;

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
            return;
        }
    }
}

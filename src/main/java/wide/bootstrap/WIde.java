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

import wide.session.arguments.Arguments;
import wide.session.config.Config;
import wide.session.config.WIdeConfig;

public class WIde
{
    private final Config config;

    private final Arguments args;

    public static void main(String[] args)
    {
        // Debug Begin
        args = new String[] { "", "--nogui" };
        // Debug End

        final Arguments arguments;
        final Config config;

        try
        {
            arguments = new Arguments(args);
            config = new WIdeConfig("WIde.xml");

        } catch (Exception e)
        {
            return;
        }

        final WIde wide = new WIde(arguments, config);
        wide.launch();
    }

    public WIde(Arguments args, Config config)
    {
        this.config = config;
        this.args = args;
    }
    
    public Config getConfig()
    {
        return config;
    }

    public Arguments getArgs()
    {
        return args;
    }
    
    private void launch()
    {
        
        
        
    }
}

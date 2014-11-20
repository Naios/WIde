package wide.core.session.enviroment;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import wide.core.Constants;
import wide.core.WIde;
import wide.core.session.hooks.Hook;

/**
 * {@link Enviroment} implements all static application settings like passed arguments and revision info.<p>
 * There are several getters to check arguments. You may also get the revision with {@link #getApplicationInfo()}
 */
public class Enviroment
{
    @SuppressWarnings("serial")
    private static final Options options = new DefaultOptions()
    {
        @Override
        @SuppressWarnings("static-access")
        public void configure()
        {
            addOption(OptionBuilder
                    .withLongOpt("config")
                    .withDescription("Uses a specific Config instead the standard one.")
                    .withValueSeparator('=')
                    .hasArg()
                    .withArgName("path")
                    .create("c"));

            addOption(OptionBuilder
                    .withLongOpt("execute")
                    .withDescription("Executes a Single command in the WIde Console")
                    .withValueSeparator('=')
                    .hasArg()
                    .withArgName("query")
                    .create("e"));

            addOption(OptionBuilder
                    .withLongOpt("trace")
                    .withDescription("Enables detailed Tracelogs.")
                    .create("t"));

            addOption(OptionBuilder
                    .withLongOpt("legacy")
                    .withDescription("Enables legacy mode.")
                    .create("l"));

            addOption(OptionBuilder
                    .withLongOpt("nogui")
                    .withDescription("Prevents WIde from creating a gui. (console mode)")
                    .create("ng"));

            addOption(OptionBuilder.withLongOpt("help")
                    .withDescription("Shows the available arguments.")
                    .create("h"));

            addOption(OptionBuilder.withLongOpt("version")
                    .withDescription("Shows Version and Author Info.")
                    .create("v"));
        }
    };

    private CommandLine cmd = null;

    private final ApplicationInfo applicationInfo = new ApplicationInfo(Constants.PATH_REPOSITORY_INFO.toString());

    public boolean setUp(String[] args)
    {
        readApplicationInfo();
        return parseArguments(args);
    }

    private boolean parseArguments(String[] args)
    {
        final CommandLineParser parser = new BasicParser();

        try
        {
            cmd = parser.parse(options, args);
        } catch (final ParseException exception)
        {
            System.out.println(exception.getMessage() + "\n");
        }

        // Parser failed if cmd == null
        // Display help context then
        if (cmd == null  || cmd.hasOption("help") || !checkArguments())
        {
            final HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("WIde", options);
            return false;
        }
        else if (cmd.hasOption("version"))
        {
            System.out.println(String.format("%s\n" + " * Built: %s", getVersionString(), getApplicationInfo().getBuildTime()));
            return false;
        }

        // Hook.ON_ARGUMENTS_LOADED
        WIde.getHooks().fire(Hook.ON_ARGUMENTS_LOADED);
        return true;
    }

    private void readApplicationInfo()
    {
        applicationInfo.read();
    }

    public boolean hasArgument(String arg)
    {
        return (cmd != null) && cmd.hasOption(arg);
    }

    public String getParameter(String arg)
    {
        return (cmd != null) ? cmd.getOptionValue(arg) : null;
    }

    private boolean checkArguments()
    {
        if ((getParameter("execute") != null) && (!hasArgument("nogui")))
            System.out.println("[Warning] --execute forces --nogui");

        return true;
    }

    public boolean isGuiApplication()
    {
        return !hasArgument("nogui") && (getParameter("execute") == null);
    }

    public boolean isTraceEnabled()
    {
        return hasArgument("trace");
    }

    public boolean isLegacyEnabled()
    {
        return hasArgument("legacy");
    }

    public String getConfigName()
    {
        if (!hasArgument("config"))
            return Constants.STRING_DEFAULT_PROPERTIES.toString();
        else
            return cmd.getOptionValue("config");
    }

    public String getPath()
    {
        return System.getProperty("user.dir");
    }

    public ApplicationInfo getApplicationInfo()
    {
        return applicationInfo;
    }

    public String getVersionString()
    {
        return String.format("WIde (%s)", getApplicationInfo().getHashShort());
    }
}

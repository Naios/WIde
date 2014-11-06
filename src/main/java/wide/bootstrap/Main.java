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

public class Main
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
    
    @SuppressWarnings("static-access")
    public static void main(String[] args)
    {
        // Test Args
        args = new String[] { "", "--nogui" };

        // Parses the arguments
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

        /*
         * OptionBuilder.
         * 
         * lvOptions.addOption("h", "hilfe", false, "zeigt diese Hilfe an.");
         * Option lvName = new Option("name", true, "der Namen des Nutzers.");
         * lvOptions.addOption(lvName);
         * 
         * lvOptions .addOption(OptionBuilder .withLongOpt("tage")
         * .withDescription(
         * "die Anzahl der Tage mit Gleichheitszeichen als Separator.")
         * .isRequired().withValueSeparator('=').hasArg() .create("t"));
         * 
         * OptionGroup lvGruppe = new OptionGroup();
         * 
         * lvGruppe.addOption(OptionBuilder.withLongOpt("gruppe1")
         * .withDescription("gruppe 1").create("g1"));
         * lvGruppe.addOption(OptionBuilder.withLongOpt("gruppe2")
         * .withDescription("gruppe 2").create("g2"));
         * lvGruppe.addOption(OptionBuilder.withLongOpt("gruppe3")
         * .withDescription("gruppe 3").create("g3"));
         * 
         * lvOptions.addOptionGroup(lvGruppe);
         * 
         * CommandLineParser lvParser = new BasicParser();
         * 
         * CommandLine lvCmd = null; try { lvCmd = lvParser.parse(lvOptions,
         * args); } catch (ParseException pvException) {
         * System.out.println(pvException.getMessage()); return; }
         * 
         * lvCmd.hasOption("tage");
         * 
         * HelpFormatter lvFormater = new HelpFormatter();
         * 
         * lvFormater.printHelp("Programm_Name", lvOptions);
         */
        /*
         * // create Options object Options options = new Options();
         * 
         * // add t option options.addOption("t", false,
         * "display current time");
         * 
         * 
         * CommandLineParser parser = new DefaultParser(); try { // parse the
         * command line arguments CommandLine line = parser.parse( options, args
         * ); } catch( ParseException exp ) { // oops, something went wrong
         * System.err.println( "Parsing failed.  Reason: " + exp.getMessage() );
         * }
         */
    }
}

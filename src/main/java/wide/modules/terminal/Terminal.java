package wide.modules.terminal;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;

import wide.core.Constants;
import wide.core.WIde;
import wide.core.framework.extensions.modules.Module;
import wide.core.framework.ui.UserInferface;

public class Terminal extends Module implements UserInferface
{
    public Terminal()
    {
        super("default_terminal");
    }

    @Override
    public boolean validate()
    {
        return !WIde.getEnviroment().isGuiApplication();
    }

    @Override
    public void enable()
    {

    }

    @Override
    public void disable()
    {

    }

    @Override
    public void show()
    {
        final Console console = System.console();
        final String cmdString = WIde.getConfig().getProperty(Constants.PROPERTY_DATABASE_USER).get() +
                "@" + WIde.getConfig().getProperty(Constants.PROPERTY_DATABASE_HOST).get() + ": ";

        final String singleCommand = WIde.getEnviroment().getParameter("execute");

        if (!WIde.getDatabase().isConnected())
        {
            System.out.println("Sorry, could not connect to: "
                    + WIde.getConfig().getProperty(Constants.PROPERTY_DATABASE_USER).get() + "@"
                    + WIde.getConfig().getProperty(Constants.PROPERTY_DATABASE_HOST).get()
                    + ", closed.");

            return;
        }

        if (singleCommand != null)
        {
            // Single Command
            System.out.println(cmdString + singleCommand);
            execute(singleCommand);
        }
        else
        {
            System.out.println("Welcome to WIde! (Console Mode)");

            if (console != null && !WIde.getEnviroment().isLegacyEnabled())
            // Normal Mode
            {
                System.out.println();
                String input;

                while (true)
                {
                    input = console.readLine(cmdString);
                    if (input == null || input.equals("exit"))
                        break;

                    execute(input);
                }

                System.out.println();
            }
            // Legacy Mode
            else
            {
                System.out
                        .println(">> Switched to Legacy Mode! (Shortcuts disabled)\n");

                final BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(System.in));
                String input = "";

                while (true)
                {
                    System.out.print(cmdString);

                    try
                    {
                        input = bufferedReader.readLine();

                    } catch (final IOException e)
                    {
                    }
                    if (input == null || input.equals("exit"))
                        break;

                    execute(input);
                }
            }

            System.out.println("Bye!");
        }
    }

    private void execute(String cmd)
    {
        if (cmd.length() == 0)
            return;

        if (!WIde.getScripts().execute(cmd))
            System.out.println("\t >> Wrong command!");
    }
}

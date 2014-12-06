package com.github.naios.wide.modules.terminal;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;

import com.github.naios.wide.core.Constants;
import com.github.naios.wide.core.WIde;
import com.github.naios.wide.core.framework.extensions.modules.Module;
import com.github.naios.wide.core.framework.extensions.modules.type.UIModule;
import com.github.naios.wide.modules.ModuleDefinition;

public class Terminal extends Module implements UIModule
{
    public Terminal(final ModuleDefinition definition)
    {
        super(definition);
    }

    @Override
    public boolean validate()
    {
        return !WIde.getEnviroment().isGuiApplication();
    }

    @Override
    public void onEnable()
    {

    }

    @Override
    public void onDisable()
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

    private void execute(final String cmd)
    {
        if (cmd.length() == 0)
            return;

        if (!WIde.getScripts().execute(cmd))
            System.out.println("\t >> Wrong command!");
    }
}

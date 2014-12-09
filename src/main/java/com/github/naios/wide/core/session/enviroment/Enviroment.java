
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.core.session.enviroment;

import java.io.File;
import java.io.IOException;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.github.naios.wide.core.Constants;
import com.github.naios.wide.core.WIde;
import com.github.naios.wide.core.session.hooks.Hook;

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

    public boolean setUp(final String[] args)
    {
        if (!hasReadWriteAccess())
        {
            System.out.println("WIde needs read/write permission to the working direktory, closed!");
            return false;
        }

        readApplicationInfo();

        if (!parseArguments(args))
            return false;

        // Hook.ON_ENVIROMENT_LOADED
        WIde.getHooks().fire(Hook.ON_ENVIROMENT_LOADED);
        return true;
    }

    private boolean parseArguments(final String[] args)
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

        return true;
    }

    private void readApplicationInfo()
    {
        applicationInfo.read();
    }

    public boolean hasArgument(final String arg)
    {
        return (cmd != null) && cmd.hasOption(arg);
    }

    public String getParameter(final String arg)
    {
        return (cmd != null) ? cmd.getOptionValue(arg) : null;
    }

    private boolean checkArguments()
    {
        if (hasArgument("trace") && hasArgument("execute") && !hasArgument("nogui"))
            System.out.println("[Warning] --execute forces --nogui");

        return true;
    }

    public boolean isGuiApplication()
    {
        return !hasArgument("nogui") && (!hasArgument("execute"));
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

    private boolean hasReadWriteAccess()
    {
        final File file = new File(Constants.STRING_TEST.toString());
        try
        {
            file.createNewFile();
        } catch (final IOException e)
        {
            return false;
        }

        if (!file.canRead() || !file.canWrite())
            return false;

        file.delete();
        return true;
    }

    public void createDirectory(final String path)
    {
        final File dir = new File(path);
        if (dir.exists())
            if (dir.isDirectory())
                return;
            else
                dir.delete();

        dir.mkdir();
    }
}

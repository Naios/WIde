package wide.core;

import wide.core.arguments.Arguments;
import wide.core.config.Config;
import wide.core.config.WIdeConfig;
import wide.core.database.Database;
import wide.core.hooks.ActionHook;
import wide.core.hooks.Hook;

public class WIde
{
    private final static ActionHook HOOKS = new ActionHook();

    private final static Arguments ARGUMENTS = new Arguments();

    private final static Config CONFIG = new WIdeConfig();

    private final static Database DATABASE = new Database();

    private final static WIde INSTANCE = new WIde();

    public static ActionHook getHooks()
    {
        return HOOKS;
    }

    public static Config getConfig()
    {
        return CONFIG;
    }

    public static Arguments getArgs()
    {
        return ARGUMENTS;
    }

    public static Database getDatabase()
    {
        return DATABASE;
    }

    public static WIde getInstance()
    {
        return INSTANCE;
    }

    public static void main(String[] args)
    {
        if (!getArgs().parse(args))
            return;

        INSTANCE.launch();
    }

    public WIde()
    {
    }

    private void launch()
    {
        // Hook.ON_APPLICATION_LAUNCH
        WIde.getHooks().fire(Hook.ON_APPLICATION_LAUNCH);

        // Hook.ON_APPLICATION_STOP
        WIde.getHooks().fire(Hook.ON_APPLICATION_STOP);
    }
}

package wide.core;

import java.util.Collection;
import java.util.List;

import wide.core.framework.extensions.ModuleLoader;
import wide.core.framework.ui.UserInferface;
import wide.core.session.arguments.Arguments;
import wide.core.session.config.Config;
import wide.core.session.config.WIdeConfig;
import wide.core.session.database.Database;
import wide.core.session.hooks.ActionHook;
import wide.core.session.hooks.Hook;
import wide.core.session.querys.QueryParser;

public class WIde
{
    private final static ActionHook HOOKS = new ActionHook();

    private final static Arguments ARGUMENTS = new Arguments();

    private final static Config CONFIG = new WIdeConfig();

    private final static ModuleLoader MODULES = new ModuleLoader();
    
    private final static Database DATABASE = new Database();
    
    private final static QueryParser QUERYPARSER = new QueryParser();

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

    public static ModuleLoader getModules()
    {
        return MODULES;
    }
    
    public static Database getDatabase()
    {
        return DATABASE;
    }

    public static QueryParser getQueryparser()
    {
        return QUERYPARSER;
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
        
        // TODO Implement better Selection for interfaces
        // Currently its ok to select the first interface
        final List<UserInferface> interfaces = MODULES.getUserInterfaces();
        if (!interfaces.isEmpty())
            interfaces.get(0).show();
        else if(WIde.getArgs().isTraceEnabled())
            System.err.println("No User Interface available!");
        
        // Hook.ON_APPLICATION_STOP
        WIde.getHooks().fire(Hook.ON_APPLICATION_STOP);
    }
}

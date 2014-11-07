package wide.session;

import wide.session.arguments.Arguments;
import wide.session.config.Config;
import wide.session.config.WIdeConfig;
import wide.session.hooks.ActionHook;
import wide.session.hooks.Hook;

public class WIde
{
    private final static ActionHook HOOKS = new ActionHook();

    private final static Arguments ARGUMENTS = new Arguments();

    private final static Config CONFIG = new WIdeConfig();

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

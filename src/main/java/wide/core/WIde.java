package wide.core;

import java.util.List;

import wide.core.framework.extensions.modules.Module;
import wide.core.framework.extensions.modules.ModuleHolder;
import wide.core.framework.extensions.scripts.ScriptHolder;
import wide.core.framework.ui.UserInferface;
import wide.core.session.config.Config;
import wide.core.session.database.Database;
import wide.core.session.enviroment.Enviroment;
import wide.core.session.hooks.ActionHook;
import wide.core.session.hooks.Hook;

/**
 * {@link WIde} is the main class of the application, that holds several important static classes like:
 * {@link ActionHook}, {@link Enviroment}, {@link Config}, {@link ModuleHolder}, {@link ScriptHolder}, {@link Database}, {@link WIde},
 */
public class WIde
{
    private final static ActionHook HOOKS = new ActionHook();

    private final static Enviroment ENVIROMENT = new Enviroment();

    private final static Config CONFIG = new Config();

    private final static ModuleHolder MODULES = new ModuleHolder();

    private final static ScriptHolder SCRIPTS = new ScriptHolder();

    private final static Database DATABASE = new Database();

    private final static WIde INSTANCE = new WIde();

    /**
     * @return The global {@link ActionHook} object.
     */
    public static ActionHook getHooks()
    {
        return HOOKS;
    }

    /**
     * @return The global {@link Config} object.
     */
    public static Config getConfig()
    {
        return CONFIG;
    }

    /**
     * @return The global {@link Enviroment} object.
     */
    public static Enviroment getEnviroment()
    {
        return ENVIROMENT;
    }

    /**
     * @return The global {@link ModuleHolder} object.
     */
    public static ModuleHolder getModules()
    {
        return MODULES;
    }

    /**
     * @return The global {@link ScriptHolder} object.
     */
    public static ScriptHolder getScripts()
    {
        return SCRIPTS;
    }

    /**
     * @return The global {@link Database} object.
     */
    public static Database getDatabase()
    {
        return DATABASE;
    }

    /**
     * @return The global {@link WIde} object.
     */
    public static WIde getInstance()
    {
        return INSTANCE;
    }

    public static void main(String[] args)
    {
        if (!getEnviroment().setUp(args))
            return;

        INSTANCE.launch();
    }

    /**
     * Launches the {@link WIde} main procedure in the global {@link WIde} object.
     */
    private void launch()
    {
        // Hook.ON_APPLICATION_LAUNCH
        WIde.getHooks().fire(Hook.ON_APPLICATION_LAUNCH);

        DisplayUserInterface();

        // Hook.ON_APPLICATION_STOP
        WIde.getHooks().fire(Hook.ON_APPLICATION_STOP);
    }

    /**
     * Gets the preferred {@link UserInferface} from the {@link ModuleHolder} and displays it.
     */
    private void DisplayUserInterface()
    {
        // TODO Implement better Selection for UserInterfaces
        // Currently its ok to select the first interface
        final List<Module> interfaces = MODULES.getModulesInstanceOf(UserInferface.class);

        if (!interfaces.isEmpty())
            ((UserInferface)(interfaces.get(0))).show();
        else if(WIde.getEnviroment().isTraceEnabled())
            System.err.println("No User Interface available!");
    }
}

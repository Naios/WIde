package wide.core.framework.extensions.scripts;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import wide.core.WIde;
import wide.core.framework.extensions.ExtensionHolder;
import wide.core.session.hooks.Hook;
import wide.scripts.ScriptLoader;

public class ScriptHolder extends ExtensionHolder
{
    private final ScriptLoader loader = new ScriptLoader();
    
    private final Map<String, Script> scripts = new HashMap<>();

    @Override
    protected void load()
    {
        for (Script script : loader.getExtensions())
        {
            if (script.validate())
            {
                scripts.put(script.toString(), script);

                if(WIde.getArgs().isTraceEnabled())
                    System.out.println("Script " + script + " loaded.");
            }
        }

        // Hook.ON_SCRIPTS_LOADED
        WIde.getHooks().fire(Hook.ON_SCRIPTS_LOADED);
    }

    @Override
    protected void unload()
    {
        scripts.clear();
        
        // Hook.ON_SCRIPTS_UNLOADED
        WIde.getHooks().fire(Hook.ON_SCRIPTS_UNLOADED);
    }

    public boolean execute(String cmd)
    {
        // Split space but no quotes
        final String[] args = cmd.split(" (?=(([^'\"]*['\"]){2})*[^'\"]*$)");

        if (args.length < 1)
            return false;

        Script script = scripts.get(args[0]);
        if (script == null)
            return false;

        script.run(Arrays.copyOfRange(args, 1, args.length));        
        return true;
    }
}

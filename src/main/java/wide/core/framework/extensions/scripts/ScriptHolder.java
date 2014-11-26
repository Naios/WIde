package wide.core.framework.extensions.scripts;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import wide.core.WIde;
import wide.core.framework.extensions.Holder;
import wide.core.session.hooks.Hook;
import wide.scripts.ScriptLoader;

public class ScriptHolder extends Holder
{
    private final ScriptLoader loader = new ScriptLoader();

    private final Map<String, Script> scripts = new HashMap<>();

    @Override
    protected void load()
    {
        for (final Script script : loader.getExtensions())
        {
            /*
             * TODO do we need this?
             *if (script.validate())
             */

              scripts.put(script.toString(), script);

              if(WIde.getEnviroment().isTraceEnabled())
                  System.out.println("Script " + script + " loaded.");
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

    public Set<String> getScriptNames()
    {
        return scripts.keySet();
    }

    public Script getScriptByName(String name)
    {
        return scripts.get(name);
    }

    public boolean execute(String cmd)
    {
        // Split space but no quotes
        final String[] args = cmd.split(" (?=(([^'\"]*['\"]){2})*[^'\"]*$)");

        if (args.length < 1)
            return false;

        final Script script = getScriptByName(args[0]);
        if (script == null)
            return false;

        script.run(Arrays.copyOfRange(args, 1, args.length));
        return true;
    }
}

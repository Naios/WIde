package wide.scripts;

import wide.core.framework.extensions.scripts.Script;
import wide.scripts.fetch.Fetch;
import wide.scripts.hello.HelloWorld;
import wide.scripts.help.Help;
import wide.scripts.printdbc.PrintDBC;
import wide.scripts.test.Test;

public enum ScriptDefinition
{
    // Define new Modules here
    SCRIPT_HELP("help", Help.class),
    SCRIPT_HELLO("hello", HelloWorld.class),
    SCRIPT_TEST("test", Test.class),
    SCRIPT_FETCH("fetch", Fetch.class),
    SCRIPT_PRINTDBC("printdbc", PrintDBC.class);

    private final String uuid;

    private final Class<? extends Script> type;

    private ScriptDefinition(String uuid, Class<? extends Script> type)
    {
        this.uuid = uuid;
        this.type = type;
    }

    public String getUUID()
    {
        return uuid;
    }

    public Script newInstance() throws Exception
    {
        return type.getDeclaredConstructor(ScriptDefinition.class).newInstance(this);
    }
}


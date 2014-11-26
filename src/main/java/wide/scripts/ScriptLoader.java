package wide.scripts;

import wide.core.framework.extensions.scripts.Script;
import wide.scripts.fetch.Fetch;
import wide.scripts.hello.HelloWorld;
import wide.scripts.help.Help;
import wide.scripts.printdbc.PrintDBC;
import wide.scripts.test.Test;

public class ScriptLoader
{
    // Module Loader, insert new Module instances here
    private final Script[] SCRIPTS =
    {
        new Help(),
        new HelloWorld(),
        new Test(),
        new Fetch(),
        new PrintDBC()
    };

    public Script[] getExtensions()
    {
        return SCRIPTS;
    }
}

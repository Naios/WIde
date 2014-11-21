package wide.scripts;

import wide.core.framework.extensions.ExtensionLoader;
import wide.core.framework.extensions.scripts.Script;
import wide.scripts.fetch.Fetch;
import wide.scripts.hello.HelloWorld;
import wide.scripts.test.Test;

public class ScriptLoader implements ExtensionLoader<Script>
{
    // Module Loader, insert new Module instances here
    private final Script[] SCRIPTS =
    {
        new HelloWorld(),
        new Fetch(),
        new Test()
    };

    @Override
    public Script[] getExtensions()
    {
        return SCRIPTS;
    }
}

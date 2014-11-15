package wide.scripts;

import wide.core.framework.extensions.ExtensionLoader;
import wide.core.framework.extensions.scripts.Script;
import wide.scripts.hello.HelloWorld;

public class ScriptLoader implements ExtensionLoader<Script>
{
    // Module Loader, insert new Module instances here
    private final Script[] SCRIPTS =
    {
        new HelloWorld()
    };

    @Override
    public Script[] getExtensions()
    {
        return SCRIPTS;
    }
}

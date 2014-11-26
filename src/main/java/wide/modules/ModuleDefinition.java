package wide.modules;

import wide.core.framework.extensions.modules.Module;
import wide.modules.gui.GUI;
import wide.modules.terminal.Terminal;

public enum ModuleDefinition
{
    // Define new Modules here
    MODULE_GUI("gui", GUI.class),
    MODULE_TERMINAL("terminal", Terminal.class);

    private final String uuid;

    private final Class<? extends Module> type;

    private ModuleDefinition(String uuid, Class<? extends Module> type)
    {
        this.uuid = uuid;
        this.type = type;
    }

    public String getUUID()
    {
        return uuid;
    }

    public Module newInstance() throws Exception
    {
        return type.getDeclaredConstructor(ModuleDefinition.class).newInstance(this);
    }
}

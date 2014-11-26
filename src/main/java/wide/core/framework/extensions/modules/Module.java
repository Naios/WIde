package wide.core.framework.extensions.modules;

import java.util.LinkedList;
import java.util.List;

import wide.core.framework.extensions.Extension;
import wide.modules.ModuleDefinition;

public abstract class Module implements Extension
{
    private final ModuleDefinition definition;

    private final List<ModuleDefinition> requires = new LinkedList<ModuleDefinition>();

    public Module(ModuleDefinition definition)
    {
        this.definition = definition;
    }

    public abstract boolean validate();

    public abstract void onEnable();

    public abstract void onDisable();

    @Override
    public String getUUID()
    {
        return definition.getUUID();
    }

    protected Module requires(ModuleDefinition definition)
    {
        requires.add(definition);
        return this;
    }
}

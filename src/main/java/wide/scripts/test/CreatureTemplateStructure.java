package wide.scripts.test;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.StringProperty;
import wide.core.framework.storage.server.ServerStorageEntry;
import wide.core.framework.storage.server.ServerStorageStructure;

public class CreatureTemplateStructure extends ServerStorageStructure implements CreatureTemplate
{
    protected CreatureTemplateStructure()
    {
        super("creature_template");
    }

    @ServerStorageEntry(key=true)
    private ReadOnlyIntegerProperty entry;

    @Override
    public ReadOnlyIntegerProperty entry()
    {
        return entry;
    }

    @ServerStorageEntry
    private StringProperty name;

    @Override
    public StringProperty name()
    {
        return name;
    }

    @ServerStorageEntry
    private IntegerProperty unit_flags;

    @Override
    public IntegerProperty unit_flags()
    {
        return unit_flags;
    }
}

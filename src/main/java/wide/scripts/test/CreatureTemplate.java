package wide.scripts.test;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.StringProperty;
import wide.core.framework.storage.StorageName;
import wide.core.framework.storage.server.ServerStorageStructure;

@StorageName(name="creature_template")
public abstract class CreatureTemplate extends ServerStorageStructure
{
    public abstract ReadOnlyIntegerProperty entry();

    public abstract StringProperty name();

    public abstract IntegerProperty unit_flags();
}

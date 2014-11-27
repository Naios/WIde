package com.github.naios.wide.scripts.test;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.StringProperty;
import com.github.naios.wide.core.framework.storage.StorageName;
import com.github.naios.wide.core.framework.storage.server.ServerStorageStructure;

@StorageName(name="creature_template")
public abstract class CreatureTemplate extends ServerStorageStructure
{
    public abstract ReadOnlyIntegerProperty entry();

    public abstract StringProperty name();

    public abstract IntegerProperty unit_flags();
}

package com.github.naios.wide.core.framework.storage.server.helper;

import java.lang.reflect.Field;

import com.github.naios.wide.core.framework.storage.server.ServerStorageStructure;

public class ObservableValueStorageInfo
{
    private final ServerStorageStructure structure;

    private final Field field;

    public ObservableValueStorageInfo(final ServerStorageStructure structure, final Field field)
    {
        this.structure = structure;
        this.field = field;
    }

    public String getTableName()
    {
        return structure.getOwner().getTableName();
    }

    public ServerStorageStructure getStructure()
    {
        return structure;
    }

    public Field getField()
    {
        return field;
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final ObservableValueStorageInfo other = (ObservableValueStorageInfo) obj;
        if (field == null)
        {
            if (other.field != null)
                return false;
        }
        else if (!field.equals(other.field))
            return false;
        if (structure == null)
        {
            if (other.structure != null)
                return false;
        }
        else if (!structure.equals(other.structure))
            return false;
        return true;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((field == null) ? 0 : field.hashCode());
        result = prime * result
                + ((structure == null) ? 0 : structure.hashCode());
        return result;
    }
}
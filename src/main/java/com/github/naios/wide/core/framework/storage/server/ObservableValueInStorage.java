package com.github.naios.wide.core.framework.storage.server;

import java.lang.reflect.Field;

public class ObservableValueInStorage
{
    private final String tableName;

    private final ServerStorageStructure structure;

    private final Field field;

    public ObservableValueInStorage(final String tableName,
            final ServerStorageStructure structure, final Field field)
    {
        this.tableName = tableName;
        this.structure = structure;
        this.field = field;
    }

    public String getTableName()
    {
        return tableName;
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
        final ObservableValueInStorage other = (ObservableValueInStorage) obj;
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
        if (tableName == null)
        {
            if (other.tableName != null)
                return false;
        }
        else if (!tableName.equals(other.tableName))
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
        result = prime * result
                + ((tableName == null) ? 0 : tableName.hashCode());
        return result;
    }
}
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
package com.github.naios.wide.database_pool.internal;

import java.sql.SQLException;

import javafx.beans.property.ObjectProperty;

import com.github.naios.wide.database_pool.Database;
import com.github.naios.wide.database_pool.DatabasePoolService;

public final class DatabasePoolServiceImpl
    implements DatabasePoolService
{

    @Override
    public ObjectProperty<Database> requestConnection(final String id)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ObjectProperty<Database> registerConnection(final String id,
            final String endpoint, final String user, final String password, final String table)
            throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

}

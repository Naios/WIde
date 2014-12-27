
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.configuration.internal.config;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import com.github.naios.wide.configuration.DatabaseConfig;

public class DatabaseConfigImpl implements DatabaseConfig
{
    private StringProperty id, name, host, user, password, schema;

    /**
     * connection gets late initialized and bound to user@host at first usage
     */
    private StringProperty connection = null;

    @Override
    public StringProperty id()
    {
        return id;
    }

    @Override
    public StringProperty name()
    {
        return name;
    }

    @Override
    public StringProperty host()
    {
        return host;
    }

    @Override
    public StringProperty user()
    {
        return user;
    }

    @Override
    public StringProperty password()
    {
        return password;
    }

    @Override
    public StringProperty schema()
    {
        return schema;
    }

    @Override
    public StringProperty connection()
    {
        // We need to late bind the connection property to user and host
        // because user & host might be null sometimes
        if (connection == null)
        {
            connection = new SimpleStringProperty();
            connection.bind(Bindings.concat(user, "@", host));
        }

        return connection;
    }
}

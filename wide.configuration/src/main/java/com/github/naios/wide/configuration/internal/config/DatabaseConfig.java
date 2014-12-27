
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.configuration.internal.config;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class DatabaseConfig
{
    private final StringProperty id, name, host, user, password, schema;

    public DatabaseConfig()
    {
        this.id = new SimpleStringProperty();
        this.name = new SimpleStringProperty();
        this.host = new SimpleStringProperty();
        this.user = new SimpleStringProperty();
        this.password = new SimpleStringProperty();
        this.schema = new SimpleStringProperty();
    }

    public StringProperty id()
    {
        return id;
    }

    public StringProperty name()
    {
        return name;
    }

    public StringProperty host()
    {
        return host;
    }

    public StringProperty user()
    {
        return user;
    }

    public StringProperty password()
    {
        return password;
    }

    public StringProperty schema()
    {
        return schema;
    }

    public String getEndpointString()
    {
        return String.format("%s@%s", user.get(), host.get());
    }
}


/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.core.session.config;

import javafx.beans.property.StringProperty;

class DatabaseConfig
{
    private StringProperty id, name, host, user, password, schema;

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
}

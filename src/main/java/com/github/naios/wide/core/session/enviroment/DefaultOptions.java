
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.core.session.enviroment;

import org.apache.commons.cli.Options;

@SuppressWarnings("serial")
public abstract class DefaultOptions extends Options
{
    public DefaultOptions()
    {
        configure();
    }

    public abstract void configure();
}


/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.scripts.test;

public class ServerTableImplementation implements ServerTable
{
    @Override
    public void delete()
    {
        System.out.println(String.format("DEBUG: %s", "Invoce delete!"));
    }
}

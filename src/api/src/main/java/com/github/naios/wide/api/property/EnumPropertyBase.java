package com.github.naios.wide.api.property;

/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

public interface EnumPropertyBase<T extends Enum<T>>
{
    public Class<T> getEnumClass();

    public T[] getEnumConstants();

    public T getEnumConstant(int index);

    public int getEnumClassSize();

    public String getValueAsHex();
}

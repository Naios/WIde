
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.api.config.alias;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;

public interface Alias
{
    /**
     * @return Returns the {@link AliasType} of the alias
     */
    public AliasType getAliasType();

    /**
     * Needed in enum aliases
     * @return Returns the associated class defined in the entities bundle.
     */
    public Class<?> getTarget();

    /**
     * Needed in enum aliases
     * @return Returns the name of the associated class defined in the entities bundle.
     */
    public StringProperty targetName();

    /**
     * Needed in server namestorages
     * @return Returns the entry column name
     */
    public StringProperty entryColumn();

    /**
     * Needed in client namestorages
     * @return Returns the entry column index
     */
    public IntegerProperty entryColumnIndex();

    /**
     * Needed in server namestorages
     * @return Returns the name column name
     */
    public StringProperty nameColumn();

    /**
     * Needed in client namestorages
     * @return Returns the name column index
     */
    public IntegerProperty nameColumnIndex();

    /**
     * Prefixes are added in front of the name (useful for building sql variables)<br>
     * If the prefix string is empty no prefix is added.
     *
     * @return Returns the prefix string
     */
    public StringProperty prefix();

    /**
     * Defines a name used if the requested value is 0.<br>
     * If the zeroName string is empty no special name on zero values is used.
     *
     * @return Returns the zeroName string
     */
    public StringProperty zeroName();

    /**
     * Use a special prefix after the normal prefix id no associated name for the requested value was found (such as unk)<br>
     * The fail string is formatted with {@link String#format} where the value is passed as paremeter (makes it possible to use "unk %s" for example)<br>
     * If the failString string is empty no special prefix is used on fail.
     *
     * @return Returns the failPrefix string
     */
    public StringProperty failPrefix();
}

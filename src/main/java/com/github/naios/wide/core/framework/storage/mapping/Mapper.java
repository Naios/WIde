
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.core.framework.storage.mapping;

import com.google.common.reflect.TypeToken;

public interface Mapper<FROM, TO extends Mapping<BASE>, BASE>
{
    public TO map(FROM from);

    @SuppressWarnings("rawtypes")
    public Mapper<FROM, TO, BASE> registerAdapter(final TypeToken type,
            final MappingAdapter<FROM, ? extends BASE> adapter);
}

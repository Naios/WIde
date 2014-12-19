
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.core.framework.storage.client;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class ClientStorageStringTable
{
    private final Map<Integer, String> strings
    = new HashMap<>();

    public ClientStorageStringTable(final ByteBuffer buffer, final int offset)
    {
        // Fill String table
        // String of the String Block offset begin always at StringBlockOffset + 1
        buffer.position(offset + 1);

        while (buffer.remaining() > 1)
        {
            // Push buffer forward to the next string
            final int current = buffer.position();
            while (buffer.get() != 0);

            final int length = buffer.position() - offset - 1;

            final byte[] bytes = new byte[length];

            // TODO Find a better way
            // buffer.get(bytes, begin, length); seems to be bugged hard!
            buffer.position(begin);
            for (int i = 0; i < length; ++i)
                bytes[i] = buffer.get();


            strings.put(offset, new StringInBufferCached(buffer, offset, buffer.position() - offset - 1));
        }

    }
}

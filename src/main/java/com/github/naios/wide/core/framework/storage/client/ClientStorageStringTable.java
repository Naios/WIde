
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.core.framework.storage.client;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class ClientStorageStringTable
{
    private final Map<Integer, String> strings
    = new HashMap<>();

    public ClientStorageStringTable(final ByteBuffer buffer, final int stringTableOffset)
    {
        // Fill String table
        // String of the String Block offset begin always at StringBlockOffset + 1
        buffer.position(stringTableOffset + 1);

        final ByteArrayOutputStream stream = new ByteArrayOutputStream();

        while (buffer.remaining() > 1)
        {
            stream.reset();

            final int offset = buffer.position();

            // Read null terminated string in buffer
            byte cur;
            while ((cur = buffer.get()) != 0)
                stream.write(cur);

            // TODO Do we need to set the encoding to utf8?
            strings.put(offset, new String(stream.toByteArray()));
        }
    }
}

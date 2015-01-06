
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.api.util;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

public class RandomUtil
{
    private static final Random random = new SecureRandom();

    public static int getInt(final int min, final int max)
    {
        return random.nextInt(max - min + 1) + min;
    }

    public static String getString()
    {
        return new BigInteger(130, random).toString(32);
    }

    public static String getString(final int length)
    {
        final String str = getString();
        return str.substring(0, Math.min(str.length(), length));
    }
}

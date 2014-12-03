package com.github.naios.wide.core.framework.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class FormatterWrapper
{
    public enum Options
    {
        NO_STRING_ENCLOSURE,
        NO_FLOAT_DOUBLE_POSTFIX,
    }

    final Object obj;

    final Set<Options> options = new HashSet<>();

    public FormatterWrapper(final Object obj, final Options... options)
    {
        this.obj = obj;

        this.options.addAll(Arrays.asList(options));
    }

    @Override
    public String toString()
    {
        if (obj instanceof Integer)
            return obj.toString();
        else if ((obj instanceof Float) && !hasOption(Options.NO_FLOAT_DOUBLE_POSTFIX))
            return obj.toString() + "f";
        else if ((obj instanceof Double) && !hasOption(Options.NO_FLOAT_DOUBLE_POSTFIX))
            return obj.toString() + "d";
        else if ((obj instanceof String) && !hasOption(Options.NO_STRING_ENCLOSURE))
            return "\"" + obj.toString() + "\"";
        else
            return obj.toString();
    }

    public boolean hasOption(final Options option)
    {
        return options.contains(option);
    }
}

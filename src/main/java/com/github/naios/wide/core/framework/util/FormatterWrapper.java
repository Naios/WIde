package com.github.naios.wide.core.framework.util;

public class FormatterWrapper
{
    final Object obj;

    public FormatterWrapper(final Object obj)
    {
        this.obj = obj;
    }

    @Override
    public String toString()
    {
        if (obj instanceof Integer)
            return obj.toString();
        else if (obj instanceof Float)
            return obj.toString() + "f";
        else if (obj instanceof Double)
            return obj.toString() + "f";
        else if (obj instanceof String)
            return "\"" + obj.toString() + "\"";
        else
            return obj.toString();
    }
}

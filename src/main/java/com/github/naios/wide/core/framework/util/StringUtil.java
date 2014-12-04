package com.github.naios.wide.core.framework.util;

public class StringUtil
{
    public static String convertStringToVarName(final String str)
    {
        // TODO Find a better regex for this
        return str
                .toUpperCase()
                .replace("[", "")
                .replace("]", "")
                .replace("-", "")
                .replace(" ", "_")
                .replace(")", "")
                .replace("(", "")
                .replace(":", "")
                .replace(".", "")
                .replaceAll("_{2}", "_")
                .replaceAll("[()'@\"]", "");
    }
}

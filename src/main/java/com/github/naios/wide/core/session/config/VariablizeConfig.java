package com.github.naios.wide.core.session.config;

import javafx.beans.property.BooleanProperty;

public class VariablizeConfig
{
    private BooleanProperty custom, names, enums, flags;

    public BooleanProperty custom()
    {
        return custom;
    }

    public BooleanProperty names()
    {
        return names;
    }

    public BooleanProperty enums()
    {
        return enums;
    }

    public BooleanProperty flags()
    {
        return flags;
    }
}

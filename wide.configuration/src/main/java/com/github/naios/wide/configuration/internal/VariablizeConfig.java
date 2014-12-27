package com.github.naios.wide.core.session.config;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class VariablizeConfig
{
    private final BooleanProperty custom, names, enums, flags;

    public VariablizeConfig()
    {
        this.custom = new SimpleBooleanProperty();
        this.names = new SimpleBooleanProperty();
        this.enums = new SimpleBooleanProperty();
        this.flags = new SimpleBooleanProperty();
    }

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

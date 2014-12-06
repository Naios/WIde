package com.github.naios.wide.scripts.hello;

import java.util.Arrays;

import com.github.naios.wide.core.framework.extensions.scripts.Script;
import com.github.naios.wide.scripts.ScriptDefinition;

public class HelloWorld extends Script
{
    public HelloWorld(final ScriptDefinition definition)
    {
        super(definition);
    }

    @Override
    public void run(final String[] args)
    {
        System.out.println("Hello World!");

        if (args.length > 0)
            System.out.println("\tWith Arguments: " + Arrays.toString(args));
    }

    @Override
    public String getUsage()
    {
        return "";
    }
}

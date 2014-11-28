package com.github.naios.wide.scripts.test;

import java.util.Arrays;
import java.util.List;

import com.github.naios.wide.core.framework.extensions.scripts.Script;
import com.github.naios.wide.core.framework.storage.server.ServerStorage;
import com.github.naios.wide.core.session.database.DatabaseType;
import com.github.naios.wide.scripts.ScriptDefinition;

/**
 * Simple testing script, use this as playground.
 * Don't commit its content in the master branch!
 */
public class Test extends Script
{
    public Test(final ScriptDefinition definition)
    {
        super(definition);
    }

    @Override
    public void run(final String[] args)
    {
        System.out.println(String.format("Running %s script with args %s.",
                toString(), Arrays.toString(args)));

        usePlayground(args);
    }

    @Override
    public String getUsage()
    {
        return "";
    }

    // Playground begin (only commit it in sub-branches to test stuff!)
    private void usePlayground(final String[] args)
    {
        final ServerStorage<CreatureTemplate> table =
                new ServerStorage<>(CreatureTemplateStructure.class, DatabaseType.WORLD);

        final CreatureTemplate entry = table.get(41378);

        // final FlagVersionedProperty<UnitFlags> flags =
        //         new SimpleFlagVersionedProperty<>();

        System.out.println(entry + "\n");

        final List<CreatureTemplate> list = table.getFromWhereQuery("entry between 0 and 200");
        for (final CreatureTemplate c : list)
        {
            System.out.println(c);
        }
    }
}

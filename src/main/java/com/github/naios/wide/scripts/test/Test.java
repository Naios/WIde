package com.github.naios.wide.scripts.test;

import java.util.Arrays;
import java.util.List;

import com.github.naios.wide.core.framework.entities.client.TaxiNodes;
import com.github.naios.wide.core.framework.entities.server.CreatureTemplate;
import com.github.naios.wide.core.framework.extensions.scripts.Script;
import com.github.naios.wide.core.framework.game.UnitFlags;
import com.github.naios.wide.core.framework.storage.client.ClientStorage;
import com.github.naios.wide.core.framework.storage.client.ClientStorageSelector;
import com.github.naios.wide.core.framework.storage.server.ServerStorage;
import com.github.naios.wide.core.framework.storage.server.ServerStorageChangeHolder;
import com.github.naios.wide.core.framework.util.FlagUtil;
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
        final ClientStorage<TaxiNodes> taxiNodes =
                new ClientStorageSelector<TaxiNodes>(TaxiNodesStructure.class).select();

        int count = 0;
        for (final TaxiNodes nodes : taxiNodes)
            if (++count > 5)
                break;
            else
                System.out.println(nodes);

        final ServerStorage<CreatureTemplate> table =
                new ServerStorage<>(CreatureTemplateStructure.class, DatabaseType.WORLD);

        final CreatureTemplate entry = table.get(CreatureTemplate.createKey(41378));

        // final FlagVersionedProperty<UnitFlags> flags =
        //         new SimpleFlagVersionedProperty<>();

        System.out.println(entry + "\n");

        final List<CreatureTemplate> list = table.getWhere("entry > 0 and unit_flags != 0 LIMIT 20");
        for (final CreatureTemplate c : list)
            System.out.println(c);

        // Check for same reference
        final CreatureTemplate e1 = table.get(CreatureTemplate.createKey(491));
        final CreatureTemplate e2 = table.get(CreatureTemplate.createKey(491));
        System.out.println(e1 == e2);

        final CreatureTemplate e3 = table.getWhere("entry=%d", 491).get(0);
        System.out.println(e2 == e3);

        final CreatureTemplate e4 = table.getWhere("name=%s", "Quartermaster Lewis").get(0);
        System.out.println(e3 == e4);

        final CreatureTemplate e5 = table.getWhere("name=%s", "Maloriak").get(0);
        System.out.println(e4 != e5);
        System.out.println(entry == e5);

        // System.out.println(table);

        /*
        // Heavy hashing check
        final List<CreatureTemplate> l1 = table.getWhere("entry = entry");
        final List<CreatureTemplate> l2 = table.getWhere("entry = entry");

        for (final CreatureTemplate t : l2)
            if (!l1.contains(t))
                System.out.println("Error on " + t);
        */

        // Change Listener test

        System.out.println(FlagUtil.createFlag(UnitFlags.UNIT_FLAG_SERVER_CONTROLLED));
        System.out.println(FlagUtil.createFlag(UnitFlags.UNIT_FLAG_NON_ATTACKABLE));
        System.out.println(FlagUtil.createFlag(UnitFlags.UNIT_FLAG_DISABLE_MOVE));

        for (final CreatureTemplate te : list)
            System.out.println(te);

        System.out.println("Changes...");

        System.out.println(entry);
        entry.name().set("TestName");
        entry.unit_flags().addFlag(UnitFlags.UNIT_FLAG_SERVER_CONTROLLED);
        System.out.println(entry);
        entry.name().set("Sec Test");
        entry.unit_flags().addFlag(UnitFlags.UNIT_FLAG_NON_ATTACKABLE);
        System.out.println(entry);
        System.out.println(ServerStorageChangeHolder.instance());
        System.out.println("Reverting...");
        ServerStorageChangeHolder.instance().rollback(entry.name(), 2);
        ServerStorageChangeHolder.instance().revert(entry.unit_flags());
        System.out.println(entry);
        System.out.println(ServerStorageChangeHolder.instance());


        table.close();

        /*
        final NameStorage names = new DatabaseNameStorage("creature_template", "entry", "name");
        System.out.println(names);
        */

        /*
        final GameBuildMask mask = new GameBuildMask()
            .addUntil(GameBuild.V4_2_0_14480)
            .removeExpansion(Expansion.WRATH_OF_THE_LICH_KING)
            .removeRange(GameBuild.V5_0_5_16048, GameBuild.V5_4_1_17538)
            .add(GameBuild.V6_0_3_19103);

        System.out.println(mask.contains(GameBuild.V4_2_0_14480));
        System.out.println(!mask.contains(GameBuild.V3_3_5a_12340));
        System.out.println(!mask.contains(GameBuild.V5_0_5_16048));
        System.out.println(!mask.contains(GameBuild.V5_4_1_17538));
        System.out.println(mask.contains(GameBuild.V6_0_3_19103));
        */
    }
}


/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.scripts.test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import javafx.beans.value.ObservableValue;

import com.github.naios.wide.core.WIde;
import com.github.naios.wide.core.framework.entities.client.TaxiNodes;
import com.github.naios.wide.core.framework.entities.server.CreatureTemplate;
import com.github.naios.wide.core.framework.extensions.scripts.Script;
import com.github.naios.wide.core.framework.game.UnitClass;
import com.github.naios.wide.core.framework.game.UnitFlags;
import com.github.naios.wide.core.framework.storage.client.ClientStorage;
import com.github.naios.wide.core.framework.storage.client.ClientStorageSelector;
import com.github.naios.wide.core.framework.storage.client.ClientStorageStructure;
import com.github.naios.wide.core.framework.storage.client.UnknownClientStorageStructure;
import com.github.naios.wide.core.framework.storage.mapping.JsonMapper;
import com.github.naios.wide.core.framework.storage.mapping.Mapper;
import com.github.naios.wide.core.framework.storage.mapping.schema.SchemaCache;
import com.github.naios.wide.core.framework.storage.mapping.schema.TableSchema;
import com.github.naios.wide.core.framework.storage.mapping.templates.SQLToPropertyMappingAdapterHolder;
import com.github.naios.wide.core.framework.storage.name.NameStorage;
import com.github.naios.wide.core.framework.storage.name.NameStorageHolder;
import com.github.naios.wide.core.framework.storage.name.NameStorageType;
import com.github.naios.wide.core.framework.storage.server.ServerStorage;
import com.github.naios.wide.core.framework.storage.server.ServerStorageBaseImplementation;
import com.github.naios.wide.core.framework.storage.server.builder.SQLMaker;
import com.github.naios.wide.core.framework.util.FlagUtil;
import com.github.naios.wide.core.framework.util.RandomUtil;
import com.github.naios.wide.core.framework.util.StringUtil;
import com.github.naios.wide.core.session.database.DatabaseType;
import com.github.naios.wide.scripts.ScriptDefinition;
import com.google.common.collect.Iterables;

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

        // testStorages(args);
        testMapping(args);
    }

    @Override
    public String getUsage()
    {
        return "";
    }

    // Playground begin (only commit it in sub-branches to test stuff!)
    private void testStorages(final String[] args)
    {
        final ClientStorage<UnknownClientStorageStructure> sceneSript =
                new ClientStorageSelector<UnknownClientStorageStructure>
                    (UnknownClientStorageStructure.class, ClientStorageStructure.getPathOfFile("SceneScript.db2")).select();

        System.out.println(String.format("DEBUG: %s", Arrays.toString(sceneSript.getFieldTypes())));

        final ClientStorage<TaxiNodes> taxiNodes =
                new ClientStorageSelector<TaxiNodes>(TaxiNodesStructure.class).select();

        int count = 0;
        for (final TaxiNodes nodes : taxiNodes)
            if (++count > 5)
                break;
            else
                System.out.println(nodes);

        final ServerStorage<CreatureTemplate> table =
                new ServerStorage<>(CreatureTemplateStructure.class);

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
        System.out.println(table.getChangeHolder());
        System.out.println("Reverting...");
        table.getChangeHolder().rollback(entry.name(), 2);
        table.getChangeHolder().tryReset(entry.unit_flags());
        System.out.println(entry);
        System.out.println(table.getChangeHolder());

        System.out.println(String.format("%s", NameStorageHolder.instance().get("creature_name").getStorage().request(41378)));
        System.out.println(String.format("%s", NameStorageHolder.instance().get("spell_name").getStorage().request(13480)));
        System.out.println(String.format("%s", NameStorageHolder.instance().get("map_name").getStorage().request(189)));

        final NameStorageType mapType = NameStorageHolder.instance().get("spell_name");
        final NameStorage maps = mapType.getStorage();
        for (int i = 0; i < 30; ++i)
        {
            final String name = maps.request(i);
            if (name != null)
                System.out.println(String.format("SET @%s%s := %s;", mapType.getPrefix(), StringUtil.convertStringToVarName(name), i));
        }

        /*
        final NameStorage names = new DatabaseNameStorage("creature_template", "entry", "name");
        System.out.println(names);
        */

        /*
        final NameStorage names = new ClientNameStorage("Map.dbc", 0, 1);
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

        table.getChangeHolder().setScope("myscope","a simple create test comment.");
        final CreatureTemplate myentry = table.create(CreatureTemplate.createKey(100000));

        for (int step = 0; step < 7; ++step)
        {
            switch (step)
            {
                case 0:
                    myentry.name().set("Test King");
                    myentry.unit_flags().addFlag(UnitFlags.UNIT_FLAG_DISARMED);
                    myentry.unit_flags().addFlag(UnitFlags.UNIT_FLAG_IMMUNE_TO_NPC);
                    break;
                case 1:
                    myentry.getOwner().getChangeHolder().drop(myentry.unit_flags());
                    break;
                case 2:
                    myentry.unit_flags().addFlag(UnitFlags.UNIT_FLAG_IN_COMBAT);
                    break;
                case 4:
                    // Nothing will happen
                    table.getChangeHolder().free();
                    break;
                case 5:
                    myentry.getOwner().getChangeHolder().drop(myentry);
                    break;
                case 6:
                    break;
                case 7:
                    break;
            }

            System.out.println(table.getChangeHolder());
        }

        System.out.println(String.format(StringUtil.concat(" ",  new Object[] {"This", "is", "a", "test."})));

        System.out.println(SQLMaker.createComment("single line comment."));

        System.out.println(SQLMaker.createComment("this is\na multiline\ncomment."));

        final CreatureTemplate ct1 = table.get(CreatureTemplate.createKey(491));
        final CreatureTemplate ct2 = table.get(CreatureTemplate.createKey(41378));
        final CreatureTemplate ct3 = table.get(CreatureTemplate.createKey(151));
        final CreatureTemplate ct4 = table.get(CreatureTemplate.createKey(69));

        table.getChangeHolder().setScope("test scope", "simple modify test");

        ct1.name().set("blub");
        ct2.name().set("blub");

        ct3.unit_class().set(UnitClass.CLASS_ROGUE);

        ct3.kill_credit1().set(123456);
        table.getChangeHolder().setCustomVariable(ct3.kill_credit1(), "credit custom variable");

        table.getChangeHolder().setScope(
                "test flag scope",
                "some flag tests\nadds some strange flags to maloriak\n"
                        + "it only updates flags that have changed");

        ct2.unit_flags().removeFlag(UnitFlags.UNIT_FLAG_UNK_6);

        // This flag is not present in the database and won't lead to changes
        ct2.unit_flags().removeFlag(UnitFlags.UNIT_FLAG_DISARMED);

        ct2.unit_flags().addFlag(UnitFlags.UNIT_FLAG_IMMUNE_TO_NPC);
        ct2.unit_flags().addFlag(UnitFlags.UNIT_FLAG_NOT_SELECTABLE);

        table.getChangeHolder().setScope("delete scope", "deletes a creature template");
        ct4.delete();

        table.getChangeHolder().setScope("delete scope 2", "now we wanna delete multiple entrys, yay!");
        for (int i = 115; i < 120; ++i)
        {
            final CreatureTemplate deleteMe = table.get(CreatureTemplate.createKey(i));
            if (deleteMe != null)
                deleteMe.delete();
        }

        table.getChangeHolder().setScope("create scope 1", "creates one new creature template...");
        final CreatureTemplate myqueryentry = table.create(CreatureTemplate.createKey(1000000));
        myqueryentry.name().set("my test name");

        table.getChangeHolder().setScope("create scope 2", "creates 5 templates with random values");
        for (int i = 2000000; i < 2000005; ++i)
        {
            final CreatureTemplate template = table.create(CreatureTemplate.createKey(i));

            template.unit_class().set(RandomUtil.getInt(0, 3));
            template.unit_flags().set(RandomUtil.getInt(0, 30));
            template.kill_credit1().set(RandomUtil.getInt(0, 10000));
            template.name().set(RandomUtil.getString(RandomUtil.getInt(3, 15)));
        }

        System.out.println(table.getChangeHolder());
        System.out.println(table.getChangeHolder().getQuery());
        table.close();
    }

    private void testMapping(final String[] args)
    {
        final TableSchema mySchema = Iterables.get(SchemaCache.INSTANCE.getSchemaOfActiveEnviroment(DatabaseType.WORLD.getId()).getTables(), 0);


        final Mapper<ResultSet, ReducedCreatureTemplate, ObservableValue<?>> mapper =
                new JsonMapper<ResultSet, ReducedCreatureTemplate, ObservableValue<?>>
                    (mySchema, SQLToPropertyMappingAdapterHolder.INSTANCE,
                            ReducedCreatureTemplate.class, ServerStorageBaseImplementation.class);

        final Connection con = WIde.getDatabase().connection("world").get();

        final ResultSet result;
        try
        {
             result = con.createStatement().executeQuery("select * from creature_template limit 10");

        } catch (final SQLException e)
        {
            e.printStackTrace();
            return;
        }

        try
        {
            while (result.next())
            {
                final ReducedCreatureTemplate template = mapper.map(result);

                template.forEach(entry -> System.out.println(entry));
            }

        }
        catch (final SQLException e1)
        {
            e1.printStackTrace();
        }

        try
        {
            result.close();
        } catch (final SQLException e)
        {
            e.printStackTrace();
        }
    }
}

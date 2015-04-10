
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.framework.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import org.apache.felix.service.command.Descriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.naios.wide.api.config.ConfigService;
import com.github.naios.wide.api.config.main.EnviromentConfig;
import com.github.naios.wide.api.config.main.QueryTypeConfig;
import com.github.naios.wide.api.database.DatabasePoolService;
import com.github.naios.wide.api.entities.EntityService;
import com.github.naios.wide.api.framework.FrameworkService;
import com.github.naios.wide.api.framework.FrameworkWorkspace;
import com.github.naios.wide.api.framework.storage.client.ClientStorage;
import com.github.naios.wide.api.framework.storage.client.ClientStorageFormat;
import com.github.naios.wide.api.framework.storage.client.ClientStoragePolicy;
import com.github.naios.wide.api.framework.storage.server.SQLInfoProvider;
import com.github.naios.wide.api.framework.storage.server.SQLUpdateInfo;
import com.github.naios.wide.api.framework.storage.server.ServerStorage;
import com.github.naios.wide.api.framework.storage.server.ServerStorageStructure;
import com.github.naios.wide.api.util.FormatterWrapper;
import com.github.naios.wide.api.util.RandomUtil;
import com.github.naios.wide.entities.client.MapEntry;
import com.github.naios.wide.entities.enums.UnitClass;
import com.github.naios.wide.entities.enums.UnitFlags;
import com.github.naios.wide.entities.server.ServerStorageKeys;
import com.github.naios.wide.entities.server.world.CreatureTemplate;
import com.github.naios.wide.entities.server.world.QuestTemplate;
import com.github.naios.wide.framework.internal.storage.client.ClientStorageSelector;
import com.github.naios.wide.framework.internal.storage.server.builder.SQLUpdateInfoImpl;

public final class FrameworkServiceImpl implements FrameworkService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(FrameworkServiceImpl.class);

    private static ConfigService configService;

    private static DatabasePoolService databasePoolService;

    private static EntityService entityService;

    private static FrameworkServiceImpl INSTANCE;

    public void start()
    {
        INSTANCE = this;

        if (LOGGER.isDebugEnabled())
            LOGGER.debug("WIde Framework Service started!");
    }

    public void stop()
    {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("WIde Framework Service stopped!");
    }

    public void setConfigService(final ConfigService configService)
    {
        FrameworkServiceImpl.configService = configService;
    }

    public void setDatabasePoolService(final DatabasePoolService databasePoolService)
    {
        FrameworkServiceImpl.databasePoolService = databasePoolService;
    }

    public void setEntityService(final EntityService entityService)
    {
        FrameworkServiceImpl.entityService = entityService;
    }

    public static ConfigService getConfigService()
    {
        return configService;
    }

    public static DatabasePoolService getDatabasePoolService()
    {
        return databasePoolService;
    }

    public static EntityService getEntityService()
    {
        return entityService;
    }

    public static FrameworkServiceImpl getInstance()
    {
        return INSTANCE;
    }

    @Override
    public FrameworkWorkspace createWorkspaceFromEnviroment(final EnviromentConfig config)
    {
        return new FrameworkWorkspaceImpl(config);
    }

    private ClientStorage<?> getStorageOfCommand(final String name, final int policy)
    {
        if ((policy < 0) || (policy > ClientStoragePolicy.values().length))
            throw new RuntimeException(String.format("%s is not an ordinal of ClientStoragePolicy!"));

        final ClientStoragePolicy p = ClientStoragePolicy.values()[policy];

        return ClientStorageSelector.select(name, p);
    }

    @Descriptor("Returns any .dbc, .db2 or .adb storage (located in the data dir).")
    public Collection<Object[]> dbc(@Descriptor("The name of the storage (TaxiNodes.db2 for example)") final String name,
            @Descriptor("0 = POLICY_SCHEMA_ONLY, 1 = POLICY_ESTIMATE_ONLY, 2 = POLICY_SCHEMA_FIRST_ESTIMATE_AFTER (default)")
                /*FIXME @Parameter(names={"-p", "--policy"}, absentValue="2") */ final int policy)
    {
        final ClientStorage<?> storage = getStorageOfCommand(name, policy);
        final Object[][] array = storage.asObjectArray();
        final int height = array.length, width = array[0].length;

        final Collection<Object[]> result = new ArrayList<>(height);

        final List<String> names = new ArrayList<>(storage.getFieldNames());
        names.replaceAll(columnName -> new FormatterWrapper(columnName).toString());

        result.add(names.toArray());

        for (int y = 0; y < height; ++y)
        {
            for (int x = 0; x < width; ++x)
                if (array[y][x] instanceof String)
                    array[y][x] = new FormatterWrapper(array[y][x]);

            result.add(array[y]);
        }

        return result;
    }

    @Descriptor("Returns an estimated format of any .dbc, .db2 or .adb storage (located in the data dir).")
    public ClientStorageFormat dbcformat(@Descriptor("The name of the storage (TaxiNodes.db2 for example)") final String name)
    {
        return getStorageOfCommand(name, ClientStoragePolicy.POLICY_ESTIMATE_ONLY.ordinal()).getFormat();
    }

    public void test()
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    final FrameworkWorkspace workspace = createWorkspaceFromEnviroment(configService.getActiveEnviroment());

                    System.out.println("\nShort Example (Quest Template):\n");
                    final ServerStorage<QuestTemplate> questTemplate = workspace.requestServerStorage("world", "quest_template");

                    System.out.println("\n (Creature Template):\n");

                    questTemplate.getChangeTracker().setScope("q2", "Add a new quest");
                    final QuestTemplate newQuest = questTemplate.create(ServerStorageKeys.ofQuestTemplate(3000000));
                    newQuest.title().set("hey im new here, complete me please!");

                    // Receive query
                    System.out.println(workspace.createSQLBuilder(questTemplate.getChangeTracker()).toString());
                    System.out.println("\n");

                    // -------

                    System.out.println("\nShort Example (Creature Template):\n");
                    final ServerStorage<CreatureTemplate> creatureTemplate = workspace.requestServerStorage("world", "creature_template");
                    final CreatureTemplate maloriak = creatureTemplate.request(ServerStorageKeys.ofCreatureTemplate(41378)).get();

                    creatureTemplate.getChangeTracker().setScope("1", "Some flag changes \nand comment tests");
                    maloriak.unitFlags().removeFlag(UnitFlags.UNIT_FLAG_DISARMED);
                    maloriak.unitFlags().addFlag(UnitFlags.UNIT_FLAG_IMMUNE_TO_NPC);

                    creatureTemplate.getChangeTracker().setScope("2", "Set the name");
                    maloriak.name().set("hey im a test npc");

                    creatureTemplate.getChangeTracker().setScope("3", "Make npc");
                    final CreatureTemplate newNpc = creatureTemplate.create(ServerStorageKeys.ofCreatureTemplate(3000000));
                    newNpc.name().set("hey im new here!");

                    // Receive query
                    System.out.println(workspace.createSQLBuilder(creatureTemplate.getChangeTracker()).toString());

                    System.out.println("\n");

                    final ClientStorage<MapEntry> me = workspace.requestClientStorage("Map.dbc");
                    for (int i = 0; i < 15; ++i)
                        me.getEntry(i).ifPresent(e -> System.out.println(String.format("DEBUG: %s", e)));

                    final ServerStorage<CreatureTemplate> table = workspace.requestServerStorage("world", "creature_template");

                    System.out.println(String.format("DEBUG: %s", table.request(ServerStorageKeys.ofCreatureTemplate(41378))));

                    table.requestWhere("entry between 22000 and 30000 limit 20").forEach(template -> System.out.println(template));

                    final CreatureTemplate ct1 = table.request(ServerStorageKeys.ofCreatureTemplate(491)).get();
                    final CreatureTemplate ct2 = table.request(ServerStorageKeys.ofCreatureTemplate(41378)).get();
                    final CreatureTemplate ct3 = table.request(ServerStorageKeys.ofCreatureTemplate(151)).get();
                    final CreatureTemplate ct4 = table.request(ServerStorageKeys.ofCreatureTemplate(69)).get();

                    table.getChangeTracker().setScope("test scope", "simple modify test");

                    ct1.name().set("blub");

                    ct2.name().set("blub");

                    ct3.unitClass().set(UnitClass.CLASS_ROGUE);

                    ct3.killCredit1().set(123456);
                    table.getChangeTracker().setCustomVariable(ct3, ct3.killCredit1(), "credit custom variable");

                    table.getChangeTracker().setScope(
                            "test flag scope",
                            "some flag tests\nadds some strange flags to maloriak\n"
                                    + "it only updates flags that have changed");

                    ct2.unitFlags().removeFlag(UnitFlags.UNIT_FLAG_UNK_6);

                    // This flag is not present in the database and won't lead to changes
                    ct2.unitFlags().removeFlag(UnitFlags.UNIT_FLAG_DISARMED);

                    ct2.unitFlags().addFlag(UnitFlags.UNIT_FLAG_IMMUNE_TO_NPC);
                    ct2.unitFlags().addFlag(UnitFlags.UNIT_FLAG_NOT_SELECTABLE);
                    ct2.name().set("hey im here");

                    table.getChangeTracker().setScope("delete scope", "deletes a creature template");
                    ct4.delete();

                    table.getChangeTracker().setScope("delete scope 2", "now we wanna delete multiple entrys, yay!");
                    for (int i = 115; i < 120; ++i)
                        table.request(ServerStorageKeys.ofCreatureTemplate(i)).ifPresent(template -> template.delete());

                    table.getChangeTracker().setScope("create scope 1", "creates one new creature template...");
                    final CreatureTemplate myqueryentry = table.create(ServerStorageKeys.ofCreatureTemplate(1000000));
                    myqueryentry.name().set("my test name");

                    table.getChangeTracker().setScope("create scope 2", "creates 5 templates with random values");
                    for (int i = 2000000; i < 2000005; ++i)
                    {
                        final CreatureTemplate template = table.create(ServerStorageKeys.ofCreatureTemplate(i));

                        template.unitClass().set(template.unitClass().getEnumConstant(RandomUtil.getInt(0, 3)));
                        template.unitFlags().set(RandomUtil.getInt(0, 30));
                        template.killCredit1().set(RandomUtil.getInt(0, 10000));
                        template.name().set(RandomUtil.getString(RandomUtil.getInt(3, 15)));
                    }

                    System.out.println(table.getChangeTracker());
                    // System.out.println(table.getChangeTracker().getQuery());

                    final SQLInfoProvider sqlInfoProvider = new SQLInfoProvider()
                    {
                        @Override
                        public String getScopeOfStructure(final ServerStorageStructure structure)
                        {
                            return "";
                        }

                        @Override
                        public String getScopeOfEntry(final ServerStorageStructure structure,
                                final ReadOnlyProperty<?> property)
                        {
                            return "";
                        }

                        @Override
                        public String getCommentOfScope(final String scope)
                        {
                            return "";
                        }

                        @Override
                        public String getCustomVariable(
                                final ServerStorageStructure structure,
                                final ReadOnlyProperty<?> observable)
                        {
                            return null;
                        }
                    };

                    final List<SQLUpdateInfo> update = new ArrayList<>();

                    update.add(new SQLUpdateInfoImpl(ct2.getEntryByName("unit_flags"), 16));
                    update.add(new SQLUpdateInfoImpl(ct2.getEntryByName("name")));

                    final Collection<ServerStorageStructure> insert = new ArrayList<>();
                    insert.add(ct1);
                    insert.add(ct3);

                    final Collection<ServerStorageStructure> delete = new ArrayList<>();
                    insert.add(ct4);

                    final QueryTypeConfig falseConfig = new QueryTypeConfig()
                    {
                        private final BooleanProperty falseProperty = new SimpleBooleanProperty(false)
                        {
                            {
                                addListener(new ChangeListener<Boolean>()
                                {
                                    @Override
                                    public void changed(
                                            final ObservableValue<? extends Boolean> observable,
                                            final Boolean oldValue, final Boolean newValue)
                                    {
                                        throw new UnsupportedOperationException();
                                    }
                                });
                            }
                        };

                        @Override
                        public BooleanProperty alias()
                        {
                            return falseProperty;
                        }

                        @Override
                        public BooleanProperty flags()
                        {
                            return falseProperty;
                        }

                        @Override
                        public BooleanProperty enums()
                        {
                            return falseProperty;
                        }

                        @Override
                        public BooleanProperty custom()
                        {
                            return falseProperty;
                        }
                    };

                    System.out.println(workspace.createSQLBuilder(sqlInfoProvider, update, insert, delete, falseConfig, falseConfig, falseConfig));

                    System.out.println("\n--\n");

                    System.out.println(workspace.createSQLBuilder(table.getChangeTracker()));

                    System.out.println(table.getChangeTracker());

                    System.out.println(String.format("DEBUG: Entries deleted:"));
                    table.getChangeTracker().structuresDeleted().forEach(structure -> System.out.println(structure.history()));

                    System.out.println(String.format("DEBUG: Entries created:"));
                    table.getChangeTracker().structuresCreated().forEach(structure -> System.out.println(structure.history()));

                    final Optional<MapEntry> map =  me.getEntry(1);
                    map.ifPresent(m -> System.out.println(m));

                    System.out.println(String.format("DEBUG: Finished!"));
                }
                catch (final Throwable e)
                {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}

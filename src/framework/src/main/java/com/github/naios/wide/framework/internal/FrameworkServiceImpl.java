
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
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import org.apache.felix.service.command.Descriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.naios.wide.api.config.ConfigService;
import com.github.naios.wide.api.config.main.QueryConfig;
import com.github.naios.wide.api.config.main.QueryType;
import com.github.naios.wide.api.config.main.QueryTypeConfig;
import com.github.naios.wide.api.database.DatabasePoolService;
import com.github.naios.wide.api.entities.EntityService;
import com.github.naios.wide.api.framework.FrameworkService;
import com.github.naios.wide.api.framework.storage.client.ClientStorage;
import com.github.naios.wide.api.framework.storage.client.ClientStorageFormat;
import com.github.naios.wide.api.framework.storage.client.ClientStoragePolicy;
import com.github.naios.wide.api.framework.storage.client.ClientStorageStructure;
import com.github.naios.wide.api.framework.storage.server.ChangeTracker;
import com.github.naios.wide.api.framework.storage.server.SQLBuilder;
import com.github.naios.wide.api.framework.storage.server.SQLInfoProvider;
import com.github.naios.wide.api.framework.storage.server.SQLUpdateInfo;
import com.github.naios.wide.api.framework.storage.server.ServerStorage;
import com.github.naios.wide.api.framework.storage.server.ServerStorageStructure;
import com.github.naios.wide.api.util.FormatterWrapper;
import com.github.naios.wide.api.util.Pair;
import com.github.naios.wide.api.util.RandomUtil;
import com.github.naios.wide.entities.client.MapEntry;
import com.github.naios.wide.entities.enums.UnitClass;
import com.github.naios.wide.entities.enums.UnitFlags;
import com.github.naios.wide.entities.server.world.CreatureTemplate;
import com.github.naios.wide.entities.server.world.ServerStorageKeys;
import com.github.naios.wide.framework.internal.alias.AliasStorage;
import com.github.naios.wide.framework.internal.storage.client.ClientStorageSelector;
import com.github.naios.wide.framework.internal.storage.server.ChangeTrackerImpl;
import com.github.naios.wide.framework.internal.storage.server.ServerStorageImpl;
import com.github.naios.wide.framework.internal.storage.server.builder.SQLBuilderImpl;
import com.github.naios.wide.framework.internal.storage.server.builder.SQLUpdateInfoImpl;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public final class FrameworkServiceImpl implements FrameworkService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(FrameworkServiceImpl.class);

    private static ConfigService configService;

    private static DatabasePoolService databasePoolService;

    private static EntityService entityService;

    private final AliasStorage aliases = new AliasStorage();

    private static FrameworkServiceImpl INSTANCE;



    private final Cache<String, ClientStorage<? extends ClientStorageStructure>> clientStorages =
            CacheBuilder
                .newBuilder()
                .build();

    private final Cache<String, ChangeTrackerImpl> changeTracker =
            CacheBuilder
                .newBuilder()
                .build();

    private final Cache<Pair<String, String>, ServerStorage<? extends ServerStorageStructure>> serverStorages =
            CacheBuilder
                .newBuilder()
                .build();

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

    @SuppressWarnings("unchecked")
    @Override
    public <T extends ClientStorageStructure> ClientStorage<T> requestClientStorage(final String name)
    {
        try
        {
            return (ClientStorage<T>) clientStorages.get(name, () -> ClientStorageSelector.<T>select(name, ClientStoragePolicy.DEFAULT_POLICY));
        }
        catch (final ExecutionException e)
        {
            throw new RuntimeException(e);
        }
    }

    private ChangeTrackerImpl requestChangeTrackerImpl(final String databaseId)
    {
        try
        {
            return changeTracker.get(databaseId, () -> new ChangeTrackerImpl());
        }
        catch (final ExecutionException e)
        {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends ServerStorageStructure> ServerStorage<T> requestServerStorage(final String databaseId, final String name)
    {
        try
        {
            return (ServerStorage<T>) serverStorages.get(new Pair<>(databaseId, name), () -> new ServerStorageImpl<T>(databaseId, name, requestChangeTrackerImpl(databaseId)));
        }
        catch (final ExecutionException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String requestAlias(final String name, final int value)
    {
        return aliases.requestAlias(name, value);
    }

    @Override
    public Map<Integer, String> requestAllAliases(final String name)
    {
        return aliases.requestAllAliases(name);
    }

    @Override
    public void reloadAliases()
    {
        aliases.reloadAliases();
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
                    testMe();
                }
                catch (final Throwable e)
                {
                    e.printStackTrace();
                }
            }

            public void testMe()
            {
                final ClientStorage<MapEntry> me = requestClientStorage("Map.dbc");
                for (int i = 0; i < 15; ++i)
                    me.getEntry(i).ifPresent(e -> System.out.println(String.format("DEBUG: %s", e)));

                final ServerStorage<CreatureTemplate> table = requestServerStorage("world", "creature_template");

                System.out.println(String.format("DEBUG: %s", table.request(ServerStorageKeys.ofCreatureTemplate(41378))));

                table.requestWhere("entry between 22000 and 30000 limit 20").forEach(template -> System.out.println(template));

                final CreatureTemplate ct1 = table.request(ServerStorageKeys.ofCreatureTemplate(491)).get();
                final CreatureTemplate ct2 = table.request(ServerStorageKeys.ofCreatureTemplate(41378)).get();
                final CreatureTemplate ct3 = table.request(ServerStorageKeys.ofCreatureTemplate(151)).get();
                final CreatureTemplate ct4 = table.request(ServerStorageKeys.ofCreatureTemplate(69)).get();

                table.getChangeTracker().setScope("test scope", "simple modify test");

                ct1.name().set("blub");

                ct2.name().set("blub");

                ct3.unit_class().set(UnitClass.CLASS_ROGUE);

                ct3.kill_credit1().set(123456);
                table.getChangeTracker().setCustomVariable(ct3, ct3.kill_credit1(), "credit custom variable");

                table.getChangeTracker().setScope(
                        "test flag scope",
                        "some flag tests\nadds some strange flags to maloriak\n"
                                + "it only updates flags that have changed");

                ct2.unit_flags().removeFlag(UnitFlags.UNIT_FLAG_UNK_6);

                // This flag is not present in the database and won't lead to changes
                ct2.unit_flags().removeFlag(UnitFlags.UNIT_FLAG_DISARMED);

                ct2.unit_flags().addFlag(UnitFlags.UNIT_FLAG_IMMUNE_TO_NPC);
                ct2.unit_flags().addFlag(UnitFlags.UNIT_FLAG_NOT_SELECTABLE);
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

                    template.unit_class().set(template.unit_class().getEnumConstant(RandomUtil.getInt(0, 3)));
                    template.unit_flags().set(RandomUtil.getInt(0, 30));
                    template.kill_credit1().set(RandomUtil.getInt(0, 10000));
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

                System.out.println(createSQLBuilder(sqlInfoProvider, update, insert, delete, falseConfig, falseConfig, falseConfig));

                System.out.println("\n--\n");

                System.out.println(createSQLBuilder(table.getChangeTracker()));

                System.out.println(table.getChangeTracker());

                System.out.println(String.format("DEBUG: Entries deleted:"));
                table.getChangeTracker().structuresDeleted().forEach(structure -> System.out.println(structure.history()));

                System.out.println(String.format("DEBUG: Entries created:"));
                table.getChangeTracker().structuresCreated().forEach(structure -> System.out.println(structure.history()));

                final Optional<MapEntry> map =  me.getEntry(1);
                map.ifPresent(m -> System.out.println(m));

                System.out.println(String.format("DEBUG: Finished!"));
             }

        }).start();
    }

    @Override
    public SQLBuilder createSQLBuilder(final ChangeTracker changeTracker)
    {
        return createSQLBuilder(changeTracker,
                changeTracker.entriesChangedAsCollection(),
                changeTracker.structuresCreated(),
                changeTracker.structuresDeleted());
    }

    @Override
    public SQLBuilder createSQLBuilder(final ChangeTracker changeTracker,
                final QueryTypeConfig updateConfig,
                    final QueryTypeConfig insertConfig,
                        final QueryTypeConfig deleteConfig)
    {
        return createSQLBuilder(changeTracker,
                changeTracker.entriesChangedAsCollection(),
                changeTracker.structuresCreated(),
                changeTracker.structuresDeleted(),
                updateConfig,
                insertConfig,
                deleteConfig);
    }

    @Override
    public SQLBuilder createSQLBuilder(final SQLInfoProvider sqlInfoProvider,
            final Collection<SQLUpdateInfo> update,
            final Collection<ServerStorageStructure> insert,
            final Collection<ServerStorageStructure> delete)
    {
        final QueryConfig queryConfig = configService.getQueryConfig();
        return createSQLBuilder(sqlInfoProvider, update, insert, delete,
                queryConfig.getConfigForType(QueryType.UPDATE),
                queryConfig.getConfigForType(QueryType.INSERT),
                queryConfig.getConfigForType(QueryType.DELETE));
    }

    @Override
    public SQLBuilder createSQLBuilder(final SQLInfoProvider sqlInfoProvider,
            final Collection<SQLUpdateInfo> update,
            final Collection<ServerStorageStructure> insert,
            final Collection<ServerStorageStructure> delete,
            final QueryTypeConfig updateConfig,
            final QueryTypeConfig insertConfig,
            final QueryTypeConfig deleteConfig)
    {
        return new SQLBuilderImpl(sqlInfoProvider, update, insert, delete, updateConfig, insertConfig, deleteConfig);
    }
}

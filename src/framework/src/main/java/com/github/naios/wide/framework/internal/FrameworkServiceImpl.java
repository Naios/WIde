
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

import org.apache.felix.service.command.Descriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.naios.wide.api.config.ConfigService;
import com.github.naios.wide.api.database.DatabasePoolService;
import com.github.naios.wide.api.entities.EntityService;
import com.github.naios.wide.api.framework.FrameworkService;
import com.github.naios.wide.api.framework.storage.client.ClientStorage;
import com.github.naios.wide.api.framework.storage.client.ClientStorageFormat;
import com.github.naios.wide.api.framework.storage.client.ClientStoragePolicy;
import com.github.naios.wide.api.framework.storage.client.ClientStorageStructure;
import com.github.naios.wide.api.framework.storage.server.ServerStorage;
import com.github.naios.wide.api.framework.storage.server.ServerStorageKey;
import com.github.naios.wide.api.framework.storage.server.ServerStorageStructure;
import com.github.naios.wide.api.util.FormatterWrapper;
import com.github.naios.wide.api.util.RandomUtil;
import com.github.naios.wide.entities.client.MapEntry;
import com.github.naios.wide.entities.enums.UnitClass;
import com.github.naios.wide.entities.enums.UnitFlags;
import com.github.naios.wide.entities.server.world.CreatureTemplate;
import com.github.naios.wide.framework.internal.alias.AliasStorage;
import com.github.naios.wide.framework.internal.storage.client.ClientStorageSelector;
import com.github.naios.wide.framework.internal.storage.server.ServerStorageImpl;

public final class FrameworkServiceImpl implements FrameworkService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(FrameworkServiceImpl.class);

    private static ConfigService configService;

    private static DatabasePoolService databasePoolService;

    private static EntityService entityService;

    private final AliasStorage aliases = new AliasStorage();

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
    public <T extends ClientStorageStructure> ClientStorage<T> requestClientStorage(
            final String name)
    {
        // TODO @FrameworkIntegration
        return null;
    }

    @Override
    public <T extends ServerStorageStructure> ServerStorage<T> requestServerStorage(
            final String databaseId, final String name)
    {
        // TODO @FrameworkIntegration
        return null;
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

    private ClientStorage<?> getStorgeOfCommand(final String name, final int policy)
    {
        if ((policy < 0) || (policy > ClientStoragePolicy.values().length))
            throw new RuntimeException(String.format("%s is not an ordinal of ClientStoragePolicy!"));

        final ClientStoragePolicy p = ClientStoragePolicy.values()[policy];

        return new ClientStorageSelector<>(name, p).select();
    }

    @Descriptor("Returns any .dbc, .db2 or .adb storage (located in the data dir).")
    public Collection<Object[]> dbc(@Descriptor("The name of the storage (TaxiNodes.db2 for example)") final String name,
            @Descriptor("0 = POLICY_SCHEMA_ONLY, 1 = POLICY_ESTIMATE_ONLY, 2 = POLICY_SCHEMA_FIRST_ESTIMATE_AFTER (default)")
                /*FIXME @Parameter(names={"-p", "--policy"}, absentValue="2") */ final int policy)
    {
        final ClientStorage<?> storage = getStorgeOfCommand(name, policy);
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
        return getStorgeOfCommand(name, ClientStoragePolicy.POLICY_ESTIMATE_ONLY.ordinal()).getFormat();
    }

    public void test()
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                final ClientStorage<MapEntry> me = new ClientStorageSelector<MapEntry>("Map.dbc").select();
                System.out.println(String.format("DEBUG: %s", me));

                final ServerStorage<CreatureTemplate> table = new ServerStorageImpl<>("world", "creature_template");

                System.getProperties().forEach((key, value) -> System.out.println(String.format("%s = %s", key, value)));

                System.out.println(String.format("DEBUG: %s", table.get(new ServerStorageKey<>(41378))));

                table.getWhere("entry between 22000 and 30000 limit 20").forEach(template -> System.out.println(template));

                final CreatureTemplate ct1 = table.get(new ServerStorageKey<>(491));
                final CreatureTemplate ct2 = table.get(new ServerStorageKey<>(41378));
                final CreatureTemplate ct3 = table.get(new ServerStorageKey<>(151));
                final CreatureTemplate ct4 = table.get(new ServerStorageKey<>(69));

                // table.getchangeTracker().setScope("test scope", "simple modify test");

                ct1.name().set("blub");

                ct2.name().set("blub");

                ct3.unit_class().set(UnitClass.CLASS_ROGUE);

                ct3.kill_credit1().set(123456);
                // table.getchangeTracker().setCustomVariable(ct3.kill_credit1(), "credit custom variable");

                /* table.getchangeTracker().setScope(
                        "test flag scope",
                        "some flag tests\nadds some strange flags to maloriak\n"
                                + "it only updates flags that have changed");
                                */

                ct2.unit_flags().removeFlag(UnitFlags.UNIT_FLAG_UNK_6);

                // This flag is not present in the database and won't lead to changes
                ct2.unit_flags().removeFlag(UnitFlags.UNIT_FLAG_DISARMED);

                ct2.unit_flags().addFlag(UnitFlags.UNIT_FLAG_IMMUNE_TO_NPC);
                ct2.unit_flags().addFlag(UnitFlags.UNIT_FLAG_NOT_SELECTABLE);

                // table.getchangeTracker().setScope("delete scope", "deletes a creature template");
                ct4.delete();

                // table.getchangeTracker().setScope("delete scope 2", "now we wanna delete multiple entrys, yay!");
                for (int i = 115; i < 120; ++i)
                {
                    final CreatureTemplate deleteMe = table.get(new ServerStorageKey<>(i));
                    if (deleteMe != null)
                        deleteMe.delete();
                }

                // table.getchangeTracker().setScope("create scope 1", "creates one new creature template...");
                final CreatureTemplate myqueryentry = table.create(new ServerStorageKey<>(1000000));
                myqueryentry.name().set("my test name");

                // table.getchangeTracker().setScope("create scope 2", "creates 5 templates with random values");
                for (int i = 2000000; i < 2000005; ++i)
                {
                    final CreatureTemplate template = table.create(new ServerStorageKey<>(i));

                    template.unit_class().set(RandomUtil.getInt(0, 3));
                    template.unit_flags().set(RandomUtil.getInt(0, 30));
                    template.kill_credit1().set(RandomUtil.getInt(0, 10000));
                    template.name().set(RandomUtil.getString(RandomUtil.getInt(3, 15)));
                }

                // System.out.println(table.getchangeTracker());
                // System.out.println(table.getchangeTracker().getQuery());
             }

        }).start();
    }
}

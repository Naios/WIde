
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.framework.internal;

import org.apache.felix.service.command.Descriptor;

import com.github.naios.wide.api.config.ConfigService;
import com.github.naios.wide.api.config.main.ClientStorageConfig;
import com.github.naios.wide.api.database.DatabasePoolService;
import com.github.naios.wide.api.entities.EntityService;
import com.github.naios.wide.api.framework.FrameworkService;
import com.github.naios.wide.api.framework.storage.client.ClientStorage;
import com.github.naios.wide.api.framework.storage.client.ClientStoragePolicy;
import com.github.naios.wide.api.framework.storage.client.ClientStorageStructure;
import com.github.naios.wide.api.framework.storage.server.ServerStorage;
import com.github.naios.wide.api.framework.storage.server.ServerStorageKey;
import com.github.naios.wide.api.framework.storage.server.ServerStorageStructure;
import com.github.naios.wide.api.util.RandomUtil;
import com.github.naios.wide.entities.client.MapEntry;
import com.github.naios.wide.entities.enums.UnitClass;
import com.github.naios.wide.entities.enums.UnitFlags;
import com.github.naios.wide.entities.server.world.CreatureTemplate;
import com.github.naios.wide.framework.internal.storage.client.ClientStorageSelector;
import com.github.naios.wide.framework.internal.storage.server.ServerStorageImpl;

public final class FrameworkServiceImpl implements FrameworkService
{
    private static ConfigService configService;

    private static DatabasePoolService databasePoolService;

    private static EntityService entityService;

    public void start()
    {
        // Debug Code
        final ClientStorageConfig csc = getConfigService().getActiveEnviroment().getClientStorageConfig();

        System.out.println(String.format("DEBUG: %s", csc));
        System.out.println(String.format("DEBUG: %s = %s", csc.schemaPath().get(), csc.schema().get()));
        // ////

        System.out.println(String.format("DEBUG: %s", "FrameworkServiceImpl::start()"));
    }

    public void stop()
    {
        System.out.println(String.format("DEBUG: %s", "FrameworkServiceImpl::stop()"));
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

    private ClientStorage<?> getStorgeOfCommand(final String name, final int policy)
    {
        if ((policy < 0) || (policy > ClientStoragePolicy.values().length))
            throw new RuntimeException(String.format("%s is not an ordinal of ClientStoragePolicy!"));

        final ClientStoragePolicy p = ClientStoragePolicy.values()[policy];

        return new ClientStorageSelector<>(name, p).select();
    }

    @Descriptor("Shows any .dbc, .db2 or .adb storage (located in the data dir).")
    public void dbc(@Descriptor("The name of the storage (TaxiNodes.db2 for example)") final String name,
            @Descriptor("0 = POLICY_SCHEMA_ONLY, 1 = POLICY_ESTIMATE_ONLY, 2 = POLICY_SCHEMA_FIRST_ESTIMATE_AFTER") final int policy)
    {
        System.out.println(getStorgeOfCommand(name, policy));
    }

    @Descriptor("Shows an estimated format of any .dbc, .db2 or .adb storage (located in the data dir).")
    public void dbcformat(@Descriptor("The name of the storage (TaxiNodes.db2 for example)") final String name)
    {
        System.out.println(getStorgeOfCommand(name, ClientStoragePolicy.POLICY_ESTIMATE_ONLY.ordinal()).getFormat());
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
                    final CreatureTemplate deleteMe = table.get(new ServerStorageKey<>(i));
                    if (deleteMe != null)
                        deleteMe.delete();
                }

                table.getChangeHolder().setScope("create scope 1", "creates one new creature template...");
                final CreatureTemplate myqueryentry = table.create(new ServerStorageKey<>(1000000));
                myqueryentry.name().set("my test name");

                table.getChangeHolder().setScope("create scope 2", "creates 5 templates with random values");
                for (int i = 2000000; i < 2000005; ++i)
                {
                    final CreatureTemplate template = table.create(new ServerStorageKey<>(i));

                    template.unit_class().set(RandomUtil.getInt(0, 3));
                    template.unit_flags().set(RandomUtil.getInt(0, 30));
                    template.kill_credit1().set(RandomUtil.getInt(0, 10000));
                    template.name().set(RandomUtil.getString(RandomUtil.getInt(3, 15)));
                }

                System.out.println(table.getChangeHolder());
                System.out.println(table.getChangeHolder().getQuery());
             }

        }).start();
    }
}

package com.github.naios.wide.database_pool.internal;

import com.github.naios.wide.database_pool.DatabasePoolService;

public final class DatabasePoolServiceImpl
    implements DatabasePoolService
{
    @Override
    public void sayHello()
    {
        System.out.println("DatabasePoolServiceImpl sais hello!");
    }
}

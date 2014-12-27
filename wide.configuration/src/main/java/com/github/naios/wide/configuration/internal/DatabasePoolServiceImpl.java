package com.github.naios.wide.configuration.internal;

import com.github.naios.wide.configuration.DatabasePoolService;

public final class DatabasePoolServiceImpl
    implements DatabasePoolService
{
    @Override
    public void sayHello()
    {
        System.out.println("DatabasePoolServiceImpl sais hello!");
    }
}

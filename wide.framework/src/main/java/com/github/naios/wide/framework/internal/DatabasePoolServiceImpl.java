package com.github.naios.wide.framework.internal;

import com.github.naios.wide.framework.DatabasePoolService;

/**
 * Internal implementation of our example OSGi service
 */
public final class DatabasePoolServiceImpl
    implements DatabasePoolService
{
    @Override
    public void sayHello()
    {
        System.out.println("The pool says hey");

    }
    // implementation methods go here...
}


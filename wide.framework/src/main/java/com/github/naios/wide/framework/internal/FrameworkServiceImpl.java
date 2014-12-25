package com.github.naios.wide.framework.internal;

import com.github.naios.wide.framework.FrameworkService;

/**
 * Internal implementation of our example OSGi service
 */
public final class FrameworkServiceImpl
    implements FrameworkService
{
    @Override
    public void sayHello()
    {
        System.out.println("The framework sais hey!");

    }
}

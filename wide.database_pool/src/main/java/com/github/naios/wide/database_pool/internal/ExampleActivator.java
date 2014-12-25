package com.github.naios.wide.database_pool.internal;

import java.util.Dictionary;
import java.util.Properties;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * Extension of the default OSGi bundle activator
 */
public final class ExampleActivator
    implements BundleActivator
{
    /**
     * Called whenever the OSGi framework starts our bundle
     */
    @Override
    public void start( final BundleContext bc )
        throws Exception
    {
        System.out.println("Starting " + getClass().getCanonicalName());

        final Dictionary props = new Properties();
        // add specific service properties here...

        // System.out.println( "REGISTER com.github.naios.wide.ExampleService" );

        // Register our example service implementation in the OSGi service registry
        // bc.registerService(DatabasePoolService.class.getName(), new DatabasePoolServiceImpl(), props);
    }

    /**
     * Called whenever the OSGi framework stops our bundle
     */
    @Override
    public void stop( final BundleContext bc )
        throws Exception
    {
        System.out.println("Stopping " + getClass().getCanonicalName());

        // no need to unregister our service - the OSGi framework handles it for us
    }
}


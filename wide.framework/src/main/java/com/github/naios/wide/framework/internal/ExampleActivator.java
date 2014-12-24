package com.github.naios.wide.framework.internal;

import java.util.Dictionary;
import java.util.Properties;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.github.naios.wide.framework.ExampleService;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

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
        System.out.println("Stopping " + getClass().getCanonicalName());

        final Multimap<Integer, String> myMultimap = ArrayListMultimap.create();
        myMultimap.put(1, "guava");
        myMultimap.put(1, "resolved and");
        myMultimap.put(1, "works");

        System.out.println(String.format("DEBUG: %s", myMultimap.asMap().get(1).toString()));


        final Dictionary props = new Properties();
        // add specific service properties here...

        System.out.println("Stopping " + getClass().getCanonicalName());

        // Register our example service implementation in the OSGi service registry
        bc.registerService( ExampleService.class.getName(), new ExampleServiceImpl(), props );
    }

    /**
     * Called whenever the OSGi framework stops our bundle
     */
    @Override
    public void stop( final BundleContext bc )
        throws Exception
    {
        System.out.println( "STOPPING com.github.naios.wide" );

        // no need to unregister our service - the OSGi framework handles it for us
    }
}


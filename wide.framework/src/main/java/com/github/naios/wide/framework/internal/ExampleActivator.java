package com.github.naios.wide.framework.internal;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.github.naios.wide.framework.FrameworkService;
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
        System.out.println("Starting " + getClass().getCanonicalName());

        final Multimap<Integer, String> myMultimap = ArrayListMultimap.create();
        myMultimap.put(1, "guava");
        myMultimap.put(1, "resolved and");
        myMultimap.put(1, "works");

        System.out.println(String.format("DEBUG: %s", myMultimap.asMap().get(1).toString()));

        bc.registerService( FrameworkService.class.getName(), new FrameworkServiceImpl(), null );

        /*
        try
        {
            System.out.println(String.format("DEBUG: %s", "say..."));
            final ServiceReference ref = bc.getServiceReference(DatabasePoolService.class.getName());
            System.out.println(String.format("DEBUG: %s", "and..."));
            ((DatabasePoolService) bc.getService(ref)).sayHello();
        }
        catch (final Throwable e)
        {
            e.printStackTrace();
        }
    */
    }

    /**
     * Called whenever the OSGi framework stops our bundle
     */
    @Override
    public void stop( final BundleContext bc )
        throws Exception
    {
        System.out.println("Stopping " + getClass().getCanonicalName());

    }
}


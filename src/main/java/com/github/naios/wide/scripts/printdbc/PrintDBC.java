package com.github.naios.wide.scripts.printdbc;

import com.github.naios.wide.core.Constants;
import com.github.naios.wide.core.WIde;
import com.github.naios.wide.core.framework.extensions.scripts.Script;
import com.github.naios.wide.core.framework.storage.client.ClientStorage;
import com.github.naios.wide.core.framework.storage.client.ClientStorageSelector;
import com.github.naios.wide.scripts.ScriptDefinition;

public class PrintDBC extends Script
{
    public PrintDBC(ScriptDefinition definition)
    {
        super(definition);
    }

    @Override
    public void run(String[] args)
    {
        final String path =
                WIde.getConfig().getProperty(Constants.PROPERTY_DIR_DBC).get() + "/" + args[0];

        try
        {
            final ClientStorage<UnknownStructure> clientStorage =
                    new ClientStorageSelector<UnknownStructure>(UnknownStructure.class, path).select();

            System.out.println(clientStorage.toString());
        }
        catch (final Exception e)
        {
            e.printStackTrace();
            System.out.println("Something went wrong...");
        }
    }

    @Override
    public String getUsage()
    {
        return "{.dbc, .db2 or .adb file in your dbc directory you want to view.}";
    }
}
package wide.scripts.printdbc;

import wide.core.Constants;
import wide.core.WIde;
import wide.core.framework.extensions.scripts.Script;
import wide.core.framework.storage.client.ClientStorage;
import wide.core.framework.storage.client.ClientStorageSelector;
import wide.scripts.ScriptDefinition;

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
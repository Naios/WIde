package wide.scripts.readscenescipt;

import java.io.File;
import java.io.FileWriter;

import wide.core.Constants;
import wide.core.WIde;
import wide.core.framework.extensions.scripts.Script;
import wide.core.framework.storage.client.ClientStorage;
import wide.core.framework.storage.client.ClientStorageEntry;
import wide.core.framework.storage.client.ClientStorageSelector;
import wide.core.framework.storage.client.ClientStorageStructure;
import wide.core.framework.storage.client.ClientStorageStructureCreate;

@ClientStorageStructure(filename="SceneScript.db2")
class SceneScriptStructure
{
    @ClientStorageEntry(idx=0, name="Entry", key=true)
    private int entry;

    @ClientStorageEntry(idx=1, name="Script Name")
    private String name;

    @ClientStorageEntry(idx=2, name="Script in Lua")
    private String script;

    public int getEntry()
    {
        return entry;
    }

    public String getName()
    {
        return name;
    }

    public String getScript()
    {
        return script;
    }
}

public class ReadSceneScript extends Script
{
    public ReadSceneScript()
    {
        super("readscenescipt");
    }

    private final static String DIR = "scenescript";

    private final static String FILENAME = "SceneScript.db2";

    @Override
    public void run(String[] args)
    {
        String targetdir = WIde.getEnviroment().getPath() + "/" + WIde.getConfig().getProperty(Constants.PROPERTY_DIR_CACHE).get();

        WIde.getEnviroment().createDirectory(targetdir);

        targetdir = targetdir + "/" + DIR;

        final File dir = new File(targetdir);

        if (!dir.exists() || !dir.isDirectory())
            dir.delete();

        dir.mkdir();


        ClientStorage<SceneScriptStructure> db2 = null;

        try
        {
            db2 = new ClientStorageSelector<SceneScriptStructure>( WIde.getConfig().getProperty(Constants.PROPERTY_DIR_DBC).get() + "/" + FILENAME, new ClientStorageStructureCreate<SceneScriptStructure>()
            {
                @Override
                public SceneScriptStructure create()
                {
                    return new SceneScriptStructure();
                }
            }).select();

        } catch (final Exception e)
        {
            return;
        }

        for (final SceneScriptStructure scene : db2)
        {
            // System.out.println(String.format("%s = %s", scene.getEntry(), scene.getName()));

            final String name = scene.getEntry() + "-" + scene.getName().replace(" ", "_").replace("\"", "").replace("'", "").replace(":", "") + ".lua";

            // System.out.println("Writing " + name);

            final File file = new File(targetdir + "/" + name);

            try
            {
                file.createNewFile();

                //if (!file.exists())
                    //throw new Exception();

                final FileWriter fileWriter = new FileWriter(file);
                fileWriter.write(scene.getScript());
                fileWriter.close();

            } catch (final Exception e)
            {
                System.out.println("Cant' create " + name);
            }
        }
    }

    @Override
    public String getUsage()
    {
        return getUsage();
    }
}

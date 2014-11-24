package wide.scripts.test;

import wide.core.framework.extensions.scripts.Script;
import wide.core.framework.storage.client.DB2Storage;

public class Test extends Script
{
    public Test()
    {
        super("test");
    }

    @Override
    public void run(String[] args)
    {
        testmy(args[0]);
    }

    void testmy(String path)
    {
        try
        {
            final DB2Storage<MapStructure> dbc = new DB2Storage<MapStructure>(path)
            {
                @Override
                public MapStructure create()
                {
                    return new MapStructure_335();
                }
            };

            //for (final MapStructure map : dbc)
            // System.out.println(String.format("%s %s", map.getMapId(), map.getName()));

            System.out.println(dbc.toString());

        } catch (final Exception e)
        {
            e.printStackTrace();
        }
    }
}

package wide.scripts.test;

import wide.core.framework.extensions.scripts.Script;
import wide.core.framework.storage.DBCStorage;
import wide.core.framework.storage.DBCStructure;

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
            final DBCStorage<MapStructure> dbc = new DBCStorage<MapStructure>(path)
            {
                @Override
                public DBCStructure create()
                {
                    return new MapStructure_335();
                }
            };

            for (final MapStructure map : dbc)
                System.out.println(String.format("%s %s", map.getMapId(), map.getName()));

            // System.out.println(dbc.toString());
        } catch (final Exception e)
        {
            e.printStackTrace();
        }
    }
}

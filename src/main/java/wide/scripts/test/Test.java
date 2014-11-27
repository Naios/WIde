package wide.scripts.test;

import java.util.Arrays;

import wide.core.framework.extensions.scripts.Script;
import wide.core.framework.storage.client.ClientStorage;
import wide.core.framework.storage.client.ClientStorageSelector;
import wide.scripts.ScriptDefinition;

/**
 * Simple testing script, use this as playground.
 * Don't commit its content in the master branch!
 */
public class Test extends Script
{
    public Test(final ScriptDefinition definition)
    {
        super(definition);
    }

    @Override
    public void run(final String[] args)
    {
        System.out.println(String.format("Running %s script with args %s.",
                toString(), Arrays.toString(args)));

        // Playground begin (only commit it in sub-branches to test stuff!)

        final Object[] list = {new String("test"), new Integer(1234), new Float(3.141f), new Boolean(true)};

        for (final Object o : list)
            System.out.println(o);

        System.out.println(Arrays.deepToString(list));

        class TestClass
        {
            public int itest = 5;
        }

        final TestClass t = new TestClass();

        try
        {
            t.getClass().getField("itest").set(t, new Integer(21));
        } catch (final IllegalArgumentException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final IllegalAccessException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final NoSuchFieldException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final SecurityException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println(t.itest);

        System.out.println(int.class.equals(Integer.class));

        final ClientStorage<TaxiNodesStructure> taxiNodes =
                new ClientStorageSelector<TaxiNodesStructure>(TaxiNodesStructure.class, "data/dbc/TaxiNodes.db2").select();

        System.out.println(taxiNodes.toString());
    }

    @Override
    public String getUsage()
    {
        return "";
    }
}

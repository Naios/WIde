package wide.scripts.test;

import java.util.Arrays;

import wide.core.framework.extensions.scripts.Script;
import wide.scripts.ScriptDefinition;

/**
 * Simple testing script, use this as playground.
 * Don't commit its content in the master branch!
 */
public class Test extends Script
{
    public Test(ScriptDefinition definition)
    {
        super(definition);
    }

    @Override
    public void run(String[] args)
    {
        System.out.println(String.format("Running %s script with args %s.",
                toString(), Arrays.toString(args)));

        // Playground begin (only commit it in sub-branches to test stuff!)
    }

    @Override
    public String getUsage()
    {
        return "";
    }
}

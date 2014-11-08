package wide.core.framework.extensions;

public abstract class Module
{
    public Module(String uuid)
    {
        
    }
    

    public void read()
    {
    }

    public boolean checkDependencys()
    {
        return true;
    }

    public boolean check()
    {
        return true;
    }

    public abstract void enable();

    public abstract void disable();
}

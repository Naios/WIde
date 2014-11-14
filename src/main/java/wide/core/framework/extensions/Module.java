package wide.core.framework.extensions;

public abstract class Module
{
    private final String uuid; 

    public Module(String uuid)
    {
        this.uuid = uuid;
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
    
    @Override
    public String toString()
    {
        return uuid;
    }
}

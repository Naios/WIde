package wide.core.session.hooks;

public abstract class HookListener
{
    private final Hook hook;
    
    private final Object owner;
    
    public HookListener(Hook hook, Object owner)
    {
        this.hook = hook;
        this.owner = owner;
    }

    public Hook getType()
    {
        return hook;
    }
    
    public Object getOwner()
    {
        return owner;
    }

    public abstract void informed();
}

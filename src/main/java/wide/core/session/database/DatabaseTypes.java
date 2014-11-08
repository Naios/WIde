package wide.core.session.database;

public enum DatabaseTypes
{
    DATABASE_AUTH("DB:Auth"),
    DATABASE_CHARACTER("DB:Character"),
    DATABASE_WORLD("DB:World");
    
    private final String storageName;
    
    DatabaseTypes(String storageName)
    {
        this.storageName = storageName;
    }
    
    public String getStorageName()
    {
        return storageName;
    }
}

package wide.session.hooks;

public enum Hook
{
    // Hook Name                // Emitter
    // -----------------------------------
    ON_APPLICATION_LAUNCH,      // WIde 
    ON_APPLICATION_STOP,        // WIde

    ON_ARGS_FINISHED,           // Arguments

    ON_CONFIG_LOADED,           // Config
    ON_CONFIG_CHANGED,          // Config

    ON_DATABASE_ESTABLISHED,    // Database
    ON_DATABASE_ERROR,          // Database
    ON_DATABASE_LOST,           // Database
    ON_DATABASE_CLOSE,          // Database

    ON_UNKNOWN
}

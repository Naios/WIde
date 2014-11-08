package wide.core.session.hooks;

public enum Hook
{
    // Hook Name                // Emitter
    // -----------------------------------
    ON_APPLICATION_LAUNCH,      // WIde 
    ON_APPLICATION_STOP,        // WIde

    ON_ARGUMENTS_LOADED,        // Arguments

    ON_CONFIG_LOADED,           // Config
    ON_CONFIG_CHANGED,          // Config

    ON_MODULES_LOADED,          // ModuleLoader
    ON_MODULES_RELOADED,        // ModuleLoader
    ON_MODULES_UNLOADED,        // ModuleLoader

    ON_DATABASE_ESTABLISHED,    // Database
    ON_DATABASE_ERROR,          // Database
    ON_DATABASE_LOST,           // Database
    ON_DATABASE_CLOSE,          // Database

    ON_UNKNOWN
}

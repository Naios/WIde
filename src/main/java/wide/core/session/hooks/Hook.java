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

    ON_MODULES_LOADED,          // Module Loader
    ON_MODULES_RELOADED,        // Module Loader
    ON_MODULES_UNLOADED,        // Module Loader
    
    ON_SCRIPTS_LOADED,          // Script Loader
    ON_SCRIPTS_UNLOADED,        // Script Loader

    ON_DATABASE_ESTABLISHED,    // Database
    ON_DATABASE_ERROR,          // Database
    ON_DATABASE_LOST,           // Database
    ON_DATABASE_CLOSE,          // Database

    ON_UNKNOWN
}

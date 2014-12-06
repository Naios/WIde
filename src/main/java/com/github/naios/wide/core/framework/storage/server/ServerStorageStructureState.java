package com.github.naios.wide.core.framework.storage.server;

public enum ServerStorageStructureState
{
    /**
     * Is pushed on the history stack so we know the time when the value is in sync with the database
     */
    STATE_IN_SYNC,

    /**
     * Is only used in the Structure to mark that it has changed.
     */
    STATE_UPDATED,

    /**
     * Is pushed on the history stack so we know if the value was created
     */
    STATE_CREATED,

    /**
     * Is pushed on the history stack so we know if the value was deleted
     */
    STATE_DELETED;
}

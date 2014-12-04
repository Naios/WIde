package com.github.naios.wide.core.framework.storage.namestorage;

import com.github.naios.wide.core.framework.storage.client.ClientStorage;
import com.github.naios.wide.core.framework.storage.client.ClientStorageException;
import com.github.naios.wide.core.framework.storage.client.ClientStorageSelector;
import com.github.naios.wide.core.framework.storage.client.ClientStorageStructure;
import com.github.naios.wide.core.framework.storage.client.UnknownClientStorageStructure;

public class ClientNameStorage extends NameStorage
{
    private final int entryColumn, nameColumn;

    private final String name;

    public ClientNameStorage(final String name, final int entryColumn, final int nameColumn)
    {
        this.name = ClientStorageStructure.getPathOfFile(name);
        this.entryColumn = entryColumn;
        this.nameColumn = nameColumn;

        load();
    }

    @Override
    public void load()
    {
        try
        {
            final ClientStorage<UnknownClientStorageStructure> dbc =
                    new ClientStorageSelector<UnknownClientStorageStructure>(UnknownClientStorageStructure.class, name).select();

            dbc.fillNameStorage(storage, entryColumn, nameColumn);
        }
        catch (final ClientStorageException e)
        {
            e.printStackTrace();
        }
    }
}

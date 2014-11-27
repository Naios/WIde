package com.github.naios.wide.core.framework.storage.server;

import java.lang.annotation.Annotation;

import com.github.naios.wide.core.framework.game.GameBuildMask;
import com.github.naios.wide.core.framework.storage.GameBuildDependentStorageStructure;

public abstract class ServerStorageStructure extends GameBuildDependentStorageStructure
{
    private ServerStorage<?> owner = null;

    public ServerStorageStructure()
    {
        this(GameBuildDependentStorageStructure.ALL_BUILDS);
    }

    public ServerStorageStructure(final GameBuildMask gamebuilds)
    {
        super(gamebuilds);
    }

    @Override
    protected Class<? extends Annotation> getSpecificAnnotation()
    {
        return ServerStorageEntry.class;
    }

    protected void setOwner(final ServerStorage<?> owner)
    {
        this.owner = owner;
    }
}

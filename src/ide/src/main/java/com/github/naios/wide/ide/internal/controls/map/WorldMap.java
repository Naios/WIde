
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.ide.internal.controls.map;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.Pane;

import com.github.naios.wide.api.ide.Position;

public class WorldMap extends Pane
{
    private final ObjectProperty<Position> viewport =
            new SimpleObjectProperty<>();

    private final ObservableList<WorldMapNode> selectedNodes =
            FXCollections.observableArrayList();

    public WorldMap()
    {
        setStyle("-fx-background-color: white");

        setMinSize(400, 300);
    }

    public ObjectProperty<Position> viewportProperty()
    {
        return viewport;
    }

    public ObservableList<WorldMapNode> selectedNodesProperty()
    {
        return selectedNodes;
    }
}

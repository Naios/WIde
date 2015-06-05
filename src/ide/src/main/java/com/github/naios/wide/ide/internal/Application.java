
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.ide.internal;

import java.util.Optional;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import com.github.naios.wide.api.framework.FrameworkWorkspace;
import com.github.naios.wide.api.framework.storage.server.ServerStorage;
import com.github.naios.wide.entities.server.ServerStorageKeys;
import com.github.naios.wide.entities.server.world.CreatureTemplate;
import com.github.naios.wide.ide.internal.controls.MappingPropertySheet;
import com.github.naios.wide.ide.internal.controls.map.WorldMap;

public class Application extends javafx.application.Application
{
    @Override
    public void start(final Stage primaryStage)
    {
        primaryStage.setTitle("WIde GUI - by Naios");

        final BorderPane root = new BorderPane();

        final Pane propertyRoot = new VBox();
        final WorldMap map = new WorldMap();


        root.setLeft(map);
        root.setRight(propertyRoot);


        primaryStage.setScene(new Scene(root, 300, 250));

        primaryStage.setMinHeight(600);
        primaryStage.setMinWidth(700);

        final Label label = new Label();
        label.setAlignment(Pos.CENTER);

        propertyRoot.setMaxWidth(300);

        propertyRoot.getChildren().add(label);

        final FrameworkWorkspace workspace = Services.getFrameworkService().createWorkspaceFromEnvironment(
                Services.getConfigService().getActiveEnvironment());

        final ServerStorage<CreatureTemplate> creatureTemplate =
                workspace.requestServerStorage("world", "creature_template");

        final MappingPropertySheet propertySheet = new MappingPropertySheet(creatureTemplate.getMappingPlan());

        final Optional<CreatureTemplate> ct = creatureTemplate.request(ServerStorageKeys.ofCreatureTemplate(41378));
        ct.ifPresent(s -> propertySheet.getStructures().add(s));

        propertySheet.getStructures().add(creatureTemplate.request(ServerStorageKeys.ofCreatureTemplate(40)).get());
        propertySheet.getStructures().add(creatureTemplate.request(ServerStorageKeys.ofCreatureTemplate(60)).get());

        propertyRoot.getChildren().add(propertySheet);

        final TextArea textField = new TextArea();
        textField.setEditable(false);
        textField.setMinHeight(100.d);

        final Button asSQLButton = new Button("Print SQL");
        asSQLButton.setOnAction(e ->
        {
            textField.setText(workspace.createSQLBuilder(creatureTemplate.getChangeTracker()).toString());
        });

        propertyRoot.getChildren().addAll(asSQLButton, textField);

        primaryStage.toFront();
        primaryStage.show();

        // propertySheet.getStructures().clear();
    }

    @Override
    public void stop() throws Exception
    {
        Controller.shutdownFramework();
    }
}


/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.ide.internal;

import java.util.Optional;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import com.github.naios.wide.api.framework.FrameworkWorkspace;
import com.github.naios.wide.api.framework.storage.server.ServerStorage;
import com.github.naios.wide.entities.server.ServerStorageKeys;
import com.github.naios.wide.entities.server.world.CreatureTemplate;

public class WIdeGUIApplication extends Application
{
    @Override
    public void start(final Stage primaryStage)
    {
        primaryStage.setTitle("WIde GUI - by Naios");

        final Pane root = new VBox();
        primaryStage.setScene(new Scene(root, 300, 250));

        primaryStage.setMinHeight(700);
        primaryStage.setMinWidth(400);

        final Label label = new Label();
        label.setAlignment(Pos.CENTER);

        root.getChildren().add(label);

        final FrameworkWorkspace workspace = Services.getFrameworkService().createWorkspaceFromEnvironment(
                Services.getConfigService().getActiveEnvironment());

        final ServerStorage<CreatureTemplate> creatureTemplate =
                workspace.requestServerStorage("world", "creature_template");

        final MappingPropertySheet propertySheet = new MappingPropertySheet(creatureTemplate.getMappingPlan());

        final Optional<CreatureTemplate> ct = creatureTemplate.request(ServerStorageKeys.ofCreatureTemplate(41378));
        ct.ifPresent(s -> propertySheet.getStructures().add(s));

        propertySheet.getStructures().add(creatureTemplate.request(ServerStorageKeys.ofCreatureTemplate(40)).get());
        propertySheet.getStructures().add(creatureTemplate.request(ServerStorageKeys.ofCreatureTemplate(60)).get());

        root.getChildren().add(propertySheet);

        final TextArea textField = new TextArea();
        textField.setEditable(false);
        textField.setMinHeight(100.d);

        final Button asSQLButton = new Button("Print SQL");
        asSQLButton.setOnAction(e ->
        {
            textField.setText(workspace.createSQLBuilder(creatureTemplate.getChangeTracker()).toString());
        });

        root.getChildren().addAll(asSQLButton, textField);

        primaryStage.toFront();
        primaryStage.show();

        // propertySheet.getStructures().clear();
    }

    @Override
    public void stop() throws Exception
    {
        WIdeGUIController.shutdownFramework();
    }
}

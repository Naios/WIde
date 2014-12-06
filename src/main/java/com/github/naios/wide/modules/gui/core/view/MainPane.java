package com.github.naios.wide.modules.gui.core.view;


import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import com.github.naios.wide.modules.gui.core.resources.Resource;
import com.github.naios.wide.modules.gui.core.view.components.Dimmer;
import com.github.naios.wide.modules.gui.core.view.components.WorldMap;

public class MainPane extends StackPane
{
    private final Dimmer dimmer = new Dimmer();

    private final StackPane content = new StackPane(),
            widgets = new StackPane();

    private final Node loginForm = new Pane(); // FXMLFormFactory.create(LoginForm.class
            // .getResource("LoginForm.fxml"));

    private boolean isDialogCloseable;

    private final WorldMap map = new WorldMap();

    public MainPane()
    {
        setId("MainForm");
        setPrefSize(900, 600);
        getStylesheets().add(Resource.get("style/WIde.css"));

        // Dimmer
        dimmer.setOnMouseClicked(event ->
        {
            if (isDialogCloseable)
                hideDialog();

            event.consume();
        });

        final VBox controls = new VBox();
        // controls.setPadding(new Insets(10, 10, 10, 10));

        final AnchorPane taskbar = new AnchorPane();

        controls.getChildren().addAll(map, taskbar);

        {
            final Button button = new Button("Settings");
            button.setPrefSize(70, 20);

            button.setOnAction(e ->
            {
                /*
                 * Name c = null; while (c == null) c =
                 * Cache.CreatureNameCache.get(new Random().nextInt(50000));
                 *
                 *
                 * System.out.println(c.getName());
                 */

                // setDialog(new Button("Test"), false, true);

                /*
                try
                {
                    final Mapping mi = new Mapping(Database.WorldDatabase
                            .query("SELECT * FROM creature_addon limit 5"));

                    System.out.print(mi);
                    try
                    {
                        wait();
                    } catch (final InterruptedException e1)
                    {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }

                    //for (Mapping m : mi)
                    {
                        mi.column(3).set("333");
                        mi.column(4).set("333");
                    }

                    // System.out.println(new SQLBuilder(mi).buildUpdateQuery());

                } catch (final SQLException e1)
                {
                }
                */
            });

            taskbar.getChildren().add(button);
            AnchorPane.setBottomAnchor(button, 10.d);
            AnchorPane.setRightAnchor(button, 10.d);

        }

        {
            final Button button = new Button("History");
            button.setPrefSize(70, 20);

            taskbar.getChildren().add(button);
            AnchorPane.setBottomAnchor(button, 10.d);
            AnchorPane.setRightAnchor(button, 90.d);
        }

        {
            final Button button = new Button("Export");
            button.setPrefSize(70, 20);

            taskbar.getChildren().add(button);
            AnchorPane.setBottomAnchor(button, 10.d);
            AnchorPane.setRightAnchor(button, 170.d);
        }

        map.setPrefSize(9999999, 9999999);

        content.getChildren().add(controls);

        getChildren().addAll(content, dimmer);

    }

    public void showLoginScreen()
    {
        setDialog(loginForm, false, false);
    }

    class NodeWrapper extends StackPane
    {
        class LimitedDoubleBinding extends DoubleBinding
        {
            final private double limit;
            final private ReadOnlyDoubleProperty property;

            public LimitedDoubleBinding(final ReadOnlyDoubleProperty property,
                    final double limit)
            {
                this.limit = limit;
                this.property = property;
                bind(property);
            }

            @Override
            protected double computeValue()
            {
                return Math.max(property.get() - limit, limit);
            }
        }

        public NodeWrapper(final Region container, final Node node, final boolean maximized)
        {
            setOnMouseClicked(event -> event.consume());

            getStyleClass().add("dialog");
            getChildren().add(node);

            if (maximized)
            {
                maxHeightProperty().bind(
                        new LimitedDoubleBinding(container.heightProperty(),
                                50.d));
                maxWidthProperty().bind(
                        new LimitedDoubleBinding(container.widthProperty(),
                                50.d));
            } else
            {
                maxWidthProperty().bind(node.layoutXProperty());
                maxHeightProperty().bind(node.layoutYProperty());
            }
        }
    }

    public void setDialog(final Node node, final boolean maximized, final boolean closeable)
    {
        this.isDialogCloseable = closeable;

        dimmer.getChildren().add(new NodeWrapper(this, node, maximized));
        dimmer.setDim(true, content);
    }

    public void hideDialog()
    {
        dimmer.setDim(false, content);
    }
}

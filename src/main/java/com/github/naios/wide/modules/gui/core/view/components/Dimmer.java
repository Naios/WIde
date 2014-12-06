package com.github.naios.wide.modules.gui.core.view.components;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class Dimmer extends StackPane
{
    public Dimmer()
    {

        setId("Dimmer");
        setVisible(false);
    }

    public void setDim(final boolean on, final Node content)
    {
        if (on)
            setOn(content);
        else
            setOff(content);
    }

    private void setOn(final Node content)
    {
        setOpacity(0);
        setVisible(true);
        setCache(true);
        content.setCache(true);

        new Timeline(
                new KeyFrame(Duration.seconds(0.7d),
                        t ->
                        {
                            setCache(false);
                            content.setCache(false);
                        }, new KeyValue(opacityProperty(), 1f,
                                Interpolator.EASE_BOTH)), new KeyFrame(
                        Duration.seconds(0.7d), new KeyValue(
                                content.opacityProperty(), 0f,
                                Interpolator.EASE_BOTH))).play();
    }

    private void setOff(final Node content)
    {
        setCache(true);
        content.setCache(true);
        new Timeline(new KeyFrame(Duration.seconds(0.7d),
                t ->
                {
                    setCache(false);
                    content.setCache(false);
                    setVisible(false);
                    getChildren().clear();
                }, new KeyValue(opacityProperty(), 0, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.seconds(0.7d), new KeyValue(content
                        .opacityProperty(), 1f, Interpolator.EASE_BOTH)))
                .play();
    }
}

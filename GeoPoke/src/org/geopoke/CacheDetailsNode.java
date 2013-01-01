/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geopoke;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URISyntaxException;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import name.antonsmirnov.javafx.dialog.Dialog;

/**
 *
 * @author Michael
 */
public class CacheDetailsNode extends BorderPane {
    
    private Geocache cache;
    
    public CacheDetailsNode(final Geocache cache, final CacheList mainList) {
        this.cache = cache;
        VBox centre = new VBox();
        Text name = new Text(cache.getName() + " (" + cache.getGcNum() + ")");
        name.setStyle("-fx-font-weight: bold;");
        if(cache.isDisabledWarning()) {
            name.setFill(Color.RED);
            Tooltip.install(this, new Tooltip("This cache (" + cache.getGcNum() + ") is currently unavailable."));
        }
        else if(cache.isLogWarning()) {
            name.setFill(Color.ORANGE);
            Tooltip.install(this, new Tooltip("The last few logs for this cache have been DNF's."));
        }
        Text coords = new Text(cache.getBestCoords());
        if(!cache.hasAccurateCoords()) {
            coords.setFill(Color.RED);
            Tooltip.install(coords, new Tooltip("These co-ordinates are probably not accurate.\nThey may represent a starting point, but you will need to solve the puzzle behind the cache first!"));
        }
        Label hint = new Label(cache.getHint());
        hint.setWrapText(true);
        centre.getChildren().addAll(name, coords, hint);
        setCenter(centre);
        
        HBox leftBox = new HBox();
        leftBox.setSpacing(5);
        VBox buttonBox = new VBox();
        buttonBox.setPadding(Insets.EMPTY);
        Button urlButton = new Button("",new ImageView(new Image("file:img/url.png")));
        urlButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent t) {
                try {
                    Desktop.getDesktop().browse(cache.getURL().toURI());
                } catch (URISyntaxException | IOException ex) {
                    Dialog.showError("Error browsing to URL", "If you want to browse manually, go to " + cache.getURL());
                }
            }
        });
        buttonBox.getChildren().add(urlButton);
        Button removeButton = new Button("",new ImageView(new Image("file:img/remove.png")));
        removeButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent t) {
                mainList.removeCache(CacheDetailsNode.this);
            }
        });
        buttonBox.getChildren().add(removeButton);
        leftBox.getChildren().add(buttonBox);
        leftBox.getChildren().add(new ImageView(cache.getType().getIconImage()));
        setLeft(leftBox);
    }

    public Geocache getCache() {
        return cache;
    }
    
}

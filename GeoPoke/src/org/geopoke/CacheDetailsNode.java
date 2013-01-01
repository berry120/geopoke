/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geopoke;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
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
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Michael
 */
public class CacheDetailsNode extends BorderPane {

    private Geocache cache;
    private Text label;
    private List<LabelListener> listeners;

    public CacheDetailsNode(final Geocache cache, final CacheList mainList) {
        this.cache = cache;
        listeners = new ArrayList<>();
        VBox centre = new VBox();
        label = new Text();
        label.setStyle("-fx-font-weight: bold;");
        Label name = new Label(cache.getName() + " (" + cache.getGcNum() + ")");
        name.setWrapText(true);
        name.setStyle("-fx-font-weight: bold;");
        if (cache.isDisabledWarning()) {
            name.setStyle(name.getStyle() + " -fx-text-fill: red;");
            Tooltip.install(this, new Tooltip("This cache (" + cache.getGcNum() + ") is currently unavailable."));
        } else if (cache.isLogWarning()) {
            name.setStyle(name.getStyle() + " -fx-text-fill: orange;");
            Tooltip.install(this, new Tooltip("The last few logs for this cache have been DNF's."));
        }
        Text coords = new Text(cache.getBestCoords());
        if (!cache.hasAccurateCoords()) {
            coords.setFill(Color.RED);
            Tooltip.install(coords, new Tooltip("These co-ordinates are probably not accurate.\nThey may represent a starting point, but you will need to solve the puzzle behind the cache first!"));
        }
        Label hint = new Label(cache.getHint());
        hint.setWrapText(true);
        HBox topBox = new HBox();
        topBox.setSpacing(10);
        topBox.getChildren().addAll(label, name);
        centre.getChildren().addAll(topBox, coords, hint);
        setCenter(centre);

        HBox leftBox = new HBox();
        leftBox.setSpacing(5);
        VBox buttonBox = new VBox();
        buttonBox.setPadding(Insets.EMPTY);
        Button urlButton = new Button("", new ImageView(new Image("file:img/url.png")));
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
        Button removeButton = new Button("", new ImageView(new Image("file:img/remove.png")));
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

    public void addLabelListener(LabelListener listener) {
        listeners.add(listener);
    }

    public void removeLabelListener(LabelListener listener) {
        listeners.remove(listener);
    }

    public void setLabel(String labelText) {
        if (!label.getText().equals(labelText)) {
            String oldLabel = label.getText();
            this.label.setText(labelText);
            for (LabelListener listener : listeners) {
                listener.updated(oldLabel, labelText);
            }
        }
    }

    public String getLabel() {
        return label.getText();
    }

    public Geocache getCache() {
        return cache;
    }

    public void addToXML(Document doc, Element root) {
        Element cacheElem = doc.createElement("cache");
        root.appendChild(cacheElem);
        addTag("GC", cache.getGcNum(), doc, cacheElem);
        addTag("label", label.getText(), doc, cacheElem);
        addTag("name", cache.getName(), doc, cacheElem);
        addTag("coords", cache.getBestCoords(), doc, cacheElem);
        addTag("hint", cache.getHint(), doc, cacheElem);
        addTag("description", cache.getDescription(), doc, cacheElem);
        addTag("difficulty", Integer.toString(cache.getDifficulty()), doc, cacheElem);
        addTag("terrain", Integer.toString(cache.getTerrain()), doc, cacheElem);
        addTag("type", cache.getType().toString(), doc, cacheElem);
        addTag("warning", getWarningString(), doc, cacheElem);
    }

    private String getWarningString() {
        if (cache.isDisabledWarning()) {
            return "This cache is disabled - it's probably not there!";
        } else if (!cache.hasAccurateCoords()) {
            return "This cache hasn't had more specific co-ordinates entered - the ones above are probably just a placeholder.";
        } else if (cache.isLogWarning()) {
            return "The last few logs for this cache have been DNFs.";
        } else {
            return "";
        }
    }

    private void addTag(String tagName, String content, Document doc, Element e) {
        Element child = doc.createElement(tagName);
        child.appendChild(doc.createTextNode(content));
        e.appendChild(child);
    }
}

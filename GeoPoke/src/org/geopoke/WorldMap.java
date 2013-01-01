/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geopoke;

import java.io.File;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.Set;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebView;

/**
 *
 * @author Michael
 */
public class WorldMap extends BorderPane {

    private WebView webview;
    private boolean ready;
    private Set<CacheDetailsNode> markers;

    public WorldMap() {
        markers = new HashSet<>();
        ready = false;
        webview = new WebView();
        try {
            webview.getEngine().load(new File("googlemap.html").toURI().toURL().toString());
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }
        setCenter(webview);
        webview.getEngine().getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {
            @Override
            public void changed(ObservableValue ov, Worker.State t, Worker.State t1) {
                if (t1.equals(Worker.State.SUCCEEDED)) {
                    ready = true;
                }
            }
        });
    }

    public boolean isReady() {
        return ready;
    }

    public void addMarker(final CacheDetailsNode node) {
        markers.add(node);
        updateMarker(node);
        node.addLabelListener(new LabelListener() {

            @Override
            public void updated(String oldLabel, String newLabel) {
                webview.getEngine().executeScript("document.removeMarker(\"" + oldLabel + "\")");
                updateMarker(node);
            }
        });
    }
    
    private void updateMarker(CacheDetailsNode node) {
        String coords = node.getCache().getBestCoords();
        File labelImage = new LabelImageGenerator().generateLabelImage(node.getLabel());
        String northDegrees = coords.substring(0, coords.indexOf('°'));
        boolean north = northDegrees.charAt(0) == 'N';
        northDegrees = northDegrees.substring(1).trim();
        int northDegreeNum = Integer.parseInt(northDegrees);
        String northMinutes = coords.substring(coords.indexOf('°') + 1, coords.indexOf('\'')).trim();
        double northMinuteNum = Double.parseDouble(northMinutes);

        String latterCoords = coords.substring(coords.indexOf('\'') + 1).trim();
        String eastDegrees = latterCoords.substring(0, latterCoords.indexOf('°'));
        boolean east = eastDegrees.charAt(0) == 'E';
        eastDegrees = eastDegrees.substring(1).trim();
        int eastDegreeNum = Integer.parseInt(eastDegrees);
        String eastMinutes = latterCoords.substring(latterCoords.indexOf('°') + 1, latterCoords.indexOf('\'')).trim();
        double eastMinuteNum = Double.parseDouble(eastMinutes);

        double lat = northDegreeNum + (northMinuteNum / 60);
        if (!north) {
            lat *= -1;
        }
        double lon = eastDegreeNum + (eastMinuteNum / 60);
        if (!east) {
            lon *= -1;
        }
        webview.getEngine().executeScript("document.addMarker(" + lat + "," + lon + ",\"" + labelImage.toURI().toString() + "\",\"" + node.getLabel() + "\")");
//        webview.getEngine().executeScript("document.goToLocation(\"" + coords + "\")");
        webview.getEngine().executeScript("document.fitAllMarkers()");
    }

    public void removeMarker(CacheDetailsNode node) {
        webview.getEngine().executeScript("document.removeMarker(\"" + node.getLabel() + "\")");
        markers.remove(node);
    }
}

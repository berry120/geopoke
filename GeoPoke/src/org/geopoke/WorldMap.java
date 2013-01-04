/* 
 * This file is part of Geopoke.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.geopoke;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.Set;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.geometry.Bounds;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebView;
import javafx.stage.Window;

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

    /**
     * Get a snapshot of the current map as an image.
     * @return the current snapshot of the map.
     */
    public BufferedImage getSnapshot() {
        Window window = getScene().getWindow();
        Bounds b = localToScene(getBoundsInLocal());
        int x = (int) Math.round(window.getX() + getScene().getX() + b.getMinX());
        int y = (int) Math.round(window.getY() + getScene().getY() + b.getMinY());
        int w = (int) Math.round(b.getWidth());
        int h = (int) Math.round(b.getHeight());
        try {
            Robot robot = new Robot();
            BufferedImage image = robot.createScreenCapture(new java.awt.Rectangle(x, y, w, h));
            return image;
        } catch (AWTException ex) {
            return null;
        }
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
    
    public void removeAllMarkers() {
        webview.getEngine().executeScript("document.removeAllMarkers()");
        markers.clear();
    }
}

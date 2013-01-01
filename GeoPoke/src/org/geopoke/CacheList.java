/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geopoke;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import name.antonsmirnov.javafx.dialog.Dialog;

/**
 *
 * @author Michael
 */
public class CacheList extends VBox {

    private List<CacheDetailsNode> cacheDetailsNodes = new ArrayList<>();
    private WorldMap map;

    public CacheList(WorldMap map) {
        setSpacing(10);
        this.map = map;
    }

    public boolean alreadyAdded(Geocache newCache) {
        for (CacheDetailsNode details : cacheDetailsNodes) {
            if (details.getCache().equals(newCache)) {
                return true;
            }
        }
        return false;
    }

    public void addCache(Geocache cache) {
        if (alreadyAdded(cache)) {
            Dialog.showWarning("Already there!", "You've already added this cache to the list.");
        } else {
            CacheDetailsNode node = new CacheDetailsNode(cache, this);
            cacheDetailsNodes.add(node);
            node.setLabel(cacheDetailsNodes.size() + ".");
            map.addMarker(node);
            getChildren().add(node);
        }
    }

    public List<Geocache> getCaches() {
        List<Geocache> ret = new ArrayList<>();
        for (Node n : getChildren()) {
            if (n instanceof CacheDetailsNode) {
                ret.add(((CacheDetailsNode) n).getCache());
            }
        }
        return ret;
    }

    public List<CacheDetailsNode> getCacheNodes() {
        return new ArrayList<>(cacheDetailsNodes);
    }

    public void removeCache(CacheDetailsNode node) {
        int pos = cacheDetailsNodes.indexOf(node);
        cacheDetailsNodes.remove(node);
        map.removeMarker(node);
        getChildren().remove(node);
        for (int i = pos; i < cacheDetailsNodes.size(); i++) {
            cacheDetailsNodes.get(i).setLabel((i + 1) + ".");
        }
    }
}

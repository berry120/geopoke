/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geopoke;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import name.antonsmirnov.javafx.dialog.Dialog;

/**
 *
 * @author Michael
 */
public class CacheList extends VBox {

    private Set<String> cacheNums = new HashSet<>();
    private WorldMap map;
    
    public CacheList(WorldMap map) {
        setSpacing(10);
        this.map = map;
    }

    public void addCache(Geocache cache) {
        map.addMarker(cache.getBestCoords(), "T1");
        if (cacheNums.contains(cache.getGcNum())) {
            Dialog.showWarning("Already there!", "You've already added this cache to the list.");
        } else {
            cacheNums.add(cache.getGcNum());
            CacheDetailsNode node = new CacheDetailsNode(cache, this);
            getChildren().add(node);
        }
    }
    
    public List<Geocache> getCaches() {
        List<Geocache> ret = new ArrayList<>();
        for(Node n : getChildren()) {
            if(n instanceof CacheDetailsNode) {
                ret.add(((CacheDetailsNode)n).getCache());
            }
        }
        return ret;
    }

    public void removeCache(CacheDetailsNode node) {
        cacheNums.remove(node.getCache().getGcNum());
        map.removeMarker(node.getCache().getBestCoords());
        getChildren().remove(node);
    }
}

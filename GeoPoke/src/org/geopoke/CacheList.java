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

import java.util.ArrayList;
import java.util.List;
import javafx.scene.layout.VBox;
import name.antonsmirnov.javafx.dialog.Dialog;

/**
 *
 * @author Michael
 */
public class CacheList extends VBox {

    private List<CacheDetailsNode> cacheDetailsNodes = new ArrayList<>();
    private List<SizeListener> sizeListeners = new ArrayList<>();
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

    public List<Geocache> getCaches() {
        List<Geocache> ret = new ArrayList<>();
        for (CacheDetailsNode node : cacheDetailsNodes) {
            ret.add(node.getCache());
        }
        return ret;
    }
    
    public void addCache(List<Geocache> caches) {
        addCache(null, caches.toArray(new Geocache[caches.size()]));
    }

    public void addCache(Geocache... caches) {
        addCache(null, caches);
    }

    public void addCache(ProgressUpdator updator, Geocache... caches) {
        for (int i = 0; i < caches.length; i++) {
            Geocache cache = caches[i];
            if (alreadyAdded(cache)) {
                if (caches.length == 1) {
                    Dialog.showWarning("Already there!", "You've already added this cache to the list.");
                }
            } else {
                CacheDetailsNode node = new CacheDetailsNode(cache, this);
                cacheDetailsNodes.add(node);
                node.setLabel(cacheDetailsNodes.size() + ".");
                map.addMarker(node);
                getChildren().add(node);
                for (SizeListener listener : sizeListeners) {
                    listener.sizeChanged(cacheDetailsNodes.size() - 1, cacheDetailsNodes.size());
                }
            }
            if (updator != null) {
                updator.update((double) (i + 1) / caches.length);
            }
        }
    }

    public List<CacheDetailsNode> getCacheNodes() {
        return new ArrayList<>(cacheDetailsNodes);
    }

    public void removeCache(CacheDetailsNode node) {
        int pos = cacheDetailsNodes.indexOf(node);
        cacheDetailsNodes.remove(node);
        getChildren().remove(node);
        for (int i = pos; i < cacheDetailsNodes.size(); i++) {
            cacheDetailsNodes.get(i).setLabel((i + 1) + ".");
        }
        for (SizeListener listener : sizeListeners) {
            listener.sizeChanged(cacheDetailsNodes.size() + 1, cacheDetailsNodes.size());
        }
        map.removeMarker(node);
    }

    public void removeAllCaches() {
        map.removeAllMarkers();
        for (CacheDetailsNode node : cacheDetailsNodes) {
            getChildren().remove(node);
        }
        cacheDetailsNodes.clear();
    }

    public void addSizeListener(SizeListener listener) {
        sizeListeners.add(listener);
    }

    public void removeSizeListener(SizeListener listener) {
        sizeListeners.remove(listener);
    }
}

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

import javafx.scene.image.Image;

public enum CacheType {

    TRADITIONAL(true, new Image("file:img/trad.gif")),
    MULTI(false, new Image("file:img/multi.gif")),
    MYSTERY(false, new Image("file:img/mystery.gif")),
    HYBRID(true, new Image("file:img/hybrid.gif")),
    VIRTUAL(true, new Image("file:img/virtual.gif")),
    EVENT(true, new Image("file:img/event.gif")),
    APE(true, new Image("file:img/ape.gif")),
    WEBCAM(true, new Image("file:img/webcam.gif")),
    REVERSE(true, new Image("file:img/locationless.gif")),
    CITO(true, new Image("file:img/cito.gif")),
    EARTH(true, new Image("file:img/earth.gif")),
    MEGAEVENT(true, new Image("file:img/megaevent.gif")),
    WHERIGO(true, new Image("file:img/wherigo.gif")),
    HQ(true, new Image("file:img/hq.gif"));
    
    private Image defaultImg = new Image("file:img/trad.gif");
    private boolean initialAccurateCoords;
    private Image iconImage;

    private CacheType(boolean accurateCoords) {
        this(accurateCoords, null);
    }

    private CacheType(boolean initialAccurateCoords, Image iconImage) {
        this.initialAccurateCoords = initialAccurateCoords;
        if(iconImage==null) {
            iconImage = defaultImg;
        }
        this.iconImage = iconImage;
    }

    public boolean hasInitialAccurateCoords() {
        return initialAccurateCoords;
    }

    public Image getIconImage() {
        return iconImage;
    }
}
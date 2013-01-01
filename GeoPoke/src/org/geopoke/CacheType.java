/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geopoke;

import javafx.scene.image.Image;

public enum CacheType {

    TRADITIONAL(true, new Image("file:img/trad.gif")),
    MULTI(false, new Image("file:img/multi.gif")),
    MYSTERY(false, new Image("file:img/mystery.gif")),
    HYBRID(true),
    VIRTUAL(true),
    EVENT(true),
    APE(true),
    WEBCAM(true),
    REVERSE(true),
    CITO(true),
    EARTH(true),
    MEGAEVENT(true),
    WHERIGO(true),
    HQ(true);
    
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
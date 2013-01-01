/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geopoke;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Michael
 */
public class Geocache {

    private String gcNum;
    private String name;
    private Coords coords;
    private String description;
    private int difficulty;
    private int terrain;
    private String hint;
    private boolean warning;
    private boolean logWarning;
    private CacheType type;

    public Geocache(CacheType type, String gcNum, String name, Coords coords, String description, int difficulty, int terrain, String hint) {
        this.type = type;
        this.gcNum = gcNum;
        this.name = name;
        this.coords = coords;
        this.description = description;
        this.difficulty = difficulty;
        this.terrain = terrain;
        this.hint = hint;
    }

    public CacheType getType() {
        return type;
    }

    public boolean hasAccurateCoords() {
        if (type.hasInitialAccurateCoords()) {
            return true;
        }
        return coords.getNewCoords() != null;
    }

    public String getBestCoords() {
        if(coords==null) {
            return null;
        }
        if (coords.getNewCoords() == null) {
            return coords.getOldCoords();
        }
        return coords.getNewCoords();
    }

    public String getGcNum() {
        return gcNum;
    }

    public String getName() {
        return name;
    }

    public Coords getCoords() {
        return coords;
    }

    public String getDescription() {
        return description;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public int getTerrain() {
        return terrain;
    }

    public String getHint() {
        return hint;
    }

    public boolean isDisabledWarning() {
        return warning;
    }

    public void setDisabledWarning(boolean warning) {
        this.warning = warning;
    }

    public boolean isLogWarning() {
        return logWarning;
    }

    public void setLogWarning(boolean warning) {
        this.logWarning = warning;
    }

    public URL getURL() {
        try {
            return new URL("http://coord.info/" + gcNum);
        } catch (MalformedURLException ex) {
            return null;
        }
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.gcNum);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Geocache other = (Geocache) obj;
        if (!Objects.equals(this.gcNum, other.gcNum)) {
            return false;
        }
        return true;
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geopoke;

import java.net.MalformedURLException;
import java.net.URL;
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

    public void addToXML(Document doc, Element root) {
        Element cacheElem = doc.createElement("cache");
        root.appendChild(cacheElem);
        addTag("GC", getGcNum(), doc, cacheElem);
        addTag("name", getName(), doc, cacheElem);
        addTag("coords", getBestCoords(), doc, cacheElem);
        addTag("hint", getHint(), doc, cacheElem);
        addTag("description", getDescription(), doc, cacheElem);
        addTag("difficulty", Integer.toString(getDifficulty()), doc, cacheElem);
        addTag("terrain", Integer.toString(getTerrain()), doc, cacheElem);
        addTag("type", getType().toString(), doc, cacheElem);
        addTag("warning", getWarningString(), doc, cacheElem);
    }

    private String getWarningString() {
        if (isDisabledWarning()) {
            return "This cache is disabled - it's probably not there!";
        } else if (!hasAccurateCoords()) {
            return "This cache hasn't had more specific co-ordinates entered - the ones above are probably just a placeholder.";
        } else if (isLogWarning()) {
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

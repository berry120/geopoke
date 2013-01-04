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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

/**
 *
 * @author Michael
 */
public class Geocache {

    private String gcNum;
    private String name;
    private CacheCoords coords;
    private String shortDescription;
    private String longDescription;
    private Difficulty difficulty;
    private Difficulty terrain;
    private String hint;
    private boolean warning;
    private boolean logWarning;
    private CacheType type;

    public Geocache(CacheType type, String gcNum, String name, CacheCoords coords, String shortDescription, String longDescription, Difficulty difficulty, Difficulty terrain, String hint) {
        this.type = type;
        this.gcNum = gcNum;
        this.name = name;
        this.coords = coords;
        this.shortDescription = shortDescription;
        this.longDescription = longDescription;
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
        return coords.getCorrectedCoords() != null;
    }

    public String getBestCoords() {
        if(coords==null) {
            return null;
        }
        if (coords.getCorrectedCoords() == null) {
            return coords.getInitialCoords();
        }
        return coords.getCorrectedCoords();
    }

    public String getGcNum() {
        return gcNum;
    }

    public String getName() {
        return name;
    }

    public CacheCoords getCoords() {
        return coords;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public Difficulty getTerrain() {
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

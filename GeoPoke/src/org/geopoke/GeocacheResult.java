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

import java.util.List;

/**
 *
 * @author Michael
 */
public class GeocacheResult {
    
    private List<Geocache> caches;
    private List<String> unsuccessful;
    
    public GeocacheResult(List<Geocache> caches, List<String> unsuccessful) {
        this.caches = caches;
        this.unsuccessful = unsuccessful;
    }

    public List<Geocache> getCaches() {
        return caches;
    }

    public List<String> getUnsuccessful() {
        return unsuccessful;
    }
    
}

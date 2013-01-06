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

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Michael
 */
public class GPSSender {

    private static final File TEMPLATE_FILE = new File("gpstemplate.html");
    private static String TEMPLATE_CONTENTS;

    static {
        try {
            TEMPLATE_CONTENTS = FileUtils.readFileToString(TEMPLATE_FILE);
        }
        catch(IOException ex) {
            ex.printStackTrace();
        }
    }

    public void send(List<CacheDetailsNode> caches) {
        try {
            File temp = File.createTempFile("geopoke_gpstemplate", ".html");
            temp.deleteOnExit();
            String fileContents = TEMPLATE_CONTENTS.replace("<!--PLACEHOLDER-->", generateStrs(caches));
            FileUtils.writeStringToFile(temp, fileContents);
            Desktop.getDesktop().browse(temp.toURI());
        }
        catch(IOException ex) {
            ex.printStackTrace();
        }
    }

    private String generateStrs(List<CacheDetailsNode> caches) {
        StringBuilder ret = new StringBuilder();
        for(int i=0 ; i<caches.size() ; i++) {
            CacheDetailsNode node = caches.get(i);
            ret.append("arr[").append(Integer.toString(i)).append("]=new Garmin.WayPoint(\"");
            ret.append(node.getCache().getBestCoords().getLat());
            ret.append("\", \"");
            ret.append(node.getCache().getBestCoords().getLon());
            ret.append("\", null, \"");
            ret.append("GP-").append(node.getLabel());
            ret.append("\", \"\");\n");
        }
        return ret.toString();
    }
}

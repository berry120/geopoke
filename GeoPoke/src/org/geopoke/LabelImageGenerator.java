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

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/**
 *
 * @author Michael
 */
public class LabelImageGenerator {

    public File generateLabelImage(String label) {
        try {
            BufferedImage img = ImageIO.read(new File("bubble.png").toURI().toURL());
            Graphics2D graphics = img.createGraphics();
            graphics.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.setRenderingHint(
                    RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            Font font = new Font("Arial", Font.BOLD, 20);
            graphics.setFont(font);
            int width = graphics.getFontMetrics().stringWidth(label);
            graphics.setColor(Color.BLACK);
            graphics.drawString(label, 20 - width / 2, 20);
            graphics.dispose();
            File temp = File.createTempFile("icon", ".png");
            temp.deleteOnExit();
            ImageIO.write(img, "png", temp);
            return temp;
        } catch (Exception ex) {
            return null;
        }
    }
}

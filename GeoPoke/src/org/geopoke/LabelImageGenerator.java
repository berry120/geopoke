/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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

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

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Michael
 */
public class ReportGenerator {

    private BufferedImage rotate(BufferedImage image, double angle) {
        angle = Math.toRadians(angle);
        double sin = Math.abs(Math.sin(angle)), cos = Math.abs(Math.cos(angle));
        int w = image.getWidth(), h = image.getHeight();
        int neww = (int) Math.floor(w * cos + h * sin), newh = (int) Math.floor(h * cos + w * sin);
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        GraphicsConfiguration gc = gd.getDefaultConfiguration();
        BufferedImage result = gc.createCompatibleImage(neww, newh, Transparency.TRANSLUCENT);
        Graphics2D g = result.createGraphics();
        g.translate((neww - w) / 2, (newh - h) / 2);
        g.rotate(angle, w / 2, h / 2);
        g.drawRenderedImage(image, null);
        return result;
    }

    public File generateReport(List<CacheDetailsNode> caches, BufferedImage mapImg, File file) {
        try {
            File mapFile = File.createTempFile("geopoke_map", ".png");
            mapFile.deleteOnExit();
            if (mapImg.getWidth() > mapImg.getHeight()) {
                mapImg = rotate(mapImg, 90);
            }
            ImageIO.write(mapImg, "png", mapFile);
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("caches");
            for (CacheDetailsNode cache : caches) {
                cache.addToXML(doc, rootElement, true);
            }
            Element mapElement = doc.createElement("map");
            Element imageElement = doc.createElement("image");
            imageElement.appendChild(doc.createTextNode(mapFile.toURI().toString()));
            mapElement.appendChild(imageElement);
            rootElement.appendChild(mapElement);
            doc.appendChild(rootElement);

            File xsltfile = new File("style.xslt");
            Source source = new DOMSource(doc);
            StreamSource transformSource = new StreamSource(xsltfile);
            FopFactory fopFactory = FopFactory.newInstance();
            FOUserAgent foUserAgent = fopFactory.newFOUserAgent();
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();

            TransformerFactory tranFactory = TransformerFactory.newInstance();
            Transformer xslfoTransformer = tranFactory.newTransformer(transformSource);
            Fop fop = fopFactory.newFop("application/pdf", foUserAgent, outStream);
            Result res = new SAXResult(fop.getDefaultHandler());
            xslfoTransformer.transform(source, res);
            if (file == null) {
                file = File.createTempFile("geopoke", ".pdf");
                file.deleteOnExit();
            }
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(outStream.toByteArray());
            }
            return file;
        } catch (ParserConfigurationException | DOMException | TransformerFactoryConfigurationError | FOPException | TransformerException | IOException ex) {
            return null;
        }
    }
}

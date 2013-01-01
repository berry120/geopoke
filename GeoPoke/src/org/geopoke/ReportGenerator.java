/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geopoke;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamResult;
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

    public File generateReport(List<Geocache> caches, File file) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("caches");
            for (Geocache cache : caches) {
                cache.addToXML(doc, rootElement);
            }
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
//                file.deleteOnExit();
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

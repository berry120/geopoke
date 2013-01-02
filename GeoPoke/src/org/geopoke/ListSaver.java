/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geopoke;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;

/**
 *
 * @author Michael
 */
public class ListSaver {

    private static final Logger LOGGER = Logger.getLogger(ListSaver.class.getName());

    public void getCaches(final GeoSession session, final File file,
            final ProgressUpdator updator, final OnFinishedOpening finished) {
        Thread t = new Thread() {
            public void run() {
                try {
                    final List<Geocache> caches = new ArrayList<>();
                    BufferedReader reader = new BufferedReader(new FileReader(file));
                    String cacheNums = reader.readLine();
                    String[] parts = cacheNums.split(",");
                    for (int i = 0; i < parts.length; i++) {
                        String cacheNum = parts[i];
                        caches.add(session.getCacheFromGC(cacheNum));
                        final double val = ((double) (i + 1)) / parts.length;
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                updator.update(val);
                            }
                        });
                    }
                    Platform.runLater(new Runnable() {

                        @Override
                        public void run() {
                            finished.finished(caches.toArray(new Geocache[caches.size()]));
                        }
                    });
                } catch (IOException ex) {
                    LOGGER.log(Level.WARNING, "Couldn't get caches from file", ex);
                }
            }
        };
        t.start();
    }

    public void saveCaches(List<Geocache> caches, File file) {
        StringBuilder content = new StringBuilder();
        for (int i = 0; i < caches.size(); i++) {
            content.append(caches.get(i).getGcNum());
            if (i < caches.size() - 1) {
                content.append(',');
            }
        }
        try (PrintWriter pw = new PrintWriter(file)) {
            pw.println(content);
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, "Couldn't save caches to file", ex);
            ex.printStackTrace();
        }
    }
}

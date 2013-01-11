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
            final ProgressUpdator updator, final Callback<Geocache[]> finished) {
        Thread t = new Thread() {
            public void run() {
                try {
                    final List<Geocache> caches = new ArrayList<>();
                    BufferedReader reader = new BufferedReader(new FileReader(file));
                    String cacheNums = reader.readLine();
                    String[] parts = cacheNums.split(",");
                    for(int i = 0; i < parts.length; i++) {
                        String cacheNum = parts[i];
                        Geocache cache = null;
                        int retries = 0;
                        while(cache == null && retries < 3) {
                            if(retries != 0) {
                                LOGGER.log(Level.WARNING, "Null cache, sleeping...");
                                try {
                                    Thread.sleep(15000); //15 seconds
                                }
                                catch(InterruptedException ex) {
                                    LOGGER.log(Level.WARNING, "Interrupted :-(", ex);
                                }
                            }
                            cache = session.getCacheFromGC(cacheNum);
                            retries++;
                        }
                        if(cache != null) {
                            caches.add(cache);
                        }
                        else {
                            LOGGER.log(Level.WARNING, "Some caches didn't make it.");
                        }
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
                            finished.call(caches.toArray(new Geocache[caches.size()]));
                        }
                    });
                }
                catch(IOException ex) {
                    LOGGER.log(Level.WARNING, "Couldn't get caches from file", ex);
                }
            }
        };
        t.start();
    }

    public void saveCaches(List<Geocache> caches, File file) {
        StringBuilder content = new StringBuilder();
        for(int i = 0; i < caches.size(); i++) {
            content.append(caches.get(i).getGcNum());
            if(i < caches.size() - 1) {
                content.append(',');
            }
        }
        try(PrintWriter pw = new PrintWriter(file)) {
            pw.println(content);
        }
        catch(IOException ex) {
            LOGGER.log(Level.WARNING, "Couldn't save caches to file", ex);
            ex.printStackTrace();
        }
    }
}

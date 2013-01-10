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

import javafx.application.Platform;
import name.antonsmirnov.javafx.dialog.Dialog;

/**
 *
 * @author Michael
 */
public class SessionFactory {

    private APISession apiSession;
    private boolean apiLoginok;

    public GeoSession newAPISession() {
        final WaitForAuthStage stage = new WaitForAuthStage();
        Thread t = new Thread() {
            public void run() {
                apiSession = new APISession();
                apiLoginok = apiSession.login();
                Platform.runLater(new Runnable() {

                    @Override
                    public void run() {
                        stage.hide();
                    }
                });
            }
        };
        t.start();
        stage.showAndWait();
        if(apiLoginok) {
            if(apiSession.isLimited()) {
                Dialog.showWarning("Bit limited :-/", "You will only be able to access 3 geocaches a day with this application until you\nupgrade to premium membership on geocaching.com - sorry about that.");
            }
            return apiSession;
        }
        else {
            return null;
        }
    }

    public GeoSession newScrapeSession() {
        return new LoginStage().getSession();
    }
}

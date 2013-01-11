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

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author Michael
 */
public class WaitForAuthStage extends Stage {
    
    public WaitForAuthStage() {
        initModality(Modality.APPLICATION_MODAL);
        initStyle(StageStyle.UTILITY);
        setTitle("Waiting for authorisation...");
        getIcons().add(new Image("file:img/logo.png"));
        BorderPane root = new BorderPane();
        Label l = new Label("Welcome to Geopoke! We're just authorising you with your geocaching account, please authorise the application in your browser window. When you're authorised, this dialog will disappear and Geopoke will start up!");
        l.setWrapText(true);
        root.setTop(l);
        setScene(new Scene(root));
        setWidth(400);
        setHeight(100);
    }
    
}

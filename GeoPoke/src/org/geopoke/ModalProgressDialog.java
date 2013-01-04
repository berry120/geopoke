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
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author Michael
 */
public class ModalProgressDialog extends Stage {
    
    private ProgressBar progressBar;
    
    public ModalProgressDialog(String text) {
        initModality(Modality.APPLICATION_MODAL);
        initStyle(StageStyle.UNDECORATED);
        setResizable(false);
        setTitle(text);
        
        BorderPane root = new BorderPane();
        root.setTop(new Label(text));
        progressBar = new ProgressBar();
        root.setCenter(progressBar);
        setScene(new Scene(root));
    }
    
    public void setProgress(double progress) {
        progressBar.setProgress(progress);
        if(progress>=1) {
            hide();
        }
    }
    
}

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

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author Michael
 */
public class OpenProgressDialog extends Stage {
    
    private ProgressBar progressBar;
    
    public OpenProgressDialog() {
        initModality(Modality.APPLICATION_MODAL);
        initStyle(StageStyle.UTILITY);
        setResizable(false);
        setTitle("Opening...");
        
        VBox root = new VBox();
        root.setAlignment(Pos.CENTER);
        progressBar = new ProgressBar();
        root.getChildren().addAll(progressBar);
        setScene(new Scene(root));
        setWidth(150);
    }
    
    public void setProgress(double progress) {
        progressBar.setProgress(progress);
        if(progress>=1) {
            hide();
        }
    }
    
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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

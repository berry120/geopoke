/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geopoke;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author Michael
 */
public class LoginStage extends Stage {
    
    private Button btn;
    private GeoSession session;
    
    public LoginStage() {
        initModality(Modality.APPLICATION_MODAL);
        getIcons().add(new Image("file:img/logo.png"));
        setTitle("Login");
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        
        Text scenetitle = new Text("Hello!");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 0, 0, 2, 1);

        Label userName = new Label("User Name:");
        grid.add(userName, 0, 1);

        final TextField userTextField = new TextField();
        grid.add(userTextField, 1, 1);

        Label pw = new Label("Password:");
        grid.add(pw, 0, 2);

        final PasswordField pwBox = new PasswordField();
        pwBox.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent t) {
                btn.fire();
            }
        });
        grid.add(pwBox, 1, 2);
        
        btn = new Button("Sign in");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 1, 4);
        
        final Text actiontarget = new Text();
        actiontarget.setText("Please sign in with your geocaching.com username and password.");
        actiontarget.setWrappingWidth(150);
        grid.add(actiontarget, 1, 6);
        
        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                actiontarget.setFill(Color.FIREBRICK);
                actiontarget.setText("Signing in...");
                btn.setDisable(true);
                Thread loginThread = new Thread() {
                    public void run() {
                        session = new GeoSession(userTextField.getText(), pwBox.getText());
                        Platform.runLater(new Runnable() {

                            @Override
                            public void run() {
                                btn.setDisable(false);
                                if (session.login()) {
                                    actiontarget.setText("");
                                    hide();
                                } else {
                                    actiontarget.setText("Login failed.");
                                }
                            }
                        });
                    }
                };
                loginThread.start();
            }
        });

        Scene scene = new Scene(grid, 300, 275);
        setScene(scene);
    }
    
    public GeoSession getSession() {
        showAndWait();
        return session;
    }
    
}

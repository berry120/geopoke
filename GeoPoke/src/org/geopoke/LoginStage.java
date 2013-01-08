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
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
    private ScrapeSession session;

    public LoginStage() {
        initModality(Modality.APPLICATION_MODAL);
        getIcons().add(new Image("file:img/logo.png"));
        setTitle("Login");
        setResizable(false);
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
        userTextField.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                btn.fire();
            }
        });
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
        btn.setDisable(true);
        userTextField.textProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> ov, String t, String t1) {
                btn.setDisable(t1.isEmpty());
            }
        });
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
                        session = new ScrapeSession(userTextField.getText(), pwBox.getText());
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                if (pwBox.getText().isEmpty()) {
                                    actiontarget.setText("Please enter a password.");
                                    btn.setDisable(false);
                                    pwBox.requestFocus();
                                } else {
                                    btn.setDisable(false);
                                    if (session.login()) {
                                        actiontarget.setText("");
                                        hide();
                                    } else {
                                        actiontarget.setText("Login failed.");
                                    }
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

    public ScrapeSession getSession() {
        showAndWait();
        return session;
    }
}

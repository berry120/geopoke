/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geopoke;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import name.antonsmirnov.javafx.dialog.Dialog;

/**
 *
 * @author Michael
 */
public class Main extends Application {

    private GeoSession session;
    private StatusLabel statusLabel;
    private CacheList mainList;
    private WorldMap map;

    @Override
    public void start(Stage primaryStage) {
        session = new LoginStage().getSession();

        primaryStage.getIcons().add(new Image("file:img/logo.png"));
        primaryStage.setTitle("Geopoke");
        final BorderPane root = new BorderPane();
        HBox topPane = new HBox();
        Label topLabel = new Label("GC / URL:");
        final Button topButton = new Button("", new ImageView(new Image("file:img/add.png")));
        final Button pdfButton = new Button("", new ImageView(new Image("file:img/pdf.png")));
        pdfButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                File file = new ReportGenerator().generateReport(mainList.getCacheNodes(), map.getSnapshot(), null);
                try {
                    Desktop.getDesktop().open(file);
                } catch (IOException ex) {
                    Dialog.showError("Error opening PDF", "Sorry, couldn't open the generated file.");
                }
            }
        });
        final TextField topField = new TextField();
        topField.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                topButton.fire();
            }
        });
        topButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                statusLabel.setGettingCache();
                topButton.setDisable(true);
                new Thread() {
                    public void run() {
                        final Geocache cache = session.getCache(topField.getText());
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                if (cache == null||cache.getBestCoords()==null) {
                                    Dialog.showError("Error retrieving Geocache",
                                            "Did you definitely specify a valid GC number or URL?");
                                } else {
                                    mainList.addCache(cache);
                                }
                                topField.clear();
                                statusLabel.setIdle();
                                topButton.setDisable(false);
                            }
                        });
                    }
                }.start();
            }
        });
        topPane.getChildren().addAll(topLabel, topField, topButton, pdfButton);
        root.setTop(topPane);

        map = new WorldMap();
        mainList = new CacheList(map);
        ScrollPane mainScroll = new ScrollPane();
        mainScroll.setContent(mainList);
        mainScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        mainScroll.setFitToWidth(true);

        SplitPane pane = new SplitPane();
        pane.getItems().addAll(mainScroll, map);

        root.setCenter(pane);
        statusLabel = new StatusLabel();
        root.setBottom(statusLabel);

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setWidth(800);
        primaryStage.setHeight(600);
        primaryStage.show();
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}

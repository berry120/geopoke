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
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
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
    private Button pdfButton;

    @Override
    public void start(Stage primaryStage) {
        session = new LoginStage().getSession();
        if (session == null) { //Need a session to continue!
            Platform.exit();
        }

        primaryStage.getIcons().add(new Image("file:img/logo.png"));
        primaryStage.setTitle("Geopoke");
        final BorderPane root = new BorderPane();
        map = new WorldMap();

        HBox gcBar = new HBox();
        Label gcLabel = new Label("GC / URL:");
        final Button gcButton = new Button("", new ImageView(new Image("file:img/add.png")));
        Tooltip.install(gcButton, new Tooltip("Add the geocache to the list"));
        final TextField gcField = new TextField();
        gcField.textProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> ov, String t, String t1) {
                if(t1.isEmpty()) {
                    gcButton.setDisable(true);
                }
                else {
                    gcButton.setDisable(false);
                }
            }
        });
        gcField.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                gcButton.fire();
            }
        });
        gcButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                statusLabel.setGettingCache();
                gcButton.setDisable(true);
                gcField.setDisable(true);
                new Thread() {
                    public void run() {
                        final Geocache cache = session.getCache(gcField.getText());
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                if (cache == null || cache.getBestCoords() == null) {
                                    Dialog.showError("Error retrieving Geocache",
                                            "Did you definitely specify a valid GC number or URL?");
                                } else {
                                    mainList.addCache(cache);
                                }
                                gcField.clear();
                                statusLabel.setIdle();
                                gcField.setDisable(false);
                                gcButton.setDisable(false);
                                gcField.requestFocus();
                            }
                        });
                    }
                }.start();
            }
        });
        HBox.setHgrow(gcField, Priority.ALWAYS);
        gcBar.setAlignment(Pos.CENTER);
        gcBar.getChildren().addAll(gcLabel, gcField, gcButton);

        pdfButton = new Button("", new ImageView(new Image("file:img/pdf.png")));
        Tooltip.install(pdfButton, new Tooltip("Generate PDF"));
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
        mainList = new CacheList(map);
        mainList.addSizeListener(new SizeListener() {

            @Override
            public void sizeChanged(int oldSize, int newSize) {
                if(newSize==0) {
                    pdfButton.setDisable(true);
                }
                else {
                    pdfButton.setDisable(false);
                }
            }
        });
        pdfButton.setDisable(true);
        ScrollPane mainScroll = new ScrollPane();
        mainScroll.setContent(mainList);
        mainScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        mainScroll.setFitToWidth(true);
        StackPane mainStack = new StackPane();
        StackPane.setMargin(pdfButton, new Insets(15));
        mainStack.setAlignment(Pos.BOTTOM_RIGHT);
        mainStack.getChildren().addAll(mainScroll, pdfButton);
        VBox leftContent = new VBox();
        VBox.setVgrow(mainStack, Priority.ALWAYS);
        leftContent.getChildren().addAll(mainStack, gcBar);

        SplitPane pane = new SplitPane();
        pane.getItems().addAll(leftContent, map);

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

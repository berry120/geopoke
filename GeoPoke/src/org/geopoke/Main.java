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
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
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
    private Button gpsButton;
    private File currentFile;
    private ImageView liveLogo;

    @Override
    public void start(final Stage primaryStage) {
        session = new SessionFactory().newAPISession();
        if(session == null) { //Need a session to continue!
            System.exit(1);
        }

        primaryStage.getIcons().add(new Image("file:img/logo.png"));
        primaryStage.setTitle("Geopoke");

        final BorderPane root = new BorderPane();
        ToolBar toolbar = new ToolBar();
        final Button newButton = new Button("", new ImageView(new Image("file:img/new.png")));
        newButton.setTooltip(new Tooltip("Create a new list"));
        newButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                Dialog d = Dialog.buildConfirmation("New list?", "Create a new list?").addYesButton(
                        new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent t) {
                                mainList.removeAllCaches();
                                currentFile = null;
                            }
                        }).addNoButton(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent t) {
                    }
                }).build();
                d.showAndWait();
            }
        });
        final Button openButton = new Button("", new ImageView(new Image("file:img/open.png")));
        openButton.setTooltip(new Tooltip("Open a saved Geopoke list"));
        openButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                FileChooser chooser = new FileChooser();
                chooser.getExtensionFilters().add(FileFilters.GEOPOKE_LIST);
                final File f = chooser.showOpenDialog(primaryStage);
                openFile(f);
            }
        });
        final Button saveButton = new Button("", new ImageView(new Image("file:img/save.png")));
        saveButton.setDisable(true);
        saveButton.setTooltip(new Tooltip("Save this list"));
        saveButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                FileChooser chooser = new FileChooser();
                chooser.getExtensionFilters().add(FileFilters.GEOPOKE_LIST);
                if(currentFile == null) {
                    currentFile = chooser.showSaveDialog(primaryStage);
                    if(currentFile != null && !currentFile.getName().endsWith(".geopl")) {
                        currentFile = new File(currentFile.getAbsolutePath() + ".geopl");
                    }
                }
                if(currentFile != null) {
                    new ListSaver().saveCaches(mainList.getCaches(), currentFile);
                }
            }
        });
        toolbar.getItems().addAll(newButton, openButton, saveButton);

        map = new WorldMap();

        HBox gcBar = new HBox();
        Label gcLabel = new Label("GC:");
        final Button gcButton = new Button("", new ImageView(new Image("file:img/add.png")));
        gcButton.setDisable(true);
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
                                if(cache == null || cache.getBestCoords() == null) {
                                    Dialog.showError("Error retrieving Geocache",
                                            "Did you definitely specify a valid GC number?");
                                }
                                else {
                                    mainList.addCache(cache);
                                }
                                gcField.clear();
                                statusLabel.setIdle();
                                gcField.setDisable(false);
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
                liveLogo.setVisible(false);
                File file = new ReportGenerator().generateReport(mainList.getCacheNodes(), map.getSnapshot(), null);
                liveLogo.setVisible(true);
                try {
                    Desktop.getDesktop().open(file);
                }
                catch(IOException ex) {
                    Dialog.showError("Error opening PDF", "Sorry, couldn't open the generated file.");
                }
            }
        });
        gpsButton = new Button("", new ImageView(new Image("file:img/gps.png")));
        Tooltip.install(gpsButton, new Tooltip("Send to GPS"));
        gpsButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                new GPSSender().send(mainList.getCacheNodes());
            }
        });
        mainList = new CacheList(map);
        mainList.addSizeListener(new SizeListener() {
            @Override
            public void sizeChanged(int oldSize, int newSize) {
                if(newSize == 0) {
                    pdfButton.setDisable(true);
                    gpsButton.setDisable(true);
                    saveButton.setDisable(true);
                }
                else {
                    pdfButton.setDisable(false);
                    gpsButton.setDisable(false);
                    saveButton.setDisable(false);
                }
            }
        });
        pdfButton.setDisable(true);
        gpsButton.setDisable(true);
        ScrollPane mainScroll = new ScrollPane();
        mainScroll.setContent(mainList);
        mainScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        mainScroll.setFitToWidth(true);
        StackPane mainStack = new StackPane();
        StackPane.setMargin(pdfButton, new Insets(15));
        StackPane.setMargin(gpsButton, new Insets(0, 90, 15, 0));
        mainStack.setAlignment(Pos.BOTTOM_RIGHT);
        mainStack.getChildren().addAll(mainScroll, pdfButton, gpsButton);
        VBox leftContent = new VBox();
        VBox.setVgrow(mainStack, Priority.ALWAYS);
        statusLabel = new StatusLabel();
        leftContent.getChildren().addAll(toolbar, mainStack, gcBar, statusLabel);

        liveLogo = new ImageView(new Image("file:img/geocache_live.png"));
        StackPane.setMargin(liveLogo, new Insets(0, 10, 20, 0));
        StackPane rightContent = new StackPane();
        rightContent.setAlignment(Pos.BOTTOM_RIGHT);
        rightContent.getChildren().add(map);
        rightContent.getChildren().add(liveLogo);

        SplitPane pane = new SplitPane();
        pane.getItems().addAll(leftContent, rightContent);

        root.setCenter(pane);

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setWidth(800);
        primaryStage.setHeight(600);
        primaryStage.show();

        if(getParameters().getUnnamed().size() == 1) {
            openFile(new File(getParameters().getUnnamed().get(0)));
        }
    }

    /**
     * Open a file in the main window.
     * <p/>
     * @param file the file to open.
     */
    private void openFile(final File file) {
        if(file == null) {
            return;
        }
        final ModalProgressDialog dialog = new ModalProgressDialog("Opening, please wait...");
        dialog.show();
        new ListSaver().getCaches(session, file, new ProgressUpdator() {
            @Override
            public void update(double percentDone) {
                dialog.setProgress(percentDone);
            }
        }, new Callback<Geocache[]>() {
            @Override
            public void call(Geocache[] caches) {
                mainList.removeAllCaches();
                mainList.addCache(caches);
                currentFile = file;
            }
        });
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     * <p/>
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}

package ru.simplecloudstorage.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TreeView;
import javafx.scene.input.ZoomEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

public class MainWindow {
    @FXML
    private BorderPane mainPane;
    @FXML
    private TreeView leftView;
    @FXML
    private TreeView rightView;
    @FXML
    private HBox leftPane;
    @FXML
    private HBox rightPane;


    public void paneZoom(ZoomEvent zoomEvent) {
        leftPane.setPrefWidth((mainPane.getWidth()/2) - 40);
        rightPane.setPrefWidth((mainPane.getWidth()/2) - 40);
    }
}

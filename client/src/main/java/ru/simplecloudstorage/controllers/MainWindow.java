package ru.simplecloudstorage.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.ZoomEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import ru.simplecloudstorage.ClientApp;
import ru.simplecloudstorage.client.ClientDownloader;
import ru.simplecloudstorage.utils.SceneName;

public class MainWindow {

    @FXML
    public MenuItem settingsMenu;
    @FXML
    public Button uploadButton;
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
    private ClientApp application;
    private ClientDownloader clientDownloader;


    @FXML
    private void paneZoom(ZoomEvent zoomEvent) {
        leftPane.setPrefWidth((mainPane.getWidth()/2) - 40);
        rightPane.setPrefWidth((mainPane.getWidth()/2) - 40);
    }

    @FXML
    private void SettingsAction(ActionEvent actionEvent) {
        ClientApp.setRoot(SceneName.SETTINGS_WINDOW.getValue(), SceneName.MAIN_WINDOW.getValue());
    }


    @FXML
    private void closeMenuItemAction(ActionEvent actionEvent) {
    }

    @FXML
    private void uploadAction(ActionEvent actionEvent) {
    }

    public void setApplication(ClientApp clientApp) {
        this.application = clientApp;
    }

    public void setClientDownloader(ClientDownloader clientDownloader) {
        this.clientDownloader = clientDownloader;
    }

    public void setDownloader(ClientDownloader clientDownloader) {
        this.clientDownloader = clientDownloader;
    }

    public static void updatePCView(TreeItem<String> item) {

    }
}

package ru.simplecloudstorage.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.ZoomEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import ru.simplecloudstorage.ClientApp;
import ru.simplecloudstorage.utils.SceneName;

import java.io.IOException;

public class MainWindow {

//    private final String ERROR = "Ошибка";
//    private final String ERROR_TITLE = "Ошибка при выполнении";

    @FXML
    public MenuItem settingsMenu;
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


    @FXML
    private void paneZoom(ZoomEvent zoomEvent) {
        leftPane.setPrefWidth((mainPane.getWidth()/2) - 40);
        rightPane.setPrefWidth((mainPane.getWidth()/2) - 40);
    }

    @FXML
    private void SettingsAction(ActionEvent actionEvent) throws IOException {
        ClientApp.setRoot(SceneName.SETTINGS_WINDOW.getValue(), SceneName.MAIN_WINDOW.getValue());
    }


    @FXML
    private void closeMenuItemAction(ActionEvent actionEvent) {
    }
}

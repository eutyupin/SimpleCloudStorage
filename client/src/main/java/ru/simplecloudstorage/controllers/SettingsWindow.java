package ru.simplecloudstorage.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import ru.simplecloudstorage.ClientApp;
import ru.simplecloudstorage.utils.SceneName;

public class SettingsWindow {
    @FXML
    public void okButtonAction(ActionEvent actionEvent) {

    }

    public void cancelButtonAction(ActionEvent actionEvent) {
        ClientApp.authDialogSetRoot(ClientApp.getFromScene(), SceneName.SETTINGS_WINDOW.getValue());
    }
}

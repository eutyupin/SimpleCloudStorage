package ru.simplecloudstorage.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import ru.simplecloudstorage.ClientApp;

public class NewFolderDialog {

    private ClientApp application;

    @FXML
    private TextField directoryNameField;
    @FXML
    private Button okButton;
    @FXML
    private Button cancelButton;

    public void setApplication(ClientApp application) {
        this.application = application;
    }


    @FXML
    public void textFieldKeyPresset(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.ENTER)) okButtonAction();
    }

    @FXML
    public void okButtonAction() {
        application.getMainWindow().sendNewFolderCommand(directoryNameField.getText());
        application.getNewFolderDialogStage().close();
    }

    @FXML
    public void cancelButtonAction(ActionEvent actionEvent) {
        application.getNewFolderDialogStage().close();
    }
}

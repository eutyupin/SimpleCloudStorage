package ru.simplecloudstorage.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import ru.simplecloudstorage.ClientApp;
import ru.simplecloudstorage.client.ClientConnector;
import ru.simplecloudstorage.utils.ErrorDialog;
import ru.simplecloudstorage.utils.SceneName;

import java.io.IOException;

public class AuthDialog {

    private final String ERROR = "Ошибка";
    private final String ERROR_TITLE = "Не все поля заполнены";
    private final String ERROR_DESCRIPTION = "Заполните все поля и повторите попытку авторизации";


    private ClientApp application;
    private static ClientConnector connector;
    
    @FXML
    private Button okButton;
    @FXML
    private TextField loginField;
    @FXML
    private PasswordField passwordField;

    public void setClientApp(ClientApp clientApp) {
        this.application = clientApp;
    }

    @FXML
    private void regLinkClicked(MouseEvent mouseEvent) {
        ClientApp.authDialogSetRoot(SceneName.REGISTER_WINDOW.getValue(), SceneName.AUTH_DIALOG.getValue());
    }

    @FXML
    private void loginFieldKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER){
            passwordField.requestFocus();
        }
    }

    @FXML
    private void passowrdFieldKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.ENTER)){
            okButton.requestFocus();
        }
    }

    @FXML
    private void okButtonKeyPressed(KeyEvent keyEvent) {
        loginActions();
        application.getMainWindow().setLogin(loginField.getText());
    }

    @FXML
    private void okButtonClicked(MouseEvent mouseEvent) {
        loginActions();
    }

    private void loginActions() {
        if (checkFieldsHaveText()) {
            connector.userAuthorize(loginField.getText(), passwordField.getText().hashCode());
        } else {
            Platform.runLater(() -> new ErrorDialog(ERROR, ERROR_TITLE, ERROR_DESCRIPTION));
        }
    }

    private boolean checkFieldsHaveText() {
        if (loginField.getText().isEmpty()) {
            loginField.requestFocus();
            return false;
        }
        if (passwordField.getText().isEmpty()) {
            passwordField.requestFocus();
            return false;
        }
        return true;
    }

    @FXML
    private void settingsButtonAction(ActionEvent actionEvent) {
        ClientApp.authDialogSetRoot(SceneName.SETTINGS_WINDOW.getValue(), SceneName.AUTH_DIALOG.getValue());
    }

    public void prepareFieldsForLogin() {
        loginField.clear();
        passwordField.clear();
        loginField.requestFocus();
    }

    public void setConnector(ClientConnector clientConnector) {
        connector = clientConnector;
        RegisterDialog.setConnector(clientConnector);
    }
}
package ru.simplecloudstorage.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import ru.simplecloudstorage.ClientApp;
import ru.simplecloudstorage.client.ClientConnector;
import ru.simplecloudstorage.utils.ErrorDialog;
import ru.simplecloudstorage.utils.SceneName;

import java.io.IOException;

public class AuthDialog {

    private final String ERROR = "Ошибка";
    private final String ERROR_TITLE = "Не все поля заполнены";
    private final String ERROR_DESCRIPTION = "Заполните все поля и повторите попытку авторизации";

    @FXML
    private Button settingsButton;
    @FXML
    private Label registerLink;
    @FXML
    private Button okButton;
    @FXML
    private TextField loginField;
    @FXML
    private PasswordField passwordField;
    private ClientApp clientApp;
    private ClientConnector connector;
    private Stage authStage;

    public void setClientApp(ClientApp clientApp) {
        this.clientApp = clientApp;
    }

    @FXML
    private void regLinkClicked(MouseEvent mouseEvent) throws IOException {
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
    private void okButtonKeyPressed(KeyEvent keyEvent) throws IOException {
        loginActions();
    }

    @FXML
    private void okButtonClicked(MouseEvent mouseEvent) throws IOException {
        loginActions();
    }

    private void loginActions() {
        if (checkFieldsHaveText()) {
            connector.authorize(loginField.getText(), passwordField.getText().hashCode());

        } else {
            new ErrorDialog(ERROR, ERROR_TITLE, ERROR_DESCRIPTION);
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

    public void setAuthStage(Stage authStage) {
        this.authStage = authStage;
    }

    public void setConnector(ClientConnector connector) {
        this.connector = connector;
    }
}

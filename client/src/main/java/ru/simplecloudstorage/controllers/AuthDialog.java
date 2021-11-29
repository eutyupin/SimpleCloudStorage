package ru.simplecloudstorage.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import ru.simplecloudstorage.ClientApp;
import ru.simplecloudstorage.utils.ErrorDialog;
import ru.simplecloudstorage.utils.SceneName;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AuthDialog implements Initializable {

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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loginField.requestFocus();
    }

    @FXML
    private void regLinkClicked(MouseEvent mouseEvent) throws IOException {
        ClientApp.setRoot(SceneName.REGISTER_WINDOW.getValue(), SceneName.AUTH_DIALOG.getValue());
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

    private void loginActions() throws IOException {
        if (checkFieldsHaveText()) {
            ClientApp.setRoot(SceneName.MAIN_WINDOW.getValue(), SceneName.AUTH_DIALOG.getValue());
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
        ClientApp.setRoot(SceneName.SETTINGS_WINDOW.getValue(), SceneName.AUTH_DIALOG.getValue());
    }
}

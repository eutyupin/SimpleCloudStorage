package ru.simplecloudstorage.controllers;

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

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AuthDialog implements Initializable {
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
    public void regLinkClicked(MouseEvent mouseEvent) throws IOException {
        ClientApp.setRoot("registerdialog");
    }

    @FXML
    public void loginFieldKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER){
            passwordField.requestFocus();
        }
    }

    @FXML
    public void passowrdFieldKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.ENTER)){
            okButton.requestFocus();
        }
    }

    private void loginActions() throws IOException {
        if (checkFieldsHaveText()) {
            ClientApp.setRoot("mainwindow");
        } else {
            new ErrorDialog("Ошибка", "Не все поля заполнены","Заполните все поля " +
                    "и повторите попытку авторизации");
        }
    }

    public void okButtonKeyPressed(KeyEvent keyEvent) throws IOException {
        loginActions();
    }

    public void okButtonClicked(MouseEvent mouseEvent) throws IOException {
        loginActions();
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
}

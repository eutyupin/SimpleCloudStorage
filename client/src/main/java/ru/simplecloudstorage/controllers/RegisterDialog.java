package ru.simplecloudstorage.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import ru.simplecloudstorage.ClientApp;
import ru.simplecloudstorage.client.ClientConnector;
import ru.simplecloudstorage.utils.ErrorDialog;
import ru.simplecloudstorage.utils.SceneName;

import java.io.IOException;

public class RegisterDialog {

    private final String ERROR = "Ошибка";
    private final String ERROR_TITLE = "Не все поля заполнены";
    private final String ERROR_DESCRIPTION = "Заполните все поля и повторите попытку регистрации";

    private static ClientConnector connector;

    @FXML
    private TextField emailField;
    @FXML
    private Button okButton;
    @FXML
    private TextField loginRegField;
    @FXML
    private PasswordField pass2;
    @FXML
    private PasswordField pass1;

    public static void setConnector(ClientConnector clientConnector) {
        connector = clientConnector;
    }

    @FXML
    private void cancelAction(ActionEvent actionEvent) throws IOException {
        ClientApp.authDialogSetRoot(ClientApp.getFromScene(), SceneName.REGISTER_WINDOW.getValue());
    }

    @FXML
    private void pass2KeyTyped(KeyEvent keyEvent) {
      checkPasswordFieldsValue();
    }

    @FXML
    private void pass1KeyTyped(KeyEvent keyEvent) {
        checkPasswordFieldsValue();
    }



    @FXML
    private void emailKeyTyped(KeyEvent keyEvent) {
        checkemailFieldValue();
    }

    private void registerAction() {
        if (checkFieldsHaveText()) {
            connector.userRegister(loginRegField.getText(), pass1.getText().hashCode(), emailField.getText());
        } else {
            new ErrorDialog(ERROR, ERROR_TITLE, ERROR_DESCRIPTION);
        }
    }

    private boolean checkFieldsHaveText() {
        if (loginRegField.getText().isEmpty()) {
            loginRegField.requestFocus();
            return false;
        }
        if (pass1.getText().isEmpty()) {
            pass1.requestFocus();
            return false;
        }
        if (pass2.getText().isEmpty()) {
            pass2.requestFocus();
            return false;
        }
        if (emailField.getText().isEmpty()) {
            emailField.requestFocus();
            return false;
        }
        return true;
    }

    private void checkPasswordFieldsValue() {
        if (!pass1.getText().equals(pass2.getText())) {
            pass1.setStyle("-fx-background-color: #ffa3a3");
            pass2.setStyle("-fx-background-color: #ffa3a3");
        } else {
            pass1.setStyle("-fx-background-color: #b0ffb3");
            pass2.setStyle("-fx-background-color: #b0ffb3");
        }
    }

    private void checkemailFieldValue() {
        if (!emailField.getText().contains("@")) {
            emailField.setStyle("-fx-background-color: #ffa3a3");
        } else {
            emailField.setStyle("-fx-background-color: #b0ffb3");
        }
    }

    @FXML
    public void okAction(ActionEvent actionEvent) {
        registerAction();
    }

    @FXML
    public void loginFieldKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER){
            pass1.requestFocus();
        }
    }

    @FXML
    public void pass1KeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER){
            pass2.requestFocus();
        }
    }

    @FXML
    public void pass2KeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER){
            emailField.requestFocus();
        }
    }

    @FXML
    public void emailKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER){
            okButton.requestFocus();
        }
    }
}

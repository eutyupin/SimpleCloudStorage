package ru.simplecloudstorage.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import ru.simplecloudstorage.ClientApp;
import ru.simplecloudstorage.utils.ErrorDialog;
import ru.simplecloudstorage.utils.SceneName;

import java.io.IOException;

public class RegisterDialog {

    private final String ERROR = "Ошибка";
    private final String ERROR_TITLE = "Не все поля заполнены";
    private final String ERROR_DESCRIPTION = "Заполните все поля и повторите попытку регистрации";

    @FXML
    public TextField emailField;
    @FXML
    public TextField loginField;
    @FXML
    public Button okButton;
    @FXML
    private PasswordField pass2;
    @FXML
    private PasswordField pass1;

    @FXML
    private void cancelAction(ActionEvent actionEvent) throws IOException {
        ClientApp.setRoot(ClientApp.getFromScene(), SceneName.REGISTER_WINDOW.getValue());
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

        } else {
            new ErrorDialog(ERROR, ERROR_TITLE, ERROR_DESCRIPTION);
        }
    }

    @FXML
    private void okPressed(KeyEvent keyEvent) {
        registerAction();
    }

    @FXML
    private void okClick(MouseEvent mouseEvent) {
        registerAction();
    }

    private boolean checkFieldsHaveText() {
        if (loginField.getText().isEmpty()) {
            loginField.requestFocus();
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
}

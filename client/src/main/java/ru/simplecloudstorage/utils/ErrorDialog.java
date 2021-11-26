package ru.simplecloudstorage.utils;

import javafx.scene.control.Alert;

public class ErrorDialog extends Alert {

    public ErrorDialog(String title, String type, String description) {
        super(AlertType.ERROR);
        this.setTitle(title);
        this.setHeaderText(type);
        this.setContentText(description);
        this.showAndWait();
    }


}

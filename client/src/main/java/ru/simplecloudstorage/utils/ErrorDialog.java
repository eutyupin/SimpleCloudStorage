package ru.simplecloudstorage.utils;

import javafx.scene.control.Alert;

public class ErrorDialog extends Alert {

    public ErrorDialog(String title, String header, String contentText) {
        super(AlertType.ERROR);
        this.setTitle(title);
        this.setHeaderText(header);
        this.setContentText(contentText);
        this.showAndWait();
    }


}

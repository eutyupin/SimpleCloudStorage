package ru.simplecloudstorage.utils;

import javafx.scene.control.Alert;

public class InformationDialog extends Alert {

    public InformationDialog(String title, String header, String contentText) {
        super(AlertType.INFORMATION);
        this.setTitle(title);
        this.setHeaderText(header);
        this.setContentText(contentText);
        this.showAndWait();
    }
}

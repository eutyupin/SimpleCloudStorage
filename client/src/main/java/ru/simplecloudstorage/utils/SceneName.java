package ru.simplecloudstorage.utils;

public enum SceneName {

    MAIN_WINDOW("mainwindow.fxml"), AUTH_DIALOG("authdialog.fxml"),
    REGISTER_WINDOW("registerdialog.fxml"), NEW_FOLDER_DIALOG("newfolderdialog.fxml");

    private final String value;

    SceneName(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}

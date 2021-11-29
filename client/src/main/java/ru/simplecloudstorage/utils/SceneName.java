package ru.simplecloudstorage.utils;

public enum SceneName {

    MAIN_WINDOW("mainwindow.fxml"), AUTH_DIALOG("authdialog.fxml"),
    REGISTER_WINDOW("registerdialog.fxml"), SETTINGS_WINDOW("settingswindow.fxml");

    private String value;

    SceneName(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}

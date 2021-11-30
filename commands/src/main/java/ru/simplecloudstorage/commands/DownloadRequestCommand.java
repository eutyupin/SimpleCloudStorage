package ru.simplecloudstorage.commands;

public class DownloadRequestCommand extends BaseCommand {

    private String path;

    public DownloadRequestCommand() {
        super(CommandType.DOWNLOAD_REQUEST);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}

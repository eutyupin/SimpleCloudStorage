package ru.simplecloudstorage.commands;

public class DownloadRequestCommand extends BaseCommand {

    private String path;
    private String destinationPath;

    public DownloadRequestCommand() {
        super(CommandType.DOWNLOAD_REQUEST);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDestinationPath() {
        return destinationPath;
    }

    public void setDestinationPath(String destinationPath) {
        this.destinationPath = destinationPath;
    }
}

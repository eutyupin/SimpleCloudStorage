package ru.simplecloudstorage.commands;

public class DownloadRequestCommand extends BaseCommand {

    private String serverPath;
    private String destinationPath;

    public DownloadRequestCommand() {
        super(CommandType.DOWNLOAD_REQUEST);
    }

    public String getServerPath() {
        return serverPath;
    }

    public void setServerPath(String path) {
        this.serverPath = path;
    }

    public String getDestinationPath() {
        return destinationPath;
    }

    public void setDestinationPath(String destinationPath) {
        this.destinationPath = destinationPath;
    }
}

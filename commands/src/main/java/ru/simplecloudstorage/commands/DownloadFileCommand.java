package ru.simplecloudstorage.commands;

public class DownloadFileCommand extends BaseCommand {

    private long totalFileLength;
    private byte[] content;
    private long startPosition;
    private boolean endOfFile = false;
    private String destinationPath;

    public DownloadFileCommand() {
        super(CommandType.DOWNLOAD_FILE);
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public byte[] getContent() {
        return content;
    }

    public long getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(long startPosition) {
        this.startPosition = startPosition;
    }

    public boolean isEndOfFile() {
        return endOfFile;
    }

    public void setEndOfFile(boolean endOfFile) {
        this.endOfFile = endOfFile;
    }

    public long getTotalFileLength() {
        return totalFileLength;
    }

    public void setTotalFileLength(long totalFileLength) {
        this.totalFileLength = totalFileLength;
    }

    public String getDestinationPath() {
        return destinationPath;
    }

    public void setDestinationPath(String destinationPath) {
        this.destinationPath = destinationPath;
    }
}

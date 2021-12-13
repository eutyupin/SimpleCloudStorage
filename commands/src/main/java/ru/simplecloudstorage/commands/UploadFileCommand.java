package ru.simplecloudstorage.commands;

public class UploadFileCommand extends BaseCommand {

    private long totalFileLength;
    private byte[] content;
    private long startPosition;
    private boolean endOfFile = false;
    private String path;
    private String fileName;

    public UploadFileCommand() {
        super(CommandType.UPLOAD_FILE);
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

    public void setPath(String path) {
        this.path = path;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getPath() {
        return path;
    }

    public String getFileName() {
        return fileName;
    }
}

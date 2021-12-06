package ru.simplecloudstorage.commands;

public class DownloadFileCommand extends BaseCommand {

    private byte[] content;

    public DownloadFileCommand() {
        super(CommandType.DOWNLOAD_FILE);
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public byte[] getContent() {
        return content;
    }
}

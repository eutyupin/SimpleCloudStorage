package ru.simplecloudstorage.commands;

public class UploadFileCommand extends BaseCommand {
    private byte[] content;

    public UploadFileCommand() {
        super(CommandType.UPLOAD_FILE);
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }
}

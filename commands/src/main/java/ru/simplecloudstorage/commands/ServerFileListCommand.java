package ru.simplecloudstorage.commands;

import java.util.List;

public class ServerFileListCommand extends  BaseCommand{

    private List<String> fileList;

    public ServerFileListCommand() {
        super(CommandType.SERVER_FILE_LIST);
    }

    public List<String> getFileList() {
        return fileList;
    }

    public void setFileList(List<String> fileList) {
        this.fileList = fileList;
    }
}

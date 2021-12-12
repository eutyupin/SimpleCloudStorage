package ru.simplecloudstorage.commands;

import java.util.List;

public class ServerFileListCommand extends  BaseCommand{

    private List<String> fileList;
    private String rootDirectory;

    public ServerFileListCommand() {
        super(CommandType.SERVER_FILE_LIST);
    }

    public List<String>  getFileList() {
        return fileList;
    }

    public void setFileList(List<String>  fileList) {
        this.fileList = fileList;
    }

    public String getRootDirectory() {
        return rootDirectory;
    }

    public void setRootDirectory(String rootDirectory) {
        this.rootDirectory = rootDirectory;
    }
}

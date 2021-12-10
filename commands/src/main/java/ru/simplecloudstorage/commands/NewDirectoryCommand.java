package ru.simplecloudstorage.commands;

public class NewDirectoryCommand extends BaseCommand{
    private String path;
    public NewDirectoryCommand() {
        super(CommandType.NEW_DIRECTORY);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}

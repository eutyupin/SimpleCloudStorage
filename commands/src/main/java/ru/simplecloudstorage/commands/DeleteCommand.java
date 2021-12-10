package ru.simplecloudstorage.commands;

public class DeleteCommand extends BaseCommand{
    private String destinationPath;

    public DeleteCommand() {
        super(CommandType.DELETE);
    }

    public void setDestinationPath(String destinationPath) {
        this.destinationPath = destinationPath;
    }

    public String getDestinationPath() {
        return destinationPath;
    }
}

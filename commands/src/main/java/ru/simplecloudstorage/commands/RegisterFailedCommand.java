package ru.simplecloudstorage.commands;

public class RegisterFailedCommand extends BaseCommand{

    private String message;

    public RegisterFailedCommand() {
        super(CommandType.REGISTER_FALIED);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

package ru.simplecloudstorage.commands;

public class AuthFailedCommand extends BaseCommand {

    private String message;

    public AuthFailedCommand() {
        super(CommandType.AUTH_FAILED);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

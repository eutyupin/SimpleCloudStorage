package ru.simplecloudstorage.commands;

public class AuthCommand extends BaseCommand {

    private String login;
    private int passwordHash;

    public AuthCommand() {
        super(CommandType.AUTH);
    }

    public String getLogin() {
        return login;
    }

    public int getPasswordHash() {
        return passwordHash;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPasswordHash(int passwordHash) {
        this.passwordHash = passwordHash;
    }
}

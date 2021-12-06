package ru.simplecloudstorage.commands;

public class RegisterCommand extends BaseCommand{

    private String login, email;
    private int passwordHash;

    public RegisterCommand() {
        super(CommandType.REGISTER);
    }

    public String getLogin() {
        return login;
    }

    public int getPasswordHash() {
        return passwordHash;
    }

    public String getEmail() {
        return email;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPasswordHash(int passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

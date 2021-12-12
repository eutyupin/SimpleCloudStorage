package ru.simplecloudstorage.commands;

public class AuthCommand extends BaseCommand {

    private String login;
    private String password;

    public AuthCommand() {
        super(CommandType.AUTH);
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

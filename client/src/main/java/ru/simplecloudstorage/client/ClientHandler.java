package ru.simplecloudstorage.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import javafx.application.Platform;
import ru.simplecloudstorage.ClientApp;
import ru.simplecloudstorage.commands.*;
import ru.simplecloudstorage.utils.ErrorDialog;

public class ClientHandler extends SimpleChannelInboundHandler<BaseCommand> {

    private final String ERROR = "Ошибка";
    private final String ERROR_TITLE = "Ошибка авторизации";

    private ClientApp application;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, BaseCommand command) throws Exception {
        checkCommands(command, channelHandlerContext);
    }

    private void checkCommands(BaseCommand command, ChannelHandlerContext channelHandlerContext) {
        checkAuthCommand(command, channelHandlerContext);
    }

    private void checkAuthCommand(BaseCommand command, ChannelHandlerContext channelHandlerContext) {
        if (command.getType().equals(CommandType.AUTH_OK)) {
            application.authDialogClose();
        }
        if (command.getType().equals(CommandType.AUTH_FAILED)) {
            AuthFailedCommand authFailedCommand = (AuthFailedCommand) command;
            Platform.runLater(() -> {
                new ErrorDialog(ERROR, ERROR_TITLE, authFailedCommand.getMessage());
                application.getAuthDialog().prepareFieldsForLogin();
            });
        }
    }


    public void setApplication(ClientApp application) {
        this.application = application;
    }
}

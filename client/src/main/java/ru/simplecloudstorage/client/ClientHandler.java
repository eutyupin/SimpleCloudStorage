package ru.simplecloudstorage.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import javafx.application.Platform;
import ru.simplecloudstorage.ClientApp;
import ru.simplecloudstorage.commands.*;
import ru.simplecloudstorage.utils.ErrorDialog;

public class ClientHandler extends SimpleChannelInboundHandler<BaseCommand> {

    private ClientApp application;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, BaseCommand command) throws Exception {
        if (command.getType().equals(CommandType.AUTH_OK)) {
            application.authDialogClose();
        }
    }

    public void setApplication(ClientApp application) {
        this.application = application;
    }
}

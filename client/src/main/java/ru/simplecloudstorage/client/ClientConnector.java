package ru.simplecloudstorage.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import javafx.application.Platform;
import ru.simplecloudstorage.ClientApp;
import ru.simplecloudstorage.network.CustomFileDecoder;
import ru.simplecloudstorage.network.CustomFileEncoder;
import ru.simplecloudstorage.commands.*;
import ru.simplecloudstorage.utils.ErrorDialog;

public class ClientConnector {

    private static NioEventLoopGroup workGroup;
    private static Bootstrap client;
    private static Channel clientChannel;
    private static ClientHandler clientHandler;
    private ClientSender clientSender;
    private ClientApp application;

    public void run() {
        try {
            workGroup = new NioEventLoopGroup(1);
            clientHandler = new ClientHandler();
            clientHandler.setApplication(application);
            clientHandler.setMainWindow(application.getMainWindow());
            client = new Bootstrap()
                    .group(workGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel nioSocketChannel) {
                            nioSocketChannel.pipeline().addLast(
                                    new LengthFieldBasedFrameDecoder(1024*1024, 0,
                                            4,0,4),
                                    new LengthFieldPrepender(4),
                                    new CustomFileEncoder(),
                                    new CustomFileDecoder(),
                                    clientHandler
                            );
                        }
                    })
                    .option(ChannelOption.SO_KEEPALIVE, true);
            clientChannel = client.connect(ClientApp.consoleHost, ClientApp.consolePort).sync().channel();
            clientSender = new ClientSender(clientChannel);
            application.mainWindowSetDownloader(clientSender);
            clientChannel.closeFuture().sync();
        } catch (Exception e) {
            Platform.runLater(() -> new ErrorDialog("Ошибка", "ошибка соединения с сервером", e.getMessage()));
        } finally {
            connectorShutdown();
        }
    }

    public void connectorShutdown() {
        clientSender.getThreadPool().shutdownNow();
        clientChannel.close();
        workGroup.shutdownGracefully();
    }

    public void userAuthorize(String login, int passwordHash) {
        AuthCommand authCommand = new AuthCommand();
        authCommand.setLogin(login);
        authCommand.setPasswordHash(passwordHash);
        clientChannel.writeAndFlush(authCommand);
    }

    public void userRegister(String login, int passwordHash, String email) {
        RegisterCommand registerCommand = new RegisterCommand();
        registerCommand.setLogin(login);
        registerCommand.setPasswordHash(passwordHash);
        registerCommand.setEmail(email);
        clientChannel.writeAndFlush(registerCommand);
    }

    public void setApplication(ClientApp application) {
        this.application = application;
    }
}

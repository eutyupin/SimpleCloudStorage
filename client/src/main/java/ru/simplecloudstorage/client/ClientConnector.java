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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.simplecloudstorage.ClientApp;
import ru.simplecloudstorage.commands.AuthCommand;
import ru.simplecloudstorage.commands.RegisterCommand;
import ru.simplecloudstorage.network.CustomFileDecoder;
import ru.simplecloudstorage.network.CustomFileEncoder;
import ru.simplecloudstorage.utils.ErrorDialog;

public class ClientConnector {

    private static NioEventLoopGroup workGroup;
    private static Bootstrap client;
    private static Channel clientChannel;
    private static ClientHandler clientHandler;
    private ClientSender clientSender;
    private ClientApp application;
    private boolean normalCloseApplication = false;
    private static final Logger logger = LoggerFactory.getLogger(ClientConnector.class);

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
                                            3,0,3),
                                    new LengthFieldPrepender(3),
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
            if (!normalCloseApplication) {
                logger.error(e.getMessage());
                Platform.runLater(() -> new ErrorDialog("Ошибка", "ошибка соединения с сервером", e.getMessage()));
            }
        } finally {
            connectorShutdown();
            logger.info("Application closed");
        }
    }

    public void connectorShutdown() {
        clientSender.getThreadPool().shutdownNow();
        clientChannel.close();
        workGroup.shutdownGracefully();
        normalCloseApplication = true;
    }

    public void userAuthorize(String login, String password) {
        AuthCommand authCommand = new AuthCommand();
        authCommand.setLogin(login);
        authCommand.setPassword(password);
        clientChannel.writeAndFlush(authCommand);
        logger.info(String.format("Client %s trying to authorize", authCommand.getLogin()));
    }

    public void userRegister(String login, String password, String email) {
        RegisterCommand registerCommand = new RegisterCommand();
        registerCommand.setLogin(login);
        registerCommand.setPassword(password);
        registerCommand.setEmail(email);
        clientChannel.writeAndFlush(registerCommand);
        logger.info(String.format("Client %s trying to register", registerCommand.getLogin()));
    }

    public void setApplication(ClientApp application) {
        this.application = application;
    }
}

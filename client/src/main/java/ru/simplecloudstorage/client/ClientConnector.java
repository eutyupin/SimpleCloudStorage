package ru.simplecloudstorage.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import ru.simplecloudstorage.ClientApp;
import ru.simplecloudstorage.network.CustomFileDecoder;
import ru.simplecloudstorage.network.CustomFileEncoder;
import ru.simplecloudstorage.commands.*;
import ru.simplecloudstorage.utils.ErrorDialog;

import java.io.IOException;
import java.io.RandomAccessFile;

public class ClientConnector {
    private static int port;
    private static String host;
    private static final int DEFAULT_PORT_VALUE = 9000;
    private static final String DEFAULT_HOST_VALUE = "localhost";

    private static NioEventLoopGroup workGroup;
    private static Bootstrap client;
    private static Channel clientChannel;
    private static ClientHandler clientHandler;
    private ClientApp application;

    public void run() {
        setConnectParameters();
        try {
            workGroup = new NioEventLoopGroup(1);
            clientHandler = new ClientHandler();
            clientHandler.setApplication(application);
            client = new Bootstrap()
                    .group(workGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel nioSocketChannel) {
                            nioSocketChannel.pipeline().addLast(
                                    new LengthFieldBasedFrameDecoder(1024*1024, 0,
                                            2,0,2),
                                    new LengthFieldPrepender(2),
                                    new CustomFileEncoder(),
                                    new CustomFileDecoder(),
                                    clientHandler
                            );
                        }
                    })
                    .option(ChannelOption.SO_KEEPALIVE, true);
            clientChannel = client.connect(host, port).sync().channel();
            clientChannel.closeFuture().sync();
        } catch (Exception e) {
                new ErrorDialog("Ошибка", "ошибка соединения с сервером", e.getMessage());
        } finally {
            connectorShutdown();
        }
    }

    public void connectorShutdown() {
        workGroup.shutdownGracefully();
    }

    private void fileUploadToServer(String path) throws IOException {

        try(RandomAccessFile requestedFile = new RandomAccessFile("", "r")) {
            final UploadFileCommand uploadFileCommand = new UploadFileCommand();
            byte[] content = new byte[(int) requestedFile.length()];
            requestedFile.read(content);
            uploadFileCommand.setContent(content);
            clientChannel.writeAndFlush(uploadFileCommand);
            System.out.println("File " + content.length + " bytes was transferred to server");
        }
    }

    private void fileDownloadFromServer(String path) {
        DownloadRequestCommand downloadRequestCommand = new DownloadRequestCommand();
        downloadRequestCommand.setPath(path);
        clientChannel.writeAndFlush(downloadRequestCommand);
    }

    public void authorize(String login, String password) {
        AuthCommand authCommand = new AuthCommand();
        authCommand.setLogin(login);
        authCommand.setPassword(password);
        clientChannel.writeAndFlush(authCommand);

    }

    public static void setConnectParameters() {
            host = DEFAULT_HOST_VALUE;
            port = DEFAULT_PORT_VALUE;
    }

    public void setApplication(ClientApp application) {
        this.application = application;
    }
}

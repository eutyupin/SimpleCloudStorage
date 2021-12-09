package ru.simplecloudstorage.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import ru.simplecloudstorage.network.CustomFileDecoder;
import ru.simplecloudstorage.network.CustomFileEncoder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerConnector {
    private static int port;
    private static final int DEFAULT_PORT_VALUE = 9000;

    public void run() throws InterruptedException {
        NioEventLoopGroup connectorGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workGroup = new NioEventLoopGroup();
        ExecutorService threadPool = Executors.newCachedThreadPool();
        ServerHandler serverHandler = new ServerHandler(threadPool);
        try {
            ServerBootstrap server = new ServerBootstrap()
                    .group(connectorGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                            nioSocketChannel.pipeline().addLast(
                                    new LengthFieldBasedFrameDecoder(1024*1024, 0,
                                            4,0,4),
                                    new LengthFieldPrepender(4),
                                    new CustomFileEncoder(),
                                    new CustomFileDecoder(),
                                    serverHandler
                            );
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            Channel channel = server.bind(port).sync().channel();
            System.out.println("Simple Cloud Storage Server started..." +
                    System.lineSeparator() + "Server port is: " + port);
            channel.closeFuture().sync();
        } finally {
            {
                connectorGroup.shutdownGracefully();
                workGroup.shutdownGracefully();
                threadPool.shutdownNow();
                System.out.println("Simple Cloud Storage Server stopped!...");
            }
        }
    }

    public static int getDefaultPortValue() {
        return DEFAULT_PORT_VALUE;
    }

    public static int getPort() {
        return port;
    }

    public static void setPort(int port) {
        ServerConnector.port = port;
    }
}

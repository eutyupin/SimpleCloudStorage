package ru.simplecloudstorage.server;

import ru.simplecloudstorage.adapter.CustomFileDecoder;
import ru.simplecloudstorage.adapter.CustomFileEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

public class ServerConnector {
    private static int port;
    private static final int DEFAULT_PORT_VALUE = 9000;

    public void run() throws InterruptedException {
        NioEventLoopGroup connectorGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap server = new ServerBootstrap()
                    .group(connectorGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                            nioSocketChannel.pipeline().addLast(
                                    new LengthFieldBasedFrameDecoder(1024*1024, 0,
                                            2,0,2),
                                    new LengthFieldPrepender(2),
                                    new CustomFileEncoder(),
                                    new CustomFileDecoder(),
                                    new ServerHandler()
                            );
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            Channel channel = server.bind(port).sync().channel();
            System.out.println("Simple Cloud Storage Server started...");
            channel.closeFuture().sync();
        } finally {
            {
                connectorGroup.shutdownGracefully();
                workGroup.shutdownGracefully();
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

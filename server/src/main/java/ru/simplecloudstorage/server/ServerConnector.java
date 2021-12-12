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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.simplecloudstorage.network.CustomFileDecoder;
import ru.simplecloudstorage.network.CustomFileEncoder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerConnector {
    private static int port;
    private static final int DEFAULT_PORT_VALUE = 8189;
    private static final Logger logger = LoggerFactory.getLogger(ServerConnector.class);

    public void run() throws InterruptedException {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workGroup = new NioEventLoopGroup();
        ExecutorService threadPool = Executors.newCachedThreadPool();
        ServerHandler serverHandler = new ServerHandler(threadPool);
        try {
            ServerBootstrap server = new ServerBootstrap()
                    .group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                            nioSocketChannel.pipeline().addLast(
                                    new LengthFieldBasedFrameDecoder(1024*1024, 0,
                                            3,0,3),
                                    new LengthFieldPrepender(3),
                                    new CustomFileEncoder(),
                                    new CustomFileDecoder(),
                                    serverHandler
                            );
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            Channel channel = server.bind(port).sync().channel();
            logger.info(String.format("Simple Cloud Storage Server started... Server port is: %d", port));
            System.out.printf("Simple Cloud Storage Server started... Server port is: %d\n", port);
            channel.closeFuture().sync();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        finally {
            serverShutdown(bossGroup, workGroup, threadPool);
        }
    }

    private void serverShutdown(NioEventLoopGroup bossGroup, NioEventLoopGroup workGroup, ExecutorService threadPool) {
        bossGroup.shutdownGracefully();
        workGroup.shutdownGracefully();
        threadPool.shutdownNow();
        logger.info("Simple Cloud Storage Server stopped!...");
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
    public static void shutdown() {}
}

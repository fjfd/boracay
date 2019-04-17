package com.hex.bigdata.udsp.consumer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.json.JsonObjectDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Netty Socket Server
 */
public class SocketServer implements Runnable {

    private Logger logger = LoggerFactory.getLogger(SocketServer.class);

    private int socketPort;

    public SocketServer(int socketPort) {
        this.socketPort = socketPort;
    }

    @Override
    public void run() {
        logger.info("正在启动socket服务,端口号为：" + socketPort);
        EventLoopGroup bossGroup = new NioEventLoopGroup(1); // bossGroup设置一个线程，用于处理连接请求和建立连接
        /**
         * 如果没有设置线程数，当客户端请求的并发大于2*CPU核数时，内部会堵塞式的同一时间最多生成2*CPU核数的线程数。
         * 如果设置了线程数，当客户端请求的并发大于设置的线程数时，内部会堵塞式的同一时间最多生成设置的线程数。
         */
        EventLoopGroup workerGroup = new NioEventLoopGroup(); // workGroup线程池大小默认值2*CPU核数，在连接建立之后处理IO请求
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup) // 绑定线程池
                    .option(ChannelOption.SO_BACKLOG, 1024 * 1024) // 保持连接数
                    .option(ChannelOption.TCP_NODELAY, true) // 有数据立即发送
                    .childOption(ChannelOption.SO_KEEPALIVE, true) // 保持连接
                    .channel(NioServerSocketChannel.class) // 指定使用的channel
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() { // 服务端过滤器
                        @Override
                        public void initChannel(SocketChannel ch) {
//                            ch.pipeline().addLast(new SocketHandler());

//                            ch.pipeline() //
//                                    .addLast(new StringEncoder()) //
//                                    .addLast(new StringDecoder()) //
//                                    .addLast(new SocketHandler());

                            ch.pipeline() //
                                    .addLast(new JsonObjectDecoder (Integer.MAX_VALUE)) //
                                    .addLast(new SocketHandler());

                        }
                    });
            ChannelFuture f = b.bind(socketPort).sync(); // 服务器绑定端口监听
            if (f.isSuccess()) {
                logger.info("启动socket服务完成！");
            } else {
                logger.error("启动socket服务失败！");
            }
            f.channel().closeFuture().sync(); // 监听服务器关闭监听
        } catch (InterruptedException e) {
            logger.info("启动socket异常：" + e.getMessage());
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully(); // 关闭EventLoopGroup，释放掉所有资源包括创建的线程
            bossGroup.shutdownGracefully(); // 关闭EventLoopGroup，释放掉所有资源包括创建的线程
        }
    }
}

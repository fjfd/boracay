package com.hex.bigdata.udsp.jdbc.netty;

import com.hex.bigdata.udsp.config.NettyClientConfig;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.FixedRecvByteBufAllocator;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.internal.SystemPropertyUtil;

import java.net.InetSocketAddress;

/**
 * 长连接客户端工厂类
 */
public class NettyClientFactory extends AbstractClientFactory {

    /**
     * 共享IO线程
     **/
    public static final NioEventLoopGroup workerGroup = new NioEventLoopGroup ();

    public static final ByteBufAllocator byteBufAllocator;

    static {
        workerGroup.setIoRatio (SystemPropertyUtil.getInt ("ioratio", 100));

        if (SystemPropertyUtil.getBoolean ("bytebuf.pool", false)) {
            byteBufAllocator = PooledByteBufAllocator.DEFAULT;
        } else {
            byteBufAllocator = UnpooledByteBufAllocator.DEFAULT;
        }
    }

    @Override
    protected Client createClient(RemotingUrl url) throws Exception {
        final Bootstrap bootstrap = new Bootstrap ();
        NettyClientInitializer initializer = new NettyClientInitializer ();
        bootstrap.group (NettyClientFactory.workerGroup) //
                .option (ChannelOption.TCP_NODELAY, true) // 有数据立即发送
                .option (ChannelOption.SO_REUSEADDR, true) //
                .option (ChannelOption.SO_KEEPALIVE, true) // 保持连接
                .option (ChannelOption.ALLOCATOR, NettyClientFactory.byteBufAllocator) //
                // 最大接收返回数据大小，这里的值太小会导致返回大的结果被分段，值太大会导致对外内存溢出需要设置-XX:MaxDirectMemorySize=xxx加大。
                .option (ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator (NettyClientConfig.getBufferSize ()))
                .channel (NioSocketChannel.class) //
                .handler (initializer); //
        System.out.println ("========>" + NettyClientConfig.getBufferSize ());
        int connectTimeout = url.getConnectionTimeout ();
        if (connectTimeout < 1000) {
            bootstrap.option (ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000);
        } else {
            bootstrap.option (ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeout);
        }
        String targetIP = url.getHost ();
        int targetPort = url.getPort ();
        ChannelFuture future = bootstrap.connect (new InetSocketAddress (targetIP, targetPort));
        if (future.awaitUninterruptibly (connectTimeout) && future.isSuccess () && future.channel ().isActive ()) {
            Channel channel = future.channel ();
            return new NettyClient (url, channel, initializer);
        } else {
            future.cancel (true);
            future.channel ().close ();
            throw new Exception (targetIP);
        }
    }

}
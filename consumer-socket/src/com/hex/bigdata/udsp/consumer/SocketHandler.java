package com.hex.bigdata.udsp.consumer;

import com.hex.bigdata.udsp.common.constant.ErrorCode;
import com.hex.bigdata.udsp.common.util.JSONUtil;
import com.hex.bigdata.udsp.consumer.model.ConsumeRequest;
import com.hex.bigdata.udsp.consumer.model.Request;
import com.hex.bigdata.udsp.consumer.model.Response;
import com.hex.bigdata.udsp.consumer.service.ExternalConsumerService;
import com.hex.bigdata.udsp.consumer.service.LoggingService;
import com.hex.bigdata.udsp.consumer.util.RequestUtil;
import com.hex.goframe.util.WebApplicationContextUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import org.apache.log4j.Logger;

import java.net.InetSocketAddress;

/**
 * Netty Socket Server Handler
 */
public class SocketHandler extends SimpleChannelInboundHandler<ByteBuf> {
    private static Logger logger = Logger.getLogger(SocketHandler.class);

    private ExternalConsumerService consumerService;
    private LoggingService loggingService;

    public SocketHandler() {
        this.consumerService = (ExternalConsumerService) WebApplicationContextUtil.getBean("externalConsumerService");
        this.loggingService = (LoggingService) WebApplicationContextUtil.getBean("loggingService");
    }

    /**
     * 收到消息时，发送信息
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        System.out.println("服务端接受的消息 : " + msg);
        InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
        String clientIp = insocket.getAddress().getHostAddress();

        // 接收数据
        byte[] reqByte = new byte[msg.readableBytes()];
        msg.readBytes(reqByte);
        String reqJson = new String(reqByte, CharsetUtil.UTF_8);
        logger.info("接收报文内容:\n" + reqJson);

        // 消费
        String rsponse = consume(reqJson, clientIp);

        // 响应数据
        logger.info("响应报文内容:\n" + rsponse);
        byte[] rspByte = rsponse.getBytes(CharsetUtil.UTF_8);
        if (rspByte.length > 0) {
            ByteBuf buf = ctx.alloc().buffer(rspByte.length);
            buf.writeBytes(rspByte);
            ctx.writeAndFlush(buf).addListener(generateDataGenerator);
        }
    }

    /**
     * 执行消费
     *
     * @param json
     * @param ip
     * @return
     */
    private String consume(String json, String ip) {
        Response response = new Response();
        long bef = System.currentTimeMillis();
        try {
            Request request = RequestUtil.jsonToRequest(json);
            request.setRequestIp(ip);
            response = consumerService.externalConsume(request);
        } catch (Exception e) {
            e.printStackTrace();
            loggingService.writeResponseLog(response, new ConsumeRequest(), bef, 0,
                    ErrorCode.ERROR_000005.getValue(), ErrorCode.ERROR_000005.getName() + ":" + e.getMessage(), null);
        }
        return JSONUtil.parseObj2JSON(response);
    }

    /**
     * 发生异常关闭链路
     *
     * @param ctx
     * @param cause
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    /**
     * 建立连接时，发送消息
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("连接的客户端地址:" + ctx.channel().remoteAddress());
    }

    private final ChannelFutureListener generateDataGenerator = new ChannelFutureListener() {
        @Override
        public void operationComplete(ChannelFuture future) {
            if (!future.isSuccess()) {
                future.cause().printStackTrace();
                future.channel().close();
            }
        }
    };
}

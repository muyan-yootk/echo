package cn.mldn.netty.server.handler;

import cn.mldn.info.HostInfo;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

/**
 * 处理Echo的操作方式，其中ChannelInboundHandlerAdapter是针对于数据输入的处理
 * Netty是基于NIO的一种开发框架的封装，这里面和AIO是没有任何关系的。
 */
public class EchoServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            String inputData = msg.toString().trim();    // 将字节缓冲区的内容转为字符串
            System.err.println("{服务器}" + inputData);
            String echoData = "【ECHO】" + inputData + HostInfo.SEPARATOR; // 数据的回应处理
            ctx.writeAndFlush(echoData); // 回应的输出操作
        } finally {
            ReferenceCountUtil.release(msg) ; // 释放缓存
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close() ;
    }
}

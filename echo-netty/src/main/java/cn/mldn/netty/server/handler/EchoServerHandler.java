package cn.mldn.netty.server.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

/**
 * 处理Echo的操作方式，其中ChannelInboundHandlerAdapter是针对于数据输入的处理
 * Netty是基于NIO的一种开发框架的封装，这里面和AIO是没有任何关系的。
 */
public class EchoServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            // 表示要进行数据信息的读取操作，对于读取操作完成后也可以直接回应
            // 对于客户端发送来的数据信息，由于没有进行指定的数据类型，所以都统一按照Object进行接收
            ByteBuf buf = (ByteBuf) msg;       // 默认情况下的类型就是ByteBuf类型
            // 在进行数据类型转换的过程之中还可以进行编码指定（NIO的封装）
            String inputData = buf.toString(CharsetUtil.UTF_8);    // 将字节缓冲区的内容转为字符串
            System.err.println("{服务器}" + inputData);
            String echoData = "【ECHO】" + inputData + System.getProperty("line.separator"); // 数据的回应处理
            byte[] data = echoData.getBytes(); // 将字符串变为字节数组
            ByteBuf echoBuf = Unpooled.buffer(data.length);
            echoBuf.writeBytes(data);// 将内容保存在缓存之中
            ctx.writeAndFlush(echoBuf); // 回应的输出操作
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

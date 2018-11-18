package cn.mldn.netty.client.handler;

import cn.mldn.util.InputUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

/**
 * 需要进行数据的读取操作，服务器端处理完成的数据信息会进行读取
 */
public class EchoClientHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 只要服务器端发送完成信息之后，都会执行此方法进行内容的输出操作
        try {
            ByteBuf readBuf = (ByteBuf) msg ;
            String readData = readBuf.toString(CharsetUtil.UTF_8).trim() ; // 接收返回数据内容
            if("quit".equalsIgnoreCase(readData)) { // 结束操作
                System.out.println("【EXIT】拜拜，您已经结束了本次网络传输，再见！");
                ctx.close() ; // 关闭通道
            } else {
                System.out.println(readData); // 输出服务器端的响应内容
                String inputData = InputUtil.getString("请输入要发送的消息：") ;
                byte [] data = inputData.getBytes() ; // 将输入数据变为字节数组的形式
                ByteBuf sendBuf = Unpooled.buffer(data.length) ;
                sendBuf.writeBytes(data) ; // 将数据保存在缓存之中
                ctx.writeAndFlush(sendBuf) ; // 数据发送
            }
        } finally {
            ReferenceCountUtil.release(msg); // 释放缓存
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close() ;
    }
}

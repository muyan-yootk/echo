package cn.mldn.http.server.server.handler;

import cn.mldn.vo.Member;
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
            System.out.println(msg.getClass() + " **************");
            Member member = (Member) msg ;
            System.err.println("{服务器}" + member);
            member.setName("【ECHO】" + member.getName());
            ctx.writeAndFlush(member); // 回应的输出操作
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

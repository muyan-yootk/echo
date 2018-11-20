package cn.mldn.http.client.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.util.CharsetUtil;

public class HttpClientHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpResponse) {
            HttpResponse response = (HttpResponse) msg;
            System.out.println("【Netty-Http客户端】ContenType = " + response.headers().get(HttpHeaderNames.CONTENT_TYPE));
            System.out.println("【Netty-Http客户端】ContentLength = " + response.headers().get(HttpHeaderNames.CONTENT_LENGTH));
            System.out.println("【Netty-Http客户端】SET-COOKIE = " + ServerCookieDecoder.STRICT.decode(response.headers().get(HttpHeaderNames.SET_COOKIE)));
        }
        if (msg instanceof HttpContent) {
            HttpContent content = (HttpContent) msg ;
            ByteBuf buf = content.content() ; // 获取数据信息
            System.out.println("【Netty-HTTP客户端】" + buf.toString(CharsetUtil.UTF_8));
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}

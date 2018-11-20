package cn.mldn.http.server.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

public class HttpServerHandler extends ChannelInboundHandlerAdapter {
    private HttpRequest request;
    private DefaultFullHttpResponse response ;
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpRequest) {    // 实现HTTP请求处理操作
            this.request = (HttpRequest) msg; // 获取Request对象
            System.out.println("【Netty-HTTP服务器端】uri = " + this.request.uri() + "、Method = " + this.request.method() + "、Headers = " + request.headers());
            String content =
                    "<html>" +
                    "  <head>" +
                    "       <title>Hello Netty</title>" +
                    "   </head>" +
                    "   <body>" +
                    "       <h1>好好学习，天天向上</h1>" +
                    "   </body>" +
                    "</html>";   // HTTP服务器可以回应的数据就是HTML代码
            this.responseWrite(ctx,content);
        }
    }

    private void responseWrite(ChannelHandlerContext ctx,String content) {
        ByteBuf buf = Unpooled.copiedBuffer(content,CharsetUtil.UTF_8) ;
        this.response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,HttpResponseStatus.OK,buf) ;
        this.response.headers().set(HttpHeaderNames.CONTENT_TYPE,"text/html;charset=UTF-8") ; // 设置MIME类型
        this.response.headers().set(HttpHeaderNames.CONTENT_LENGTH,String.valueOf(buf.readableBytes())) ; // 设置回应数据长度
        ctx.writeAndFlush(this.response).addListener(ChannelFutureListener.CLOSE) ; // 数据回应完毕之后进行操作关闭
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}

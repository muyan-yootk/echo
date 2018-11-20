package cn.mldn.http.server.handler;

import cn.mldn.http.server.component.HttpSession;
import cn.mldn.http.server.component.HttpSessionManager;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;
import io.netty.util.CharsetUtil;

import java.util.Iterator;
import java.util.Set;

public class HttpServerHandler extends ChannelInboundHandlerAdapter {
    private HttpRequest request;
    private DefaultFullHttpResponse response ;
    private HttpSession session ;

    /**
     * 依据传入的标记内容进行是否向客户端Cookie中保存有SessionId数据的操作
     * @param exists
     */
    private void setSessionId(boolean exists) {
        if(exists == false) {    // 用户发送来的头信息里面不包含有SessionId内容
            String encodeCookie = ServerCookieEncoder.STRICT.encode(HttpSession.SESSIONID, HttpSessionManager.createSession()) ;
            this.response.headers().set(HttpHeaderNames.SET_COOKIE,encodeCookie) ;// 客户端保存Cookie数据
        }
    }

    /**
     * 当前所发送的请求里面是否存在有指定的 SessionID数据信息
     * @return 如果存在返回true，否则返回false
     */
    public boolean isHasSessionId() {
        String cookieStr = this.request.headers().get(HttpHeaderNames.COOKIE) ; // 获取客户端头信息发送来的Cookie数据
        if (cookieStr == null || "".equals(cookieStr)) {
            return false ;
        }
        Set<Cookie> cookieSet = ServerCookieDecoder.STRICT.decode(cookieStr);
        Iterator<Cookie> iter = cookieSet.iterator() ;
        while(iter.hasNext()) {
            Cookie cookie = iter.next() ;
            if(HttpSession.SESSIONID.equals(cookie.name())) {
                if (HttpSessionManager.isExists(cookie.value())) {
                    this.session = HttpSessionManager.getSession(cookie.value()) ;
                    return true ;
                }
            }
        }
        return false ;
    }


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
        this.setSessionId(this.isHasSessionId());
        ctx.writeAndFlush(this.response).addListener(ChannelFutureListener.CLOSE) ; // 数据回应完毕之后进行操作关闭
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}

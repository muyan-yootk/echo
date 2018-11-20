package cn.mldn.http.client;

import cn.mldn.http.client.handler.HttpClientHandler;
import cn.mldn.info.HostInfo;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;

public class HttpClient {
    public void run() throws Exception {
        // 1、如果现在客户端不同，那么也可以不使用多线程模式来处理;
        // 在Netty中考虑到代码的统一性，也允许你在客户端设置线程池
        EventLoopGroup group = new NioEventLoopGroup(); // 创建一个线程池
        try {
            Bootstrap client = new Bootstrap(); // 创建客户端处理程序
            client.group(group).channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new HttpResponseDecoder()); // 追加了处理器
                            socketChannel.pipeline().addLast(new HttpRequestEncoder()); // 追加了处理器
                            socketChannel.pipeline().addLast(new HttpClientHandler()); // 追加了处理器
                        }
                    });
            ChannelFuture channelFuture = client.connect(HostInfo.HOST_NAME, HostInfo.PORT).sync();

            String url = "http://" + HostInfo.HOST_NAME + ":" + HostInfo.PORT ; // HTTP访问地址

            DefaultFullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1,HttpMethod.GET,url) ;
            request.headers().set(HttpHeaderNames.HOST,"127.0.0.1") ;
            request.headers().set(HttpHeaderNames.CONNECTION,HttpHeaderValues.KEEP_ALIVE) ;
            request.headers().set(HttpHeaderNames.CONTENT_LENGTH,String.valueOf(request.content().readableBytes())) ;
            request.headers().set(HttpHeaderNames.COOKIE,"nothing") ;
            channelFuture.channel().writeAndFlush(request) ; // 发送请求
            channelFuture.channel().closeFuture().sync(); // 关闭连接
        } finally {
            group.shutdownGracefully();
        }
    }
}

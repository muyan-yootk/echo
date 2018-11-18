package cn.mldn.aio.server;

import cn.mldn.info.HostInfo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;

class EchoHandler implements CompletionHandler<Integer,ByteBuffer> {
    private AsynchronousSocketChannel clientChannel ;
    // 是否结束交互过程，exit = true表示结束，exit = false表示继续
    private boolean exit = false ;
    public EchoHandler(AsynchronousSocketChannel clientChannel) {
        this.clientChannel = clientChannel ;
    }
    @Override
    public void completed(Integer result, ByteBuffer buffer) {
        buffer.flip() ; // 读取之前需要执行重置处理
        String readMessage = new String(buffer.array(),0,buffer.remaining()).trim() ;
        String writeMessage = "【ECHO】" + readMessage ;  // 回应的数据信息
        if ("byebye".equalsIgnoreCase(readMessage)) {
            writeMessage = "【EXIT】拜拜，下次再见！" ;
            this.exit = true ; // 结束后期的交互
        }
        this.echoWrite(writeMessage);
    }

    private void echoWrite(String content) {
        ByteBuffer buffer = ByteBuffer.allocate(100) ;
        buffer.put(content.getBytes()) ;// 向缓存中保存数据
        buffer.flip() ;
        this.clientChannel.write(buffer, buffer, new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed(Integer result, ByteBuffer buf) {
                System.out.println(buf.hasRemaining());
                if (buf.hasRemaining()) {   // 缓存中是否有数据
                    EchoHandler.this.clientChannel.write(buffer,buffer,this);
                } else {
                    if(EchoHandler.this.exit == false) {    // 还没有结束
                        ByteBuffer readBuffer = ByteBuffer.allocate(100) ;
                        EchoHandler.this.clientChannel.read(readBuffer,readBuffer,new EchoHandler(EchoHandler.this.clientChannel)) ;
                    }
                }
            }

            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {
                try {
                    EchoHandler.this.clientChannel.close();
                } catch (IOException e) {
                }
            }
        });
    }

    @Override
    public void failed(Throwable exc, ByteBuffer attachment) {
        try {
            this.clientChannel.close();
        } catch (IOException e) {
        }
    }
}

// 连接接收的回调处理操作
class AcceptHandler implements CompletionHandler<AsynchronousSocketChannel,AIOServerThread> {

    @Override
    public void completed(AsynchronousSocketChannel channel, AIOServerThread aioThread) {
        aioThread.getServerChannel().accept(aioThread,this) ; // 接收连接
        ByteBuffer buffer = ByteBuffer.allocate(100) ;
        channel.read(buffer,buffer,new EchoHandler(channel)) ;
    }

    @Override
    public void failed(Throwable exc, AIOServerThread aioThread) {
        System.err.println("客户端连接创建失败....");
        aioThread.getLatch().countDown();
    }
}

// 设置一个单独的服务器的处理线程
class AIOServerThread implements Runnable {
    private AsynchronousServerSocketChannel serverChannel = null; // 服务器通道
    private CountDownLatch latch = null ; // 做一个同步处理操作

    public AIOServerThread() throws Exception {
        this.latch = new CountDownLatch(1) ;// 等待线程数量为1
        this.serverChannel = AsynchronousServerSocketChannel.open(); // 打开服务器的通道
        this.serverChannel.bind(new InetSocketAddress(HostInfo.PORT)) ; // 绑定端口
        System.out.println("服务器启动成功，监听端口为：" + HostInfo.PORT);
    }

    public AsynchronousServerSocketChannel getServerChannel() {
        return serverChannel;
    }

    public CountDownLatch getLatch() {
        return latch;
    }

    @Override
    public void run() {
        this.serverChannel.accept(this,new AcceptHandler()) ;
        try {
            this.latch.await(); // 线程等待
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}


public class AIOEchoServer {
    public static void main(String[] args) throws Exception {
        new Thread(new AIOServerThread()).start();
    }
}

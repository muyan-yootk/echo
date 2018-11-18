package cn.mldn.aio.server.temp;


import cn.mldn.info.HostInfo;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;


/**
 * 2、实现客户端的回应处理操作
 */
class EchoHandler implements CompletionHandler<Integer,ByteBuffer> {
    private AsynchronousSocketChannel clientChannel ;
    private boolean exit = false ; // 进行操作的结束标记判断
    public EchoHandler(AsynchronousSocketChannel clientChannel) {
        this.clientChannel = clientChannel ;
    }
    @Override
    public void completed(Integer result, ByteBuffer buffer) {
        buffer.flip() ;
        String readMessage = new String(buffer.array(),0,buffer.remaining()).trim() ;
        System.out.println("【服务器端接收到消息内容】" + readMessage);
        String resultMessage = "【ECHO】" + readMessage + "\n" ; // 回应信息
        if("exit".equalsIgnoreCase(readMessage)){
            resultMessage = "【EXIT】Bye Bye ... kiss + \n" ;
            this.exit = true ; // 结束
        }
        this.echoWrite(resultMessage);
    }

    private void echoWrite(String result) {
        ByteBuffer buffer = ByteBuffer.allocate(50) ;
        buffer.put(result.getBytes()) ;
        buffer.flip() ;
        this.clientChannel.write(buffer, buffer, new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed(Integer result, ByteBuffer buffer) {
                if(buffer.hasRemaining()) {  // 当前有数据
                    EchoHandler.this.clientChannel.write(buffer,buffer,this) ;
                } else {
                    if(EchoHandler.this.exit == false) {    // 需要继续交互
                        ByteBuffer readBuffer = ByteBuffer.allocate(50) ;
                        EchoHandler.this.clientChannel.read(readBuffer,readBuffer,new EchoHandler(EchoHandler.this.clientChannel)) ;
                    }
                }
            }

            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {
                try {
                    EchoHandler.this.clientChannel.close();
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void failed(Throwable exc, ByteBuffer attachment) {
        try {
            this.clientChannel.close(); // 关闭通道
        } catch(Exception e) {}
    }
}

/**
 * 1、实现客户端连接回调的处理操作
 */
class AcceptHandler implements CompletionHandler<AsynchronousSocketChannel,AIOServerThread> {

    @Override
    public void completed(AsynchronousSocketChannel result, AIOServerThread attachment) {
        attachment.getServerChannel().accept(attachment,this) ; // 接收连接对象
        ByteBuffer buffer = ByteBuffer.allocate(50) ;
        result.read(buffer,buffer,new EchoHandler(result));
    }

    @Override
    public void failed(Throwable exc, AIOServerThread attachment) {
        System.out.println("服务器端客户端连接失败 ...");
        attachment.getLatch().countDown(); // 恢复执行

    }
}

class AIOServerThread implements Runnable { // 是进行AIO处理的线程类
    private AsynchronousServerSocketChannel serverChannel ;
    private CountDownLatch latch ;  // 进行线程等待操作
    public AIOServerThread() throws Exception {
        this.latch = new CountDownLatch(1) ; // 设置一个线程等待个数
        this.serverChannel = AsynchronousServerSocketChannel.open() ; // 打开异步的通道
        this.serverChannel.bind(new InetSocketAddress(HostInfo.PORT)) ; // 绑定服务端口
        System.out.println("服务器启动成功，在" + HostInfo.PORT + "端口上监听服务 ...");
    }

    public AsynchronousServerSocketChannel getServerChannel() {
        return serverChannel;
    }

    public CountDownLatch getLatch() {
        return latch;
    }

    @Override
    public void run() {
        this.serverChannel.accept(this,new AcceptHandler()) ; // 等待客户端连接
        try {
            this.latch.await(); // 进入等待时机
        } catch (Exception e) {}
    }
}

public class AIOEchoServer {
    public static void main(String[] args) throws Exception {
        new Thread(new AIOServerThread()).start();
    }
}
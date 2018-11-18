package cn.mldn.nio.server;

import cn.mldn.info.HostInfo;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NIOEchoServer {
    private static class EchoClientHandler implements Runnable {
        private SocketChannel clientChannel ; // 客户端通道
        private boolean flag = true ; // 循环处理标记
        public EchoClientHandler(SocketChannel clientChannel) {
            this.clientChannel = clientChannel ;
            // 严格意义上来讲，当已经成功的连接上了服务器，并且需要进行进一步处理之前要发送一些消息给客户端
        }
        @Override
        public void run() {
            ByteBuffer buffer = ByteBuffer.allocate(50) ; // 50个缓冲区
            try {
                while(this.flag) {  // 需要不断进行交互
                    buffer.clear() ;    // 清空缓冲区
                    int readCount = this.clientChannel.read(buffer) ; // 向缓冲区之中读取数据
                    String readMessage = new String(buffer.array(),0,readCount).trim() ;
                    String writeMessage = "【ECHO】" + readMessage + "\n" ; // 回应数据信息
                    if("byebye".equalsIgnoreCase(readMessage)) {
                        writeMessage = "【EXIT】拜拜，下次再见！" ;
                        this.flag = false ;
                     }
                     // 数据输入通过缓存的形式完成，而数据的输出同样需要进行缓存操作
                    buffer.clear() ; // 为了写入新的返回数据而定义
                    buffer.put(writeMessage.getBytes()) ; // 发送内容
                    buffer.flip() ; // 重置缓冲区
                    this.clientChannel.write(buffer) ;// 回应数据
                }
                this.clientChannel.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        // 1、NIO的实现考虑到性能的问题以及响应时间问题，需要设置一个线程池，采用固定大小的线程池
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        // 2、NIO的处理是基于Channel控制的，所以有一个Selector就是负责管理所有的Channel
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        // 3、需要为其设置一个非阻塞的状态机制
        serverSocketChannel.configureBlocking(false); // 非阻塞模式
        // 4、服务器上需要提供有一个网络的监听端口
        serverSocketChannel.bind(new InetSocketAddress(HostInfo.PORT));
        // 5、需要设置一个Selector，作为一个选择器的出现，目的是管理所有的Channel
        Selector selector = Selector.open();
        // 6、将当前的Channel注册到Selector之中
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT); // 连接时处理
        System.out.println("服务器已经启动成功，服务器的监听端口为：" + HostInfo.PORT);
        // 7、NIO采用的是轮询模式，每当发现有用户连接的时候就需要启动一个线程（线程池管理）
        int keySelect = 0; // 接收轮询状态
        while((keySelect = selector.select()) > 0) {    // 实现了轮询处理
            Set<SelectionKey> selectionKeys = selector.selectedKeys() ;   // 获取全部的Key
            Iterator<SelectionKey> selectionIter = selectionKeys.iterator() ;
            while(selectionIter.hasNext()) {
                SelectionKey selectionKey = selectionIter.next() ;  // 获取每一个Key的信息
                if (selectionKey.isAcceptable()) {  // 为连接模式
                    SocketChannel clientChannel = serverSocketChannel.accept() ; // 等待连接
                    if (clientChannel != null) {
                        executorService.submit(new EchoClientHandler(clientChannel)) ;
                    }
                }
                selectionIter.remove();
            }
        }
        executorService.shutdown();
        serverSocketChannel.close();
    }
}

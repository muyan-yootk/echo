package cn.mldn.bio.server;


import cn.mldn.info.HostInfo;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BIOEchoServer {
    public static void main(String[] args) throws Exception{
        ServerSocket serverSocket = new ServerSocket(HostInfo.PORT) ;// 设置监听端口
        System.out.println("服务器端已经启动，监听的端口为：" + HostInfo.PORT);
        boolean flag = true ;
        ExecutorService executorService = Executors.newFixedThreadPool(10) ;
        while(flag) {
            Socket client = serverSocket.accept() ;
            executorService.submit(new EchoClientHandler(client)) ;
        }
        executorService.shutdown() ;
        serverSocket.close() ;
    }

    private static class EchoClientHandler implements Runnable {
        private Socket client ; // 每一个客户端都需要启动一个任务(task)来执行。
        private Scanner scanner ;
        private PrintStream out ;
        private boolean flag = true ;   // 循环标记
        public EchoClientHandler(Socket client) {
            this.client = client ; // 保存每一个客户端操作
            try {
                this.scanner = new Scanner(this.client.getInputStream()) ;
                this.scanner.useDelimiter("\n") ; // 设置换行符
                this.out = new PrintStream(this.client.getOutputStream()) ;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        @Override
        public void run() {
            while(this.flag) {
                if (this.scanner.hasNext()) {   // 现在有数据进行输入
                    String val = this.scanner.next().trim() ; // 去掉多余的空格内容
                    System.err.println("{服务器端}" + val);
                    if("byebye".equalsIgnoreCase(val)) {
                        this.out.println("ByeByeByte...");
                        this.flag = false ;
                    } else {
                        out.println("【ECHO】" + val);
                    }
                }
            }
            this.scanner.close();
            this.out.close();
            try {
                this.client.close();
            } catch (IOException e) {
            }
        }
    }
}

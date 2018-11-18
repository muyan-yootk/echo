package cn.mldn.bio.client;

import cn.mldn.info.HostInfo;
import cn.mldn.util.InputUtil;

import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class BIOEchoClient {
    public static void main(String[] args) throws Exception {
        Socket client = new Socket(HostInfo.HOST_NAME,HostInfo.PORT) ;  // 定义连接的主机信息
        Scanner scan = new Scanner(client.getInputStream()) ;   // 获取服务器端的响应数据
        scan.useDelimiter("\n") ;
        PrintStream out = new PrintStream(client.getOutputStream()) ; // 向服务器端发送信息内容
        boolean flag = true ; // 交互的标记
        while(flag) {
            String inputData = InputUtil.getString("请输入要发送的内容：").trim() ;
            out.println(inputData); // 把数据发送到服务器端上
            if(scan.hasNext()) {
                String str = scan.next().trim() ;
                System.out.println(str);
            }
            if ("byebye".equalsIgnoreCase(inputData)) {
                flag = false ;
            }
        }
        client.close();
    }
}

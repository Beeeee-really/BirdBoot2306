package com.birdboot.core;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

/**
 * 对指定客户端进行HTTP交流
 */
public class ClientHandler implements Runnable {
    private Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {

        //读取浏览器发送的内容
        try {
            InputStream is = socket.getInputStream();
            int d;
            char pre = 'a', cur = 'a';
            StringBuilder stringBuilder = new StringBuilder();
            while ((d = is.read()) != -1) {
                cur = (char) d;
                if (pre == 13 && cur == 10) {
                    break;
                }
                stringBuilder.append(cur);
                pre = cur;
            }
            String line = stringBuilder.toString().trim();
            System.out.println("请求行:" + line);
            //请求相关信息
            String method;
            String uri;
            String protocol;

            System.out.println("method" + method);//GET
            System.out.println("uri:" + uri);//index.html
            System.out.println("protocol:" + protocol);//HTTP/1.1
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}

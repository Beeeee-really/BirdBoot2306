package com.birdboot.core;

import com.birdboot.http.HTTPServletRequest;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

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
        try {
            //1.解析请求
            HTTPServletRequest httpServletRequest = new HTTPServletRequest(socket);
            String path = httpServletRequest.getUri();
            System.out.println("请求路径:" + path);

            //处理请求
            //将响应发送出去
//            File file = new File("./v5/src/main/resources/static/index.html");
            File baseDir = new File(ClientHandler.class.getClassLoader().getResource(".").toURI());
            //定位类加载路径下的目录
            File staticDir = new File(baseDir, "static");
            //定位static并返回
            File file = new File(staticDir, "index.html");


        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

//    public static void main(String[] args) throws URISyntaxException {
//        File baseDir = new File(ClientHandler.class.getClassLoader().getResource(".").toURI());
//        //定位类加载路径下的目录
//        File staticDir = new File(baseDir, "static");
//        //定位static并返回
//        File file = new File(staticDir, "index.html");
//        System.out.println(file.exists());
//    }


}

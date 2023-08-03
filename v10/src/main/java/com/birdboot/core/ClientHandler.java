package com.birdboot.core;

import com.birdboot.http.HTTPServletRequest;
import com.birdboot.http.HTTPServletResponse;

import java.io.*;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
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
            //1解析请求
            //由于将解析的代码移动到请求对象的构造器中了，因此这里实例化等同于解析了请求
            HTTPServletRequest request = new HTTPServletRequest(socket);
            HTTPServletResponse response = new HTTPServletResponse(socket);


            //2处理请求
            DispatcherServlet.getDispatcherServlet().service(request, response);

            //3发送响应
            response.response();


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                //一次HTTP交互后，断开链接
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

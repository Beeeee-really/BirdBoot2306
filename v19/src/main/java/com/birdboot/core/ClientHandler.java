package com.birdboot.core;

import com.birdboot.http.EmptyRequestException;
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
    public ClientHandler(Socket socket){
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            //1解析请求
            HTTPServletRequest request = new HTTPServletRequest(socket);
            HTTPServletResponse response = new HTTPServletResponse(socket);

            //2处理请求
            DispatcherServlet.getInstance().service(request,response);


            //3发送响应
            response.response();


        } catch (IOException e) {
            e.printStackTrace();
        } catch (EmptyRequestException e) {
            //空请求异常不需要做任何处理
        } finally {
            try {
                //一次HTTP交互后，断开链接
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}

package com.birdboot.core;

import com.birdboot.http.HTTPServletRequest;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
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
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


}

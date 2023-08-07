package com.birdboot.core;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 启动主类
 */
public class BirdBootApplication {
    private ServerSocket serverSocket;
    private ExecutorService threadPool;

    private BirdBootApplication() {
        try {
            System.out.println("正在启动服务器...");
            serverSocket = new ServerSocket(8088);
            threadPool = Executors.newFixedThreadPool(50);
            System.out.println("服务器启动完毕!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        try {

            while (true) {
                System.out.println("等待客户端连接...");
                Socket socket = serverSocket.accept();
                System.out.println("一个客户端连接了!");
                ClientHandler handler = new ClientHandler(socket);
                threadPool.execute(handler);//交给线程池
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        BirdBootApplication birdBootApplication = new BirdBootApplication();
        birdBootApplication.start();
    }
}

package com.birdboot.core;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * 启动主类
 */
public class BirdBootApplication {
    private ServerSocket serverSocket;

    private BirdBootApplication() {
        try {
            System.out.println("正在启动服务器...");
            serverSocket = new ServerSocket(8088);
            System.out.println("服务器启动完毕!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {

    }

    public static void main(String[] args) {
        BirdBootApplication birdBootApplication = new BirdBootApplication();
        birdBootApplication.start();
    }
}

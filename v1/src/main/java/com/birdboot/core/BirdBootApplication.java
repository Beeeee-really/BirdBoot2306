package com.birdboot.core;

import java.net.ServerSocket;

/**
 * 启动主类
 */
public class BirdBootApplication {
    private ServerSocket serverSocket;

    private BirdBootApplication() {

    }

    public void start() {

    }

    public static void main(String[] args) {
        BirdBootApplication birdBootApplication = new BirdBootApplication();
        birdBootApplication.start();
    }
}

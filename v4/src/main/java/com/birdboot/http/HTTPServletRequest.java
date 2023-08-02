package com.birdboot.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * 请求对象
 * 请求方式，抽象路径，协议版本
 */
public class HTTPServletRequest {
    private Socket socket;

    public HTTPServletRequest(Socket socket) throws IOException {
        this.socket = socket;
        //读取浏览器发送的内容

            String line = readLine();
            System.out.println("请求行:" + line);
            //请求相关信息
            String method;
            String uri;
            String protocol;

            String[] data = line.split("\\s");
            method = data[0];
            uri = data[1];
            protocol = data[2];

            System.out.println("method:" + method);//GET
            System.out.println("uri:" + uri);//index.html
            System.out.println("protocol:" + protocol);//HTTP/1.1


            //读取消息头
            Map<String, String> headers = new HashMap<>();
            while (true) {
                line = readLine();
                if (line.isEmpty()) {
                    break;
                }
                System.out.println("消息头:" + line);
                data = line.split("\\s");
                headers.put(data[0], data[1]);


            }


            System.out.println(headers);

    }


    /**
     * 读取方法
     *
     * @return
     * @throws IOException
     */
    private String readLine() throws IOException {
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
        return stringBuilder.toString().trim();

    }


}

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
    private String method;
    private String uri;
    private String protocol;
    //读取消息头
    private Map<String, String> headers = new HashMap<>();
    private Socket socket;

    public HTTPServletRequest(Socket socket) throws IOException {
        this.socket = socket;
        //读取浏览器发送的内容


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


    public String getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getHeaders(String name) {
        return headers.get(name);
    }


    private void parseHeadersLine() throws IOException {
        String line = readLine();
        System.out.println("请求行:" + line);
        //请求相关信息


        String[] data = line.split("\\s");
        method = data[0];
        uri = data[1];
        protocol = data[2];

        System.out.println("method:" + method);//GET
        System.out.println("uri:" + uri);//index.html
        System.out.println("protocol:" + protocol);//HTTP/1.1

    }

    private void parseHeaders() throws IOException {
        while (true) {
            String line = readLine();
            if (line.isEmpty()) {
                break;
            }
            System.out.println("消息头:" + line);
            String[] data = line.split("\\s");
            headers.put(data[0], data[1]);


        }


        System.out.println(headers);

    }

    private void parseContent() {

    }
}

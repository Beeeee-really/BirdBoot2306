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
    //请求行相关信息
    private String method;//请求方式
    private String uri;//抽象路径
    private String protocol;//协议版本

    //消息头相关信息
    private Map<String, String> headers = new HashMap<>();


    public HTTPServletRequest(Socket socket) throws IOException {
        this.socket = socket;
        //读取请求行
        parseRequestLine();
        //读取消息头
        parseHeaders();
        //读取消息正文
        parseContent();

    }
    //解析请求行
    private void parseRequestLine() throws IOException {
        String line = readLine();
        System.out.println("请求行:" + line);//GET /index.html HTTP/1.1

        String[] data = line.split("\\s");//按照空白字符拆分
        method = data[0];
        uri = data[1];
        protocol = data[2];

        System.out.println("method:" + method);// GET
        System.out.println("uri:" + uri);// /index.html
        System.out.println("protocol:" + protocol);// HTTP/1.1
    }
    //解析消息头
    private void parseHeaders() throws IOException {
        while (true) {
            String line = readLine();
            if (line.isEmpty()) {//如果读取一行内容为空字符串，说明单独读取到了回车+换行
                break;
            }
            System.out.println("消息头:" + line);
            //将消息头按照"冒号空格"拆分为消息头名字和对应的值。以key，value形式存入headers
            String[] data = line.split(":\\s");
            headers.put(data[0], data[1]);
        }
        System.out.println("headers:" + headers);
    }
    //解析消息正文
    private void parseContent(){}




    /**
     * 读取来自浏览器发送过来的一行字符串
     *
     * @return
     * @throws IOException
     */
    private String readLine() throws IOException {
        InputStream in = socket.getInputStream();
        int d;
        char pre = 'a', cur = 'a';//pre表示上次读取的字符，cur表示本次读取的字符
        StringBuilder builder = new StringBuilder();
        while ((d = in.read()) != -1) {
            cur = (char) d;//本次读取到的字符
            if (pre == 13 && cur == 10) {//是否已经连续读取到了回车+换行
                break;//暂停读取
            }
            builder.append(cur);//将本次读取的字符拼接
            pre = cur;//下次读取前，将本次读取的字符记作上次读取的字符
        }
        return builder.toString().trim();
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

    public String getHeader(String name) {
        return headers.get(name);
    }
}

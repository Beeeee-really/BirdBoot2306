package com.birdboot.http;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 响应对象
 */
public class HTTPServletResponse {
    private static MimetypesFileTypeMap mftm = new MimetypesFileTypeMap();

    private Socket socket;

    //状态行相关信息
    private int statusCode;//状态代码
    private String statusReason;//状态描述

    //响应头相关信息
    private Map<String,String> headers = new HashMap<>();

    //响应正文相关信息
    private File contentFile;//正文文件


    public HTTPServletResponse(Socket socket){
        this.socket = socket;
    }

    /**
     * 该方法用于将当前响应对象内容以标准的响应格式发送给浏览器
     */
    public void response() throws IOException {
        //发送状态行
        sendStatusLine();
        //发送响应头
        sendHeaders();
        //发送响应正文
        sendContent();
    }
    //发送状态行
    private void sendStatusLine() throws IOException {
        println("HTTP/1.1" + " " + statusCode + " " + statusReason);
    }
    //发送响应头
    private void sendHeaders() throws IOException {
        /*
            headers:
                key             value
            Content-Type        text/html
            Content-Length      245
            Server              BirdWebServer
            ...                 ...
         */
        //遍历headers，将所有的响应头发送给浏览器
        Set<Map.Entry<String,String>> entrySet = headers.entrySet();
        for (Map.Entry<String,String> e : entrySet){
            String key = e.getKey();
            String value = e.getValue();
            println(key + ": " + value);//println("Content-Type: text/html");
        }

        //单独发送回车+换行，表示响应头部分发送完毕
        println("");
    }
    //发送响应正文
    private void sendContent() throws IOException {
        //该流不用放在自动关闭特性中，因为ClientHandler处理一次HTTP交互最后会关闭socket
        OutputStream out = socket.getOutputStream();
        if(contentFile!=null) {
            try (
                    //不写catch,因为异常需要抛出，利用自动关闭特性，最终将流确保关闭
                    FileInputStream fis = new FileInputStream(contentFile);
            ) {
                int len;
                byte[] buf = new byte[1024 * 10];//10kb
                while ((len = fis.read(buf)) != -1) {
                    out.write(buf, 0, len);
                }
            }
        }
    }




    /**
     * 向客户端发送一行字符串
     * @param line
     */
    private void println(String line) throws IOException {
        OutputStream out = socket.getOutputStream();
        byte[] data = line.getBytes(StandardCharsets.ISO_8859_1);
        out.write(data);
        out.write(13);//单独发送回车符
        out.write(10);//单独发送换行符
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusReason() {
        return statusReason;
    }

    public void setStatusReason(String statusReason) {
        this.statusReason = statusReason;
    }

    public File getContentFile() {
        return contentFile;
    }

    public void setContentFile(File contentFile) {
        this.contentFile = contentFile;
        //设置正文文件的同时自动补充说明该正文的对应响应头信息
        addHeader("Content-Type",mftm.getContentType(contentFile));
        addHeader("Content-Length",String.valueOf(contentFile.length()));
    }

    /**
     * 添加一个要发送的响应头
     * @param name
     * @param value
     */
    public void addHeader(String name,String value){
        headers.put(name,value);
    }

    /**
     * 重定向到指定位置
     * @param location
     */
    public void sendRedirect(String location){
        //将状态代码设定为302
        setStatusCode(302);
        setStatusReason("Moved Temporarily");
        //添加响应头Location
        addHeader("Location",location);
    }

}

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
    private int statusCode;
    private String statusReason;

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

        //遍历headers，将所有的响应头发送给浏览器
        Set<Map.Entry<String,String>> entrySet = headers.entrySet();
        for (Map.Entry<String,String> e : entrySet){
            String key = e.getKey();
            String value = e.getValue();
            println(key + ": " + value);
        }


        println("");
    }
    //发送响应正文
    private void sendContent() throws IOException {

        OutputStream out = socket.getOutputStream();
        if(contentFile!=null) {
            try (
                    FileInputStream fis = new FileInputStream(contentFile)
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
        out.write(13);
        out.write(10);
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
        setStatusCode(302);
        setStatusReason("Moved Temporarily");
        addHeader("Location",location);
    }

}

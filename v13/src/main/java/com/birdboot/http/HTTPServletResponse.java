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
    private Socket socket;

    private int statusCode;
    private String statusReason;

    private Map<String, String> headers = new HashMap<>();
    private File contentFile;

    public HTTPServletResponse(Socket socket) {
        this.socket = socket;
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

    MimetypesFileTypeMap mftm = new MimetypesFileTypeMap();


    public void setContentFile(File contentFile) {
        this.contentFile = contentFile;
        addHeader("Content-Type", mftm.getContentType(contentFile));
        addHeader("Content-Type", String.valueOf(contentFile.length()));
    }

    /**
     * 添加一个需要的响应头
     *
     * @param name
     * @param value
     */
    public void addHeader(String name, String value) {
        headers.put(name, value);
    }

    /**
     * 将当前对象内容以标准的响应格式发送给客户端
     */
    public void response() throws IOException {
        //发送状态行
        sendStatusLine();
        //发送响应头
        sendHeaders();
        //发送响应正文
        sendContent();

        //单独发送回车+换行，表示响应头部分发送完毕


    }


    private void sendStatusLine() throws IOException {
        //发送状态行
        println("HTTP/1.1 " + statusCode + " " + statusReason);
    }

    private void sendHeaders() throws IOException {
        //发送响应头
//        println("Content-Type: text/html");
//        println("Content-Length: " + contentFile.length());
        Set<Map.Entry<String, String>> entrySet = headers.entrySet();
        for (Map.Entry<String, String> e : entrySet) {
            String key = e.getKey();
            String value = e.getValue();
            println(key + ": " + value);
        }
//单独回车换行
        println("");
    }

    private void sendContent() throws IOException {
        OutputStream out = socket.getOutputStream();
        if (contentFile != null) {
            try (
                    FileInputStream fis = new FileInputStream(contentFile);
            ) {


                int len;
                byte[] buf = new byte[1024 * 10];//10kb
                while ((len = fis.read(buf)) != -1) {
                    out.write(buf, 0, len);
                }
            }
        }
    }//自动关闭特性
    //finally{
    //     if(fis!=null){
    //         fis.close;
    //     }
    //}


    /**
     * 向客户发送一段字符串
     *
     * @param line
     */
    private void println(String line) throws IOException {
        OutputStream out = socket.getOutputStream();
        byte[] data = line.getBytes(StandardCharsets.ISO_8859_1);
        out.write(data);
        out.write(13);//单独发送回车符
        out.write(10);//单独发送换行符
    }

    /**
     * 重定向到指定位置
     *
     * @param location
     */
    public void sendRedirect(String location) {
        //将状态代码设为302
        setStatusCode(302);
        setStatusReason("Moved Temporarily");
        //添加响应头
        addHeader("Location", location);
    }

}

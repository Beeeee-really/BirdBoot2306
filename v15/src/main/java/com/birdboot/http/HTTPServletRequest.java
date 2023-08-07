package com.birdboot.http;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
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

    private String requestURI;//保存uri中的请求部分("?"左侧内容)
    private String queryString;//保存uri中的参数部分("?"右侧内容)
    private Map<String, String> parameters = new HashMap<>();//保存每一组参数

    //消息头相关信息
    private Map<String, String> headers = new HashMap<>();


    public HTTPServletRequest(Socket socket) throws IOException, EmptyRequestException {
        this.socket = socket;
        //读取请求行
        parseRequestLine();
        //读取消息头
        parseHeaders();
        //读取消息正文
        parseContent();

    }

    //解析请求行
    private void parseRequestLine() throws IOException, EmptyRequestException {
        String line = readLine();
        //请求行是否为空字符串，如果是，则说明本次为空请求
        if (line.isEmpty()) {
            throw new EmptyRequestException();
        }

        System.out.println("请求行:" + line);//GET /index.html HTTP/1.1

        String[] data = line.split("\\s");//按照空白字符拆分
        method = data[0];
        uri = data[1];
        protocol = data[2];

        parseURI();//进一步解析URI

        System.out.println("method:" + method);// GET
        System.out.println("uri:" + uri);// /index.html
        System.out.println("protocol:" + protocol);// HTTP/1.1
    }

    //进一步解析uri
    private void parseURI() {
        /*
            String requestURI
            String queryString
            Map parameters

            uri有两种情况:
            1:不含有参数的
              例如: /index.html
              直接将uri的值赋值给requestURI即可.

            2:含有参数的
              例如:/regUser?username=fancq&password=123456&nickname=chuanqi&age=22
              将uri中"?"左侧的请求部分赋值给requestURI
              将uri中"?"右侧的参数部分赋值给queryString
              将参数部分首先按照"&"拆分出每一组参数，再将每一组参数按照"="拆分为参数名与参数值
              并将参数名作为key，参数值作为value存入到parameters中。
                requestURI:/regUser
                queryString:username=&password=123456&nickname=chuanqi&age=22
                parameters:
                    key             value
                  username          ""
                  password          123456
                  nickname          chuanqi
                  age               22


           几种特殊情况
           1:表单中有空着不写的输入框
           例如:用户名不填
           /regUser?username=&password=123456&nickname=chuanqi&age=22

           2:表单中如果所有输入框没有指定name属性，此时表单提交则会忽略所有输入框
           /regUser?
         */
        String[] data = uri.split("\\?");
        requestURI = data[0];
        if (data.length > 1) {
            queryString = data[1];
            parseParameter(queryString);
        }
        System.out.println("requestURI:" + requestURI);
        System.out.println("queryString:" + queryString);
        System.out.println("parameters:" + parameters);
    }

    /**
     * 解析参数并存入parameters这个Map
     *
     * @param line 格式:name=value&name=value&...
     */
    private void parseParameter(String line) {
        //先拆分处每一组参数
        String[] paras = line.split("&");
        //paras:[username=, password=123456, nickname=chuanqi, age=22]
        for (String para : paras) {
            //para:username=
            //将每一组参数按照"="拆分出参数名和参数值
            String[] arr = para.split("=", 2);
            //arr:[username, ""]
            parameters.put(arr[0], arr[1]);
        }
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
            //消息头的名字以全小写形式存入，这样获取时只要将名字也全小写获取即可，可以达到忽略大小写目的
            headers.put(data[0].toLowerCase(), data[1]);
        }
        System.out.println("headers:" + headers);
    }

    //解析消息正文
    private void parseContent() throws IOException {
        //post请求会包含消息正文
        if ("post".equalsIgnoreCase(method)) {
            String lengthStr = getHeader("Content-Length");
            //如果存在Content-Length
            if (lengthStr != null) {
                //将正文长度转换为整数
                int contentLength = Integer.parseInt(lengthStr);
                //根据长度读取正文所有数据
                byte[] data = new byte[contentLength];
                InputStream in = socket.getInputStream();
                in.read(data);

                //根据Content-Type来对正文进行不同的解码处理
                String contentType = getHeader("Content-Type");
                //如果是form表单不含附件的提交类型
                if ("application/x-www-form-urlencoded".equals(contentType)) {
                    String line = new String(data, StandardCharsets.ISO_8859_1);
                    System.out.println("正文内容===========" + line);
                    parseParameter(line);
                }
                //后期还可扩展判断其他形式，比如含附件的表单提交
//                else if("xxxx/xxxxxx".equals(contentType)){
//
//                }


            }
        }
    }


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
        //根据消息头名字提取value时，统一将消息头名字全小写(因为存入headers时消息头名字全小写)
        return headers.get(name.toLowerCase());
    }

    public String getRequestURI() {
        return requestURI;
    }

    public String getQueryString() {
        return queryString;
    }

    public String getParameter(String name) {
        return parameters.get(name);
    }
}

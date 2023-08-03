package com.birdboot.core;

import com.birdboot.http.HTTPServletRequest;
import com.birdboot.http.HTTPServletResponse;

import java.io.File;
import java.net.URISyntaxException;

/**
 * 处理请求
 */
public class DispatcherServlet {

    private static File baseDir;
    private static File staticDir;

    static {
        //指定类加载路径
        try {
            baseDir = new File(
                    ClientHandler.class.getClassLoader().getResource(".").toURI()
            );
            //定位类加载路径下的static目录
            staticDir = new File(baseDir, "static");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void service(HTTPServletRequest request, HTTPServletResponse response) {


   /*
                当我们需要在项目中定位一个资源时，常用相对路径。
                而相对路径中"./"之前用过，在这里不太适用，因为后面还会迭代V6,V7,V8..
                如果要定位当前子项目中某个资源总是带着V5,V6那么每次都要跟着改
                比如:
                在V5时:
                File file = new File("./V5/src/main/resources/static/index.html");

                在V6时:
                File file = new File("./V6/src/main/resources/static/index.html");

                上述操作不理想，不能因为每次项目迭代代码跟着改动，这不是好的跨平台操作。


                实际开发中还有一个常用的相对路径:类加载路径
                类加载路径是以当前类作为依据，定位的是当前类所在包最上级包的上一层。
                例如:
                当前类ClientHandler,它所在的包:package com.birdboot.core;
                该类所在包的最上级:com

                而类加载路径则是com的上级目录:java目录(V5/src/main/java)
                实际上不是该目录，因为JVM在执行当前类时，并非执行ClientHandler.java文件
                而是执行ClientHandler.class文件。因此类加载路径应当是ClientHandler.class
                所在包最上级(com)的上级目录
                在IDEA里，MAVEN项目中所有的代码编译后，会放在target/classes目录中。因此在
                classes目录中也会包含:com/birdboot/core/ClientHandler.class。所以实际
                上类加载路径应当是:classes目录
                由于我们项目中所有的代码编译后都是放在target/classes中，因此定位到classes目录
                就可以顺着该目录找到项目中所有类。

                注意:
                在MAVEN项目中有一个要求:
                src/main下有俩个子目录
                java:要求存放所有.java文件和包
                resources:存放所有其他类型文件
                但是最终编译后，java和resources会合并保存到target/classes下。
                因此V5项目编译后，产生target/classes后，会发现该目录下有com目录(所有java代码)
                和static目录
                因此，定位类加载路径还可以访问到在resources下存放的所有非java文件


                定位类加载路径:
                File baseDir = new File(
                    当前类.class.getClassLoader().getResource(".").toURI()
                );
             */
        //测试将static目录下的index.html页面发送给浏览器
//            File file = new File("./V5/src/main/resources/static/index.html");
        //定位类加载路径

        String path = request.getUri();
        System.out.println("请求路径:" + path);

        //定位static目录中的index.html页面
        File file = new File(staticDir, path);


        if (file.isFile()) {
            response.setStatusCode(200);
            response.setStatusReason("OK");
            response.setContentFile(file);
        } else {
            response.setStatusCode(404);
            response.setStatusReason("NotFound");
            file = new File(staticDir, "Error.html");
            response.setContentFile(file);
        }

        //发送状态行

        //发送响应正文


//             方法2
        //3发送响应
//            /*
//                HTTP/1.1 200 OK(CRLF)
//                Content-Type: text/html(CRLF)
//                Content-Length: 2546(CRLF)
//                (CRLF)
//                1011101010101010101......
//             */
//                OutputStream out = socket.getOutputStream();
//                //发送状态行
//                String line = "HTTP/1.1 200 OK";
//                byte[] data = line.getBytes(StandardCharsets.ISO_8859_1);
//                out.write(data);
//                out.write(13);//单独发送回车符
//                out.write(10);//单独发送换行符
//                //发送响应头
//                line = "Content-Type: text/html";
//                data = line.getBytes(StandardCharsets.ISO_8859_1);
//                out.write(data);
//                out.write(13);//单独发送回车符
//                out.write(10);//单独发送换行符
//
//                line = "Content-Length: " + file.length();
//                data = line.getBytes(StandardCharsets.ISO_8859_1);
//                out.write(data);
//                out.write(13);//单独发送回车符
//                out.write(10);//单独发送换行符
//
//                //单独发送回车+换行，表示响应头部分发送完毕
//                out.write(13);//单独发送回车符
//                out.write(10);//单独发送换行符
//
//                //发送响应正文
//                FileInputStream fis = new FileInputStream(file);
//                int len;
//                byte[] buf = new byte[1024 * 10];//10kb
//                while ((len = fis.read(buf)) != -1) {
//                    out.write(buf, 0, len);
//                }
//            } else {
//                File file1 = new File(
//                        ClientHandler.class.getClassLoader().getResource(".").toURI()
//                );
//                //定位类加载路径下的static目录
//                File file2 = new File(file1, "static");
//                //定位static目录中的index.html页面
//                File file3 = new File(file2, "Error.html");
//                OutputStream out = socket.getOutputStream();
//                //发送状态行
//                String line = "HTTP/1.1 404 NotFound";
//                byte[] data = line.getBytes(StandardCharsets.ISO_8859_1);
//                out.write(data);
//                out.write(13);//单独发送回车符
//                out.write(10);//单独发送换行符
//                //发送响应头
//                line = "Content-Type: text/html";
//                data = line.getBytes(StandardCharsets.ISO_8859_1);
//                out.write(data);
//                out.write(13);//单独发送回车符
//                out.write(10);//单独发送换行符
//
//                line = "Content-Length: " + file3.length();
//                data = line.getBytes(StandardCharsets.ISO_8859_1);
//                out.write(data);
//                out.write(13);//单独发送回车符
//                out.write(10);//单独发送换行符
//
//                //单独发送回车+换行，表示响应头部分发送完毕
//                out.write(13);//单独发送回车符
//                out.write(10);//单独发送换行符
//
//                //发送响应正文
//                FileInputStream fis = new FileInputStream(file3);
//                int len;
//                byte[] buf = new byte[1024 * 10];//10kb
//                while ((len = fis.read(buf)) != -1) {
//                    out.write(buf, 0, len);
//                }
//            }
    }
}

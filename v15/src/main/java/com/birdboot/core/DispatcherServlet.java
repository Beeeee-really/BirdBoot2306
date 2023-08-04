package com.birdboot.core;

import com.birdboot.controller.ArticleController;
import com.birdboot.controller.UserController;
import com.birdboot.http.HTTPServletRequest;
import com.birdboot.http.HTTPServletResponse;

import javax.jws.soap.SOAPBinding;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * 处理请求
 */
public class DispatcherServlet {
    private static DispatcherServlet instance = new DispatcherServlet();
    private static File baseDir;//类加载路径
    private static File staticDir;//static目录

    static {
        try {
            //定位类加载路径
            baseDir = new File(
                    ClientHandler.class.getClassLoader().getResource(".").toURI()
            );
            //定位类加载路径下的static目录
            staticDir = new File(baseDir,"static");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private DispatcherServlet(){}

    public void service(HTTPServletRequest request, HTTPServletResponse response){
        //因为完整的抽象路径部分可能包含参数，因此不适用作为请求路径判定操作
//        String path = request.getUri();
        String path = request.getRequestURI();
        System.out.println("请求路径:"+path);

        //先查看是否为请求业务
        if("/regUser".equals(path)){
            UserController controller = new UserController();
            controller.reg(request, response);
        }else if("/loginUser".equals(path)){
            UserController controller = new UserController();
            controller.login(request, response);
        }else if("/writeArticle".equals(path)){
            ArticleController controller = new ArticleController();
            controller.writeArticle(request, response);
        }else {
            //定位static目录中的页面
            File file = new File(staticDir, path);
            if (file.isFile()) {
                response.setStatusCode(200);
                response.setStatusReason("OK");
                response.addHeader("Server", "BirdWebServer");
                response.setContentFile(file);
            } else {
                response.setStatusCode(404);
                response.setStatusReason("NotFound");
                file = new File(staticDir, "error.html");
                response.addHeader("Server", "BirdWebServer");
                response.setContentFile(file);
            }
        }
    }

    public static DispatcherServlet getInstance(){
        return instance;
    }
}

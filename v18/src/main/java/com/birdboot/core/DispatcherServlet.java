package com.birdboot.core;

import com.birdboot.annotation.Controller;
import com.birdboot.annotation.RequestMapping;
import com.birdboot.controller.ArticleController;
import com.birdboot.controller.UserController;
import com.birdboot.http.HTTPServletRequest;
import com.birdboot.http.HTTPServletResponse;

import javax.jws.soap.SOAPBinding;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
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
            staticDir = new File(baseDir, "static");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private DispatcherServlet() {
    }

    public void service(HTTPServletRequest request, HTTPServletResponse response) {
        //因为完整的抽象路径部分可能包含参数，因此不适用作为请求路径判定操作
//        String path = request.getUri();
        String path = request.getRequestURI();
        System.out.println("请求路径:" + path);

        //先查看是否为请求业务

        //定位static目录中的页面


        try {
            //path:/regUser

            File dir = new File(
                    DispatcherServlet.class.getClassLoader().getResource(".").toURI()
            );
            File controllerDir = new File(dir,"com/birdboot/controller");
            //获取controller包中的所有class文件
            File[] subs = controllerDir.listFiles(f->f.getName().endsWith(".class"));
            //遍历每一个class文件
            for(File sub : subs){
                //根据文件名获取类名
                String className = sub.getName().replace(".class","");
                //加载该类的类对象
                Class cls = Class.forName("com.birdboot.controller."+className);
                //判断该类是否被注解@Controller标注
                if(cls.isAnnotationPresent(Controller.class)) {
                    //根据类对象获取其中定义的所有方法
                    Method[] methods = cls.getDeclaredMethods();
                    //遍历每一个方法
                    for (Method method : methods) {
                        //判断该方法上是否存在@RequestMapping注解
                        if(method.isAnnotationPresent(RequestMapping.class)){
                            //获取该方法上的@RequestMapping注解，目的是获取对应的参数和本次请求做对比
                            RequestMapping rm = method.getAnnotation(RequestMapping.class);
                            String value = rm.value();//获取参数值
                            if(value.equals(path)){//该注解参数值和本次请求路径若一致，则说明用该方法处理
                                //通过类对象实例化这个Controller
                                Object obj = cls.newInstance();
                                //调用该方法处理
                                method.invoke(obj,request,response);//controller.reg(request,response);
                                return;
                            }
                        }
                    }
                }
            }





        } catch (Exception e) {
            //处理业务时出现异常是典型的500问题
        }


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
            file = new File(staticDir, "404.html");
            response.addHeader("Server", "BirdWebServer");
            response.setContentFile(file);
        }
    }


    public static DispatcherServlet getInstance() {
        return instance;
    }
}

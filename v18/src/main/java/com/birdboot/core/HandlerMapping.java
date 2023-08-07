package com.birdboot.core;

import com.birdboot.annotation.Controller;
import com.birdboot.annotation.RequestMapping;

import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 使用当前类维护请求路径和对应Controller处理方法的关系
 */
public class HandlerMapping {
    private static Map<String, Method> mapping = new HashMap<>();

    static {
        initMapping();
    }

    private static void initMapping() {
        try {
            //path:/regUser

            File dir = new File(HandlerMapping.class.getClassLoader().getResource(".").toURI());
            File controllerDir = new File(dir, "com/birdboot/controller");
            //获取controller包中的所有class文件
            File[] subs = controllerDir.listFiles(f -> f.getName().endsWith(".class"));
            //遍历每一个class文件
            for (File sub : subs) {
                //根据文件名获取类名
                String className = sub.getName().replace(".class", "");
                //加载该类的类对象
                Class cls = Class.forName("com.birdboot.controller." + className);
                //判断该类是否被注解@Controller标注
                if (cls.isAnnotationPresent(Controller.class)) {
                    //根据类对象获取其中定义的所有方法
                    Method[] methods = cls.getDeclaredMethods();
                    //遍历每一个方法
                    for (Method method : methods) {
                        //判断该方法上是否存在@RequestMapping注解
                        if (method.isAnnotationPresent(RequestMapping.class)) {
                            //获取该方法上的@RequestMapping注解，目的是获取对应的参数和本次请求做对比
                            RequestMapping rm = method.getAnnotation(RequestMapping.class);
                            String value = rm.value();//获取参数值
                            //将注解参数值作为key，将该方法对象作为value存入Map
                            mapping.put(value, method);
                        }
                    }
                }
            }


        } catch (Exception e) {
            //处理业务时出现异常是典型的500问题
        }
    }
}

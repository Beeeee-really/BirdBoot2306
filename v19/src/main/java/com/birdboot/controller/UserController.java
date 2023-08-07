package com.birdboot.controller;

import com.birdboot.annotation.Controller;
import com.birdboot.annotation.RequestMapping;
import com.birdboot.entity.User;
import com.birdboot.http.HTTPServletRequest;
import com.birdboot.http.HTTPServletResponse;

import java.io.*;

/**
 * 处理与用户相关的业务
 */
@Controller
public class UserController {
    private static File userDir;

    static {
        userDir = new File("./users");
        if (!userDir.exists()) {
            userDir.mkdirs();
        }
    }

    //@RequestMapping("/regUser")
    @RequestMapping("/signupUser")
    public void reg(HTTPServletRequest request, HTTPServletResponse response) {
        System.out.println("开始处理用户注册");
        //1
        String username = request.getParameter("username");//参数要与页面表单中对应输入框name属性的值一致
        String password = request.getParameter("password");
        String nickname = request.getParameter("nickname");
        String ageStr = request.getParameter("age");
        System.out.println(username + "," + password + "," + nickname + "," + ageStr);

        if (username == null || username.isEmpty() ||
                password == null || password.isEmpty() ||
                nickname == null || nickname.isEmpty() ||
                ageStr == null || ageStr.isEmpty() ||
                !ageStr.matches("[0-9]+")) {

            response.sendRedirect("/signup_info_error.html");
            return;
        }

        int age = Integer.parseInt(ageStr);
        User user = new User(username, password, nickname, age);

        File file = new File(userDir, username + ".obj");
        if (file.exists()) {//文件若存在，说明为重复用户
            response.sendRedirect("/have_user.html");
            return;
        }

        try (
                FileOutputStream fos = new FileOutputStream(file);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
        ) {
            oos.writeObject(user);
            response.sendRedirect("/signup_success.html");
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @RequestMapping("/loginUser")
    public void login(HTTPServletRequest request, HTTPServletResponse response) {
        System.out.println("开始处理用户登录!!!!");

        //1获取表单信息
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        //必要的验证工作
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            response.sendRedirect("/login_info_error.html");
            return;
        }

        //2
        //读取该登录用户曾经注册时的信息
        File file = new File(userDir, username + ".obj");
        if (file.exists()) {//文件存在，用户名有效
            try (
                    FileInputStream fis = new FileInputStream(file);
                    ObjectInputStream ois = new ObjectInputStream(fis);
            ) {
                User user = (User) ois.readObject();//反序列化注册信息
                //比较密码是否一致
                if (user.getPassword().equals(password)) {
                    //密码一致登录成功
                    response.sendRedirect("/login_success.html");
                } else {
                    //不一致登录失败
                    response.sendRedirect("/login_fail.html");
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            //没有此用户，登录失败
            response.sendRedirect("/login_fail.html");
        }


    }


}

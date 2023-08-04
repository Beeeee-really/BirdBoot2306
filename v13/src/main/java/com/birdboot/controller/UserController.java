package com.birdboot.controller;

import com.birdboot.entity.User;
import com.birdboot.http.HTTPServletRequest;
import com.birdboot.http.HTTPServletResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * 处理与用户相关的业务
 */
public class UserController {
    private static File userDir;

    static {
        userDir = new File("./users");
        if (!userDir.exists()) {
            userDir.mkdirs();
        }
    }

    public void signup(HTTPServletRequest request, HTTPServletResponse response) {
        System.out.println("处理注册...");
        String username = request.getParameter("username");
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
            //数据不正确，响应错误页面
        }
        int age = Integer.parseInt(ageStr);
        if (!(age > 0)) {

            response.sendRedirect("/sign_info_error.html");

            return;
        }
        User user = new User(username, password, nickname, age);

        File file = new File(userDir, username + ".obj");

        if (file.exists()) {
            response.sendRedirect("/have_user.html");
            return;
        }
        try (
                FileOutputStream fos = new FileOutputStream(file);
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(user);
            response.sendRedirect("/signup_success.html");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package com.birdboot.controller;

import com.birdboot.entity.Article;

import com.birdboot.http.HTTPServletRequest;
import com.birdboot.http.HTTPServletResponse;


import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class ArticleController {

    private static File articleDir;

    static {
        articleDir = new File("articles");
        if (!articleDir.exists()) {
            articleDir.mkdirs();
        }
    }

    public void writeArticle(HTTPServletRequest request, HTTPServletResponse response) {
        //1获取表单数据
        String title = request.getParameter("title");
        String author = request.getParameter("author");
        String content = request.getParameter("content");
        System.out.println(title + "," + author + "," + content);
        if (title == null || title.isEmpty() ||
                author == null || author.isEmpty() ||
                content == null || content.isEmpty()) {
            response.sendRedirect("/article_fail.html");
            return;
        }

        //将文章保存
        File articleFile = new File(articleDir, title + ".obj");
        if (articleFile.exists()) {
            response.sendRedirect("/article_fail.html");
            return;
        }

        Article article = new Article(title, author, content);

        try (
                FileOutputStream fos = new FileOutputStream(articleFile);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
        ) {
            oos.writeObject(article);

            response.sendRedirect("/article_success.html");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}

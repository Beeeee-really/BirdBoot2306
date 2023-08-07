package com.birdboot.test;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;

/**
 * 解析资源文件对应的Content-Type值
 */
public class ContentTypeDemo {
    public static void main(String[] args) {
        MimetypesFileTypeMap mftm = new MimetypesFileTypeMap();
        File file = new File("abc.gif");

        String contenType = mftm.getContentType(file);
        System.out.println(contenType);
    }
}

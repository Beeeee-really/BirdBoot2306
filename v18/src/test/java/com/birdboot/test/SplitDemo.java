package com.birdboot.test;

import java.util.Arrays;

/**
 * String提供了一个重载的Spilt方法
 */
public class SplitDemo {
    public static void main(String[] args) {
        String line = "1=2=3=4=5=6=7=8=9================================================================";
        String[] data = line.split("=");
        System.out.println(Arrays.toString(data));//仅拆分出两项
        //如果大于最多可拆分项时，保留最多项

        data = line.split("=", 0);//与原先的效果相同
        System.out.println(data);

        data = line.split("=", 2);
        System.out.println(Arrays.toString(data));

        data = line.split("=", -1);//小于零，应拆尽拆
        System.out.println(data);


    }
}

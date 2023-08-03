package com.birdboot.test;

/**
 * <p>单例测试
 * <p>使用举例模式设计的类，全局只有一个实例
 * <p>
 * <p>单例模式实现步骤:
 * <p>1:私有化构造器
 * <p>2:定义私有的静态方法
 * <p>3:...
 */
public class Singleton {
    private static Singleton instonce = new Singleton();

    private Singleton() {

    }

    public static Singleton getInstance() {
        return instonce;
    }
}

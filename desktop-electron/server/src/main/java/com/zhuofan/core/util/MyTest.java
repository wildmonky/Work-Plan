package com.zhuofan.core.util;

public class MyTest {

    public void Test1(int x){
        double y=1.5+0.5*x;
        System.out.println(String.format("第%d天吃%s肉",x,y));

    }

    public  static void main(String[] args){
        MyTest my=new MyTest();
        my.Test1(10);
        my.Test1(100);
        my.Test1(1000);
        my.Test1(10000);
    }
}

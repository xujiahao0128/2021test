package com.wangzaiplus.test.demoProxy;

import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: MyTest
 * @description: TODO
 * @author: Mr.Xu
 * @time: 2021/1/7 14:00
 */
public class MyTest {

    @Test
    public void Test1(){
        String str="徐家豪 This is my code !";
        //fromIndex:8，相当于截取下标0~8的字符串再开始查找对应字符最后一次出现的位置，没有返回-1
        System.out.println("当前字符串最后一次出现的下标："+str.lastIndexOf("i",20));
        System.out.println("当前字符串第一次出现的下标："+str.indexOf("i"));
        //从下标3开始截取字符串#包头
        System.out.println("截取字符："+str.substring(3));
        //substring包头不包尾
        System.out.println("截取字符："+str.substring(3,19));
        System.out.println("字符串的长度（会算空格）："+str.length());
        System.out.println("替换字符串中出现的字符："+str.replace('!','?'));
        System.out.println("分割字符串：" + Arrays.stream(str.split("i")).collect(Collectors.joining()));
        System.out.println("字符串是否包含字符："+str.contains("i"));
        System.out.println("字符串是否为空："+str.isEmpty());
    }

    @Test
    public void TestFile() throws IOException {
        String path="C:\\Users\\00\\Desktop\\springboot-wxw\\test.txt";
        String str="happy new year！新年快乐！";
        File file = new File(path);
        System.out.println(file.listFiles());
        System.out.println("Name:"+file.getName()+"\nAbsolutePath:"+file.getAbsolutePath()
                +"\nCanonicalPath:"+file.getCanonicalPath()+"\nPath:"+file.getPath()
                +"\nParent:"+file.getParent()+"\nexists:"+file.exists()
                +"\nDirectory:"+file.isDirectory()+"\nFile:"+file.isFile()
                +"\nAbsolute:"+file.isAbsolute()+"\nHidden:"+file.isHidden());
        OutputStream fos = new FileOutputStream(file);
        byte [] bytes=str.getBytes();
        fos.write(bytes);
        fos.flush();
        fos.close();

        InputStream fis=new FileInputStream(file);
        System.out.println("available:"+fis.available());
        byte[] bt = new byte[1024];
        int length=0;
        StringBuilder sb = new StringBuilder();
        while((length=fis.read(bt))!=-1){
            sb.append(new String(bt,0,length,"utf-8"));
        }
        System.out.println(sb.toString());
        fis.close();


        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(path));
        byte [] bt2="Hello Word !".getBytes();
        bos.write(bt2);
        bos.flush();
        bos.close();


        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(path));
        System.out.println("available:"+bis.available());
        byte [] bt3=new byte[1024];
        int length1=0;
        while((length1=bis.read(bt3))!=-1){
        System.out.println(new String(bt3,0,length1,"utf-8"));
        }
        bis.close();
    }

    @Test
    public void Test2(){
        String str1="aaa,bbb,ccc";
        String str2="aaa,bbb,ccc,";
        String [] arr1=str1.split(",");
        String [] arr2=str2.split(",");
    System.out.println("arr1:"+arr1.length);
        System.out.println("arr2:"+arr2.length);
        int code=(int)((Math.random() * 9 + 1) * 1000000000);
    System.out.println(code);
        Map<String,Object> list=new HashMap<>();
        Map<String,String> list2=new HashMap<>();
        list2.put("1","aaa");
        list2.put("2","bbb");

        list.put("1","aaa");
        list.put("2","bbb");
        list.put("3",list2);
    System.out.println(new JSONObject(list));
    System.out.println(JSONObject.toJSON(list));
    System.out.println(System.currentTimeMillis());
    }

    @Test
    public void Test3(){
        Integer i=1;
        Integer j = new Integer(1);
        int x=1;
        System.out.println(i==j);
        System.out.println(i.equals(j));
        System.out.println(i==x);
        System.out.println(x==j);
        Integer y=128;
        Integer z = new Integer(128);
        int w=128;
        System.out.println(y==z);
        System.out.println(y.equals(z));
        System.out.println(y==w);
        System.out.println(w==z);
    }

}

package com.wangzaiplus.test.study.juc;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import lombok.Data;
import org.junit.jupiter.api.Test;
import org.openjdk.jol.info.ClassLayout;
import org.openjdk.jol.vm.VM;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Mr.Xu
 * @Description TODO
 * @date 2022年07月08日 10:57
 */
public class JolTest {

    @Test
    public void testJol(){
        String dateStr = DateUtil.format(DateUtil.offsetMonth(new Date(), 3),DatePattern.NORM_MONTH_PATTERN) + "-01 00:00:00";
        DateTime parse = DateUtil.parse(dateStr, DatePattern.NORM_DATETIME_PATTERN);
        System.out.println(
        "DateUtil.offsetMonth(new Data(),3) = " + parse);

        System.out.println(
        "DateUtil.parse(\"2022-01-01 11:12:00\",\"yyyy-MM-dd HH:mm\").toDateStr() = "
            + DateUtil.format(DateUtil.parse("2022-01-01 11:12:00"),"yyyy-MM-dd HH:mm"));

        System.out.println("VM.current().details() = " + VM.current().details());

        System.out.println("===================");

        System.out.println("VM.current().objectAlignment() = " + VM.current().objectAlignment());

        System.out.println("===================");
        Object o = new Object();
        Customer customer = new Customer();
        customer = null;
    System.out.println(customer.getName());
        System.out.println(
            "ClassLayout.parseInstance(o).toPrintable() = "
            + ClassLayout.parseInstance(customer).toPrintable());

    }

    /**
     * 方法描述：方法描述：获取节假日 访问接口，根据返回值判断当前日期是否为工作日， 返回结果：检查具体日期是否为节假日，工作日对应结果为 0,
     * 休息日对应结果为 1, 节假日对应的结果为 2； 注意：传入的时间格式为2020-06-25或者20200625
     */
    @Test
    public void test(){
        String dc = "http://tool.bitefu.net/jiari/?d=";
        String httpUrl = new StringBuffer().append(dc).append("2022-10-04").toString();
        BufferedReader reader = null;
        String result = null;
        StringBuffer sbf = new StringBuffer();
        try {
            URL url = new URL(httpUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            InputStream is = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String strRead = null;
            while ((strRead = reader.readLine()) != null) {
                sbf.append(strRead);
            }
            reader.close();
            result = sbf.toString();
        } catch (Exception e) {

        }
        System.out.println("result = " + result);
    }

  public static void main(String[] args) {
    double a = 62d;
    int b = (int) (a / 5);
    System.out.println("b = " + b);
    double c = Double.valueOf(String.format("%.2f", a / 5));
    System.out.println("c = " + c);
    boolean flag =  (c - b) > 0.5d;
    System.out.println("c - b = " + flag);
    String str = b + ".5";
    System.out.println("str = " + str);
  }
}

@Data
class Customer{
    int id;
    boolean flag;
    String name;
    String name2;
}
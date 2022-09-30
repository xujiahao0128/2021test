package com.wangzaiplus.test.util;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Lists;
import com.wangzaiplus.test.pojo.Content;
import com.wangzaiplus.test.pojo.juejin.BugDTO;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

/**
 * @author Mr.Xu
 * @Description TODO
 * @date 2022年03月24日 17:41
 */
public class test {

  public static void main(String[] args) {
      Scanner scan = new Scanner(System.in);
      int sum = scan.nextInt();
      int number[] = { 0, 0, 0, 0 };
      for (int i = 0; i < 4; i++) {
          number[i] = scan.nextInt();
      }
      Solution solution = new Solution();
      boolean b = solution.judgePoint24(number,sum);

  }


  @Test
  public void test(){
      LocalDateTime localDateTime = LocalDateTimeUtil.now();

      DateTime dateTime =DateTime.now();
      String format1 = LocalDateTimeUtil.format(LocalDate.parse(dateTime.toDateStr()), DatePattern.UTC_MS_PATTERN);
      String format2 = LocalDateTimeUtil.format(LocalDate.parse(dateTime.offset(DateField.DAY_OF_MONTH,-2).toDateStr()), DatePattern.UTC_MS_PATTERN);
      System.out.println(format1);
      System.out.println(format2);
      String format = LocalDateTimeUtil.format(localDateTime, DatePattern.UTC_MS_PATTERN);
      System.out.println(format);
  }

    @Test
    public void testThreadLocal(){
        ThreadLocal threadLocal = new ThreadLocal(){
            @Override
            protected Object initialValue() {
                return Boolean.FALSE;
            }
        };

        Object initValue = threadLocal.get();
        System.out.println(initValue);

        threadLocal.set("aa");
        Object o = threadLocal.get();
        System.out.println(o);

        ThreadLocal<String> stringThreadLocal = new ThreadLocal<>();
        String s = stringThreadLocal.get();
        stringThreadLocal.set("1");
        String s1 = stringThreadLocal.get();

        threadLocal.set("bb");
        Object o1 = threadLocal.get();
        System.out.println(o1);

        threadLocal.remove();
        System.out.println(threadLocal.get());
    }

    @Test
    public void testList(){
    System.out.println(Math.max(1, 2));
        Lists.newArrayList("","","");
        int i = 20;
        int j = i >> 1;
        System.out.println(j);
        System.out.println(16 >> 1);
        System.out.println(3 >> 1);
        ArrayList list = new ArrayList(3);
      list.add("1");
        list.add("1");
        list.add("1");
        list.add("1");
      list.get(0);
      list.remove(0);

    }

    @Test
    public void testBigDecimal(){
        BigDecimal bigDecimal1 = new BigDecimal("12.56");
        BigDecimal bigDecimal2 = new BigDecimal("13.22");
    System.out.println("bigDecimal1.subtract(bigDecimal2) = " + bigDecimal1.subtract(bigDecimal2));
    }


}

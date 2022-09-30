package com.wangzaiplus.test.util;

/**
 * @author Mr.Xu
 * @Description TODO
 * @date 2022年04月07日 17:35
 */
import java.util.Scanner;

public class Point24 {

    boolean used[] = { false, false, false, false };
    float nowNumber[] = { 0, 0, 0, 0 };
    float number[] = { 0, 0, 0, 0 };
    int[] ops = { 0, 0, 0 };
    int opType[] = { 1, 2, 3, 4, 5, 6 };

    public void getInput(Scanner scan) {
        for (int i = 0; i < 4; i++) {
            number[i] = scan.nextFloat();
        }
    }

    public boolean makeNumber(int depth) {
        if (depth >= 4) {
            // 此时已经枚举完四个数了，
            // 开始枚举运算符
            return makeOperation(0);
        }

        for (int i = 0; i < 4; i++) {
            if (!used[i]) {
                nowNumber[depth] = number[i];
                used[i] = true;
                if (makeNumber(depth + 1)) {
                    return true;
                }
                used[i] = false;
            }
        }

        return false;
    }

    public boolean makeOperation(int depth) {
        if (depth >= 3) {
            // 此时已经枚举了四个数和三个运算符
            // 计算第一种模式下的值
            if (calcType1() == 24.0) {
                return true;
            }
            if (calcType2() == 24.0) {
                return true;
            }
            return false;
        }

        for (int i = 0; i < 6; i++) {
            ops[depth] = opType[i];
            if (makeOperation(depth + 1))
                return true;
        }

        return false;
    }

    public float calculate(float a, float b, int type) {

        switch (type) {
            case 1:
                return (a + b);
            case 2:
                return (a - b);
            case 3:
                return (a * b);
            case 4:
                if (b == 0)
                    return 0;
                return (a / b);
            case 5:
                return (b - a);
            case 6:
                if (a == 0)
                    return 0;
                return (b / a);
        }

        return 0;
    }

    public float calcType1() {
        float result = nowNumber[0];

        result = calculate(result, nowNumber[1], ops[0]);
        result = calculate(result, nowNumber[2], ops[1]);
        result = calculate(result, nowNumber[3], ops[2]);

        return result;
    }

    public float calcType2() {
        float result1 = nowNumber[0];
        float result2 = nowNumber[2];

        result1 = calculate(result1, nowNumber[1], ops[0]);
        result2 = calculate(result2, nowNumber[3], ops[2]);
        result1 = calculate(result1, result2, ops[1]);

        return result1;
    }

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        int n = scan.nextInt();
        String result[] = new String[n];
        Point24 point;
        for (int i = 0; i < n; i++) {
            point = new Point24();
            point.getInput(scan);
            result[i] = point.makeNumber(0) ? "Yes" : "No";
        }

        for (int i = 0; i < n; i++) {
            System.out.println(result[i]);
        }
    }

}
package com.wangzaiplus.test.demoProxy;

import lombok.Data;

/**
 * @ClassName: Speak
 * @description: TODO
 * @author: Mr.Xu
 * @time: 2020/12/23 15:10
 */
@Data
public class Speak implements Court{

    public void spe(String name){
    System.out.println(name+"说: 这不是我干的！");
    }

    @Override
    public int doCourt(int a) {
        return 0;
    }

    @Override
    public int speak() {
        return 0;
    }
}

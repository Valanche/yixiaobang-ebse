package com.weixin.njuteam.util;


import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class AccurateTest {

    @Test
    public void test1(){
        System.out.println(Arrays.toString(AccurateBasicUtil.accurateBasic("src/main/resources/images/R2.jpg")));
    }
}

package com.owl.ob;


import org.junit.Test;

import java.util.Date;

/**
 * @author by 15515
 * @Classname JunitTest
 * @Description TODO
 * @Date 2021/4/24 22:14
 **/
public class JunitTest {
    @Test
    public void testEqual() {
        Object s = new String("hello");
        Date date = (Date) s;
    }
}

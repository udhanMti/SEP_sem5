package com.example.udhan.voice3;

import org.junit.Test;

import static org.junit.Assert.*;

public class MainActivityTest {


    @Test
    public void saveAccount() {

        String address="abc@gmail.com";
        String pw="12345678";
        boolean expected=false;
        MainActivity main_act=new MainActivity();
        boolean output=main_act.SaveAccount(address,pw);
        assertEquals(expected,output);
    }
}
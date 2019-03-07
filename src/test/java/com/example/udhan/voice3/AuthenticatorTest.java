package com.example.udhan.voice3;

import org.junit.Test;

import java.util.concurrent.ExecutionException;

import static org.junit.Assert.*;

public class AuthenticatorTest {

    @Test
    public void doInBackground() {
        String address="abc@gmail.com";
        String pw="12345678";
        int expected=1;
        Authenticator a=new Authenticator();
        int output= 0;
        try {
            output = a.execute(address,pw).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        assertEquals(expected,output);
    }
}
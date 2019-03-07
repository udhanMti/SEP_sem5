package com.example.udhan.voice3;

import android.support.v7.app.AppCompatActivity;

public class Config extends AppCompatActivity {
    public static final String EMAIL =MainActivity.myPreferences.getString("email",null);
    public static final String PASSWORD =MainActivity.myPreferences.getString("password",null);
}
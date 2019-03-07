package com.example.udhan.voice3;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.speech.tts.TextToSpeech;
import android.widget.TextView;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;
import javax.mail.Store;
import javax.mail.Session;
import javax.mail.Transport;


public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

     private LinearLayout LayoutMain;
     private TextToSpeech textToSpeech;
     private boolean isRegistered=false;
     private int currentStep=0;
     static SharedPreferences myPreferences;
     private int TapCount=-1;
     private int pinNums[]={-1,-1,-1,-1};
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Login");

        textToSpeech = new TextToSpeech(this, this);
        LayoutMain=findViewById(R.id.mainlayout);

        final TextView textView1=findViewById(R.id.email);
        textView1.setVisibility(View.INVISIBLE);

        final EditText emailText=findViewById(R.id.emailEntry);
        emailText.setVisibility(View.INVISIBLE);

        final TextView textView2=findViewById(R.id.password);
        textView2.setVisibility(View.INVISIBLE);

        final EditText passwordText= findViewById(R.id.passwordEntry);
        passwordText.setVisibility(View.INVISIBLE);

        myPreferences = this.getSharedPreferences("com.example.udhan.voice3", Context.MODE_PRIVATE);

       // myPreferences.edit().clear().apply();
        isRegistered = myPreferences.getBoolean("isReg", false);


        //show the textboxes to enter email & password if not registered
        if(!isRegistered){
             textView1.setVisibility(View.VISIBLE);
             emailText.setVisibility(View.VISIBLE);
             textView2.setVisibility(View.VISIBLE);
             passwordText.setVisibility(View.VISIBLE);
        }else{
            currentStep=1;
        }

        final GestureDetector mDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener(){

            public boolean onDown(MotionEvent e) {
                TapCount+=1;
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                boolean result = false;
                try {
                    float diffY = e2.getY() - e1.getY();
                    float diffX = e2.getX() - e1.getX();
                    if (Math.abs(diffX) > Math.abs(diffY)) {
                        if (Math.abs(diffX) > 100 && Math.abs(velocityX) > 100) {
                            if (diffX > 0) {
                                if(currentStep==0){
                                     String email=emailText.getText().toString();
                                     String pw=passwordText.getText().toString();
                                     if(email.equals("")|| pw.equals("")){
                                         speakOut("Inputs cannot be empty");
                                     }else{
                                         boolean isValid=SaveAccount(email,pw);
                                         if(isValid) {
                                             currentStep=1;
                                             textView1.setVisibility(View.INVISIBLE);
                                             emailText.setVisibility(View.INVISIBLE);
                                             textView2.setVisibility(View.INVISIBLE);
                                             passwordText.setVisibility(View.INVISIBLE);
                                             TapCount = -1;
                                             speakOut("Email account linked successfully. Enter the pin code by tapping each digit and swiping right.\n\n digit 1.");
                                         }else{
                                             speakOut("Linking email account failed. please try again.");
                                         }
                                     }

                                }else if(currentStep>=1){
                                    if(isRegistered){
                                        pinNums[currentStep-1]=TapCount;
                                        if(currentStep==4){
                                            CheckLogin();
                                            currentStep=1;
                                        }else{ currentStep+=1;speakOut("digit "+currentStep);}
                                    }else{
                                        if(TapCount>=1) {
                                           pinNums[currentStep-1]=TapCount;
                                           if(currentStep==4){
                                               SaveLogin();
                                               currentStep=1;
                                           }else{
                                               currentStep+=1;
                                               speakOut("digit "+currentStep);
                                           }
                                        }
                                        else{speakOut("Digit cannot be zero"); }
                                    }

                                    TapCount=-1;
                                }
                            } else {
                               finish();
                            }
                            result = true;
                        }
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                return result;
            }

        });

        LayoutMain.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                return mDetector.onTouchEvent(event);
            }
        });

    }

    //Check the given email account and linked to app if it is verified
    public boolean SaveAccount(String address,String pw){

        try {
            int i=new Authenticator().execute(address,pw).get();//checkForErrors(address,pw);
            if(i==0){
                myPreferences.edit().putString("email",address).apply();
                myPreferences.edit().putString("password",pw).apply();
                return  true;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }

    //Check input pincode
    public void CheckLogin(){
         int pin1=myPreferences.getInt("pin1",-2);
         int pin2=myPreferences.getInt("pin2",-2);
         int pin3=myPreferences.getInt("pin3",-2);
         int pin4=myPreferences.getInt("pin4",-2);
         if(pin1==pinNums[0] && pin2==pinNums[1] && pin3==pinNums[2] && pin4==pinNums[3] ){
             Intent intent = new Intent(getApplicationContext(),Home.class);
             startActivity(intent);
         }else{
             speakOut("Incorrect pin code. Please Try again. digit 1.");
         }
    }

    //Save new pin code
    public void SaveLogin(){
        myPreferences.edit().putInt("pin1",pinNums[0]).apply();
        myPreferences.edit().putInt("pin2",pinNums[1]).apply();
        myPreferences.edit().putInt("pin3",pinNums[2]).apply();
        myPreferences.edit().putInt("pin4",pinNums[3]).apply();
        isRegistered=true;
        myPreferences.edit().putBoolean("isReg",true).apply();
        speakOut("Pin code saved. Please login again. digit 1.");

    }
    public void speakOut(String s) {
        textToSpeech.speak(s, TextToSpeech.QUEUE_FLUSH, null);
    }

    protected void onResume(){
        super.onResume();
    }

    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
            textToSpeech = null;
        }
        super.onDestroy();
    }
    public void onInit(int i) {

         if(!isRegistered){
             speakOut("Hi, Speak Mail. You are not registered. Please link your email account. Then swipe left.");
         }else{
             TapCount=-1;
             speakOut("Hi, Speak Mail. Please log in. Tap the pin code and swipe left.\n digit 1.");}
    }
}

//Class to validate entered email account and password
class Authenticator extends AsyncTask<String, Void, Integer> {

    @Override
    protected Integer doInBackground(String... strings) {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.socketFactory.port", "465");
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.port", "465");
            Session mailSession = Session.getInstance(props, null);

            Transport transport = mailSession.getTransport("smtp");

            transport.connect("smtp.gmail.com",
                    465, strings[0], strings[1]);
            transport.close();
            return 0;
        } catch (AuthenticationFailedException e) {
            e.printStackTrace();
            return 1;
        } catch (MessagingException e) {
            e.printStackTrace();
            return 2;
        }catch (Exception e){
            e.printStackTrace();
            Log.i("email",e.toString());
            return 3;
        }
    }
}
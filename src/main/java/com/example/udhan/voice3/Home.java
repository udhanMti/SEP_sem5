package com.example.udhan.voice3;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.speech.tts.TextToSpeech;

public class Home extends AppCompatActivity implements TextToSpeech.OnInitListener {
    private RelativeLayout LayoutHome;
    private TextToSpeech textToSpeech;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setTitle("Home");

        textToSpeech = new TextToSpeech(this, this);
        LayoutHome=(RelativeLayout)findViewById(R.id.homelayout);

        final GestureDetector mDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener(){

            //direct to send mail page when single tapped
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                Intent i = new Intent(getApplicationContext(),SendMail.class);

                textToSpeech.stop();
                startActivity(i);


                return true;
            }

            //direct to read mail page when double tapped
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                Intent i = new Intent(getApplicationContext(),ReadMail.class);
                textToSpeech.stop();
                startActivity(i);
                return true;
            }

            //give instructions again when long pressed
            @Override
            public void onLongPress(MotionEvent e) {
                super.onLongPress(e);
                getInstructions();
            }

            @Override
            public boolean onDoubleTapEvent(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            //go back when swipe left
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                boolean result = false;
                try {
                    float diffY = e2.getY() - e1.getY();
                    float diffX = e2.getX() - e1.getX();
                    if (Math.abs(diffX) > Math.abs(diffY)) {
                        if (Math.abs(diffX) > 100 && Math.abs(velocityX) > 100) {
                            if (diffX < 0) {
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


        LayoutHome.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                return mDetector.onTouchEvent(event);
            }
        });

    }

    public void speakOut(String s) {
        textToSpeech.speak(s, TextToSpeech.QUEUE_ADD, null);
    }

    protected void onResume(){
        super.onResume();
        speakOut("Back to Home Page");
    }

    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
            textToSpeech = null;
        }
        super.onDestroy();
    }
    public void getInstructions(){
        speakOut("Single Tap to send emails");
        speakOut("Double Tap to read new emails");
        speakOut("Swipe left to go back anytime");

    }

    @Override
    public void onInit(int i) {
        speakOut("Login successful. Here's the home page");
        getInstructions();
        speakOut("Long Press to get instructions again");
    }
}
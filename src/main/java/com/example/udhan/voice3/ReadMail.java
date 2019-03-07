package com.example.udhan.voice3;


import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.speech.tts.TextToSpeech;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.ArrayList;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;


public class ReadMail extends AppCompatActivity implements TextToSpeech.OnInitListener,AsyncResponse,TextToSpeech.OnUtteranceCompletedListener {

    private TextToSpeech textToSpeech;
    private TextView textView;
    private ArrayList<String> arr;
    private Receiver receiver;
    private ListView lv;
    private ArrayAdapter adapter;
    private RelativeLayout LayoutMain;
    private Message[] inbox;
    private boolean isProcessed;
    private boolean isOnReading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_mail);
        setTitle("Read Mail");
        LayoutMain=(RelativeLayout)findViewById(R.id.readlayout);
        textToSpeech = new TextToSpeech(this, this);
        receiver =new Receiver();
        arr=new ArrayList<String>();
        textView = (TextView) findViewById(R.id.textView);
        lv=(ListView)findViewById(R.id.window_list);
        adapter=new ArrayAdapter<String>(this,R.layout.activity_list_view,arr);
        isProcessed=false;
        isOnReading=false;

        final GestureDetector mDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener(){
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if(isProcessed && !isOnReading){
                    if(textToSpeech.isSpeaking()){textToSpeech.stop();}
                    readMails();
                }
                return true;
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if (textToSpeech.isSpeaking() && isOnReading){//&& isInstructionFinished) {
                    textToSpeech.stop();
                }

                return true;
            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onLongPress(MotionEvent e) {
                super.onLongPress(e);
                if(!isOnReading) {
                    if(textToSpeech.isSpeaking()){textToSpeech.stop();}
                    getInstructions();
                }
            }

            @Override
            public boolean onDown(MotionEvent event) {
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
                               // onSwipeRight();
                            } else {
                               // onSwipeLeft();
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
        lv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                return mDetector.onTouchEvent(event);
            }
        });



    }

    //retrieve inbox mails
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void getMails(Message[] output){
            inbox=output;
            speakOut("Mail retrieval finished","normal");
            isProcessed=true;

            textView.setText("");

            textView.setText("You have " + inbox.length + " unread emails");
            speakOut("You have " + inbox.length + " unread emails", "normal");
            getInstructions();
            speakOut("Long Press to get instructions again", "normal");

    }

    //read out mails one by one
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void readMails(){
        isOnReading=true;

        try {

            arr.clear();
            for (int i = inbox.length-1; i >= 0; i--) {

                Message indvidualmsg = inbox[i];
                arr.add(""+indvidualmsg.getFrom()[0]);
                speakOut("Mail No "+(inbox.length-i),"normal");
                speakOut(""+indvidualmsg.getFrom()[0],"normal");
                speakOut(""+indvidualmsg.getSubject(),""+i);
            }

            lv.setAdapter(adapter);

        } catch (NoSuchProviderException exp) {
            exp.printStackTrace();
        } catch (MessagingException exp) {
            exp.printStackTrace();
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void speakOut(String s, String id) {
        textToSpeech.speak(s, TextToSpeech.QUEUE_ADD, null,id);
    }
    public void onUtteranceCompleted(String utteranceId) {
        if(utteranceId.equals("0")){
              isOnReading=false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void getInstructions(){
        speakOut("Single Tap to start reading emails","normal");
        speakOut("Double Tap to stop reading","normal");
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onInit(int i) {
        textToSpeech.setOnUtteranceCompletedListener(this);
        speakOut("Here's read mail page.\n\n Wait... Retrieving emails on process","normal");

        receiver.delegate = this;
        receiver.execute();
    }

    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
            textToSpeech = null;
        }

        receiver.cancel(true);
        super.onDestroy();
    }

}

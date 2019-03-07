package com.example.udhan.voice3;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.EditText;
import android.speech.tts.TextToSpeech;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class SendMail extends AppCompatActivity implements RecognitionListener,TextToSpeech.OnInitListener,TextToSpeech.OnUtteranceCompletedListener{
    private TextToSpeech textToSpeech;
    private SpeechRecognizer speechRecognizer;
    private Intent speechRecognizerIntent;
    private EditText recipients;
    private EditText title;
    private EditText emailBody;
    private String fullText;
    private final StepTracker st=new StepTracker();
    private float x1,x2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_mail);
        setTitle("Send Mail");
        checkPermission();
        textToSpeech = new TextToSpeech(this, this);
        recipients=findViewById(R.id.receipients);
        title=findViewById(R.id.title);
        emailBody=findViewById(R.id.editText);
        fullText="";

        new Thread(){
            @Override
            public void run() {

                ControlThread t1=new ControlThread("1",st);
                ControlThread t2=new ControlThread("2",st);
                ControlThread t3=new ControlThread("3",st);
                ControlThread t4=new ControlThread("4",st);

                try {
                    t1.start();
                    t1.join();
                    t2.start();
                    t2.join();
                    t3.start();
                    t3.join();
                    t4.start();
                    t4.join();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction()){
            case MotionEvent.ACTION_UP:
                x2 = motionEvent.getX();
                float deltaX = x2 - x1;
                if(speechRecognizer != null)
                {
                    speechRecognizer.destroy();
                }
                fullText="";
                CharSequence temp="";
                int step=st.getCurrentStep();
                if(step==1){
                    temp=recipients.getText();
                }else if(step==2){
                    temp=title.getText();
                }else if(step==3){
                    temp=emailBody.getText();
                }else if(step==4 & st.isOnProcess()){


                    if (deltaX > 150)
                    {
                        sendEmail();
                        st.setOnProcess(false);
                    }
                    else if(-1*deltaX>150){
                    }
                    else
                    {
                        speakOut("Ready to send.\n Swipe right to send.","normal");
                    }
                }

                if(-1*deltaX>150){
                    finish();
                }else if(step>=1 && step<=3){
                    if(temp.length()!=0){
                        if(step==1){speakOut("Enter Topic","normal");}
                        else if(step==2){speakOut("Enter Content","normal");}
                        else if(step==3){speakOut("Ready to Send.\n Swipe right to send.","normal");}
                        st.setOnProcess(false);
                    }
                    else{
                        speakOut("Input fields cannot be empty","normal");
                    }
                }

                break;
            case MotionEvent.ACTION_DOWN:
                x1 = motionEvent.getX();
                listen();
                break;
        }
        return false;
    }

    public void onBeginningOfSpeech() {
        // TODO Auto-generated method stub
        //Log.i(TAG, "onbeginningofspeech");
    }

    public void onBufferReceived(byte[] arg0) {
        // TODO Auto-generated method stub
        //Log.i(TAG, "onbufferreceived");
    }

    public void onEndOfSpeech() {
        // TODO Auto-generated method stub
        //Log.i(TAG, "onendofspeech");
    }

    public void onError(int errorCode) {
        // TODO Auto-generated method stub
        if ((errorCode == SpeechRecognizer.ERROR_NO_MATCH)
                || (errorCode == SpeechRecognizer.ERROR_SPEECH_TIMEOUT))
        {

            speechRecognizer.startListening(speechRecognizerIntent);
        }
        else
        {
        }
    }

    public void onEvent(int arg0, Bundle arg1) {
        // TODO Auto-generated method stub
        //Log.i(TAG, "onevent");
    }

    public void onPartialResults(Bundle arg0) {
        // TODO Auto-generated method stub
        //Log.i(TAG, "onpartialresults");
    }

    public void onReadyForSpeech(Bundle arg0) {
        // TODO Auto-generated method stub
        //Log.i(TAG, "onreadyforspeech");
    }

    public void onResults(Bundle bundle) {
        // TODO Auto-generated method stub
        ArrayList<String> matches=bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION  );
        if(matches!=null && st.isOnProcess()) {
            fullText=fullText+" "+matches.get(0);
            int temp=st.getCurrentStep();

            if(temp==1){
                fullText=fullText.replaceAll("\\s+","");
                fullText=fullText.toLowerCase();
                recipients.setText(fullText);}
            else if(temp==2){title.setText(fullText);}
            else if(temp==3){emailBody.setText(fullText);}

        }
        speechRecognizer.startListening(speechRecognizerIntent);
    }

    public void onRmsChanged(float arg0) {
        // TODO Auto-generated method stub
        // Log.i(TAG, "onrmschanged");
    }

    public void listen()
    {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(this);
        speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en");
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS,1000000);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        //speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);

        speechRecognizer.startListening(speechRecognizerIntent);
    }

    //check for microphone permission
    public void checkPermission(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            if(!(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)== PackageManager.PERMISSION_GRANTED)){
                Intent intent=new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:"+getPackageName()));
                startActivity(intent);
                finish();
            }
        }
    }



    //send new mails
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void sendEmail() {
        speakOut("Sending email on process","normal");
        try {
            String email = recipients.getText().toString().trim();
            String subject = title.getText().toString().trim();
            String message = emailBody.getText().toString().trim();

            //Creating SendMail object
            Sender sm = new Sender(this, email, subject, message);

        //Executing sendmail to send email

            int j=sm.execute().get();
//            recipients.setText("");
//            title.setText("");
//            emailBody.setText("");
            if(j==0){speakOut("email sent successfully.","end");}
            else{speakOut("sending email failed. please try again.","end");}
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onInit(int i) {
           textToSpeech.setOnUtteranceCompletedListener(this);
           speakOut("Here's send mail page.\n Touch the screen and hold while entering details each time.\n enter the Recipient","normal");
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void speakOut(String s, String id) {
        textToSpeech.speak(s, TextToSpeech.QUEUE_FLUSH, null,id);
    }

    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
            textToSpeech = null;
        }
        super.onDestroy();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onUtteranceCompleted(String s) {
        if(s.equals("end")){
            finish();
        }
    }
}

//Thread to fill recipient,titl and content one by one
class ControlThread extends Thread  {
    private StepTracker st;
    private String name;
    public ControlThread(String name,StepTracker st)
    {
        this.name = name;
        this.st=st;
    }

    @Override
    public void run()
    {
        st.setOnProcess(true);
        int temp=st.getCurrentStep();
        st.setCurrentStep(temp+1);
        while(st.isOnProcess()){ }
    }

}

//keep track on current step
class StepTracker{
    private int currentStep;
    private boolean onProcess;
    public StepTracker(){currentStep=0;onProcess=false;}
    public int getCurrentStep() {
        return currentStep;
    }

    public void setCurrentStep(int currentStep) {
        this.currentStep = currentStep;
    }

    public boolean isOnProcess() {
        return onProcess;
    }

    public void setOnProcess(boolean onProcess) {
        this.onProcess = onProcess;
    }
}
package com.example.sami.frauddetection2;

import android.app.NotificationChannel;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.*;
import java.io.*;
import java.net.URL;
import java.text.DateFormat;
import java.util.*;

import android.app.Notification;
import android.app.NotificationManager;

import android.util.Log;
import android.widget.*;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.google.cloud.speech.v1.RecognitionAudio;
import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.RecognitionConfig.AudioEncoding;
import com.google.cloud.speech.v1.RecognizeResponse;
import com.google.cloud.speech.v1.SpeechClient;
import com.google.cloud.speech.v1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1.SpeechRecognitionResult;
import com.google.protobuf.ByteString;

/*import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Paths.get;*/

public class MainActivity extends AppCompatActivity {

    private SpeechRecognizer sr;
    public String q="";
    public int stoptrue=0;

    public void pushNotification () {
                String tittle="sami";
                String subject="Potential fraud detected ";
                String body="Be careful";

                NotificationManager notif=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                Notification notify=new Notification.Builder
                        (getApplicationContext()).setContentTitle(tittle).setContentText(body).
                        setContentTitle(subject).setSmallIcon(R.drawable.ic_launcher_background).build();

                notify.flags |= Notification.FLAG_AUTO_CANCEL;
                notif.notify(0, notify);
            }

    public File getLatestCallRec () {
        File storage=Environment.getExternalStorageDirectory();
        String date="20180120";
        String time="130855";
        String number="+359884790322";
        String filename="/Records/0d"+date+time+"p"+number+".arm";
        filename="/Records/tusk.arm";
        File file = new File(storage,filename);
        return file;
    }

   /* public void recognise () throws Exception {
        TextView textb0x=findViewById(R.id.textbox123);

        File storage = Environment.getExternalStorageDirectory();
        String fileName ="Records/tusk.amr";
        File record = new File(storage, fileName);

        final FileInputStream fis = new FileInputStream(record);
        byte[] buf = new byte[4000];

        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        while (true) {
            int bytesRead = fis.read(buf);
            if (bytesRead <= 0) break;

            byteOut.write(buf, 0, bytesRead);
        }
        fis.close();
        byte[] data = byteOut.toByteArray();
        byteOut.close();

//        Path path= get(fileName);
//        byte[] data = readAllBytes(path);

        SpeechClient speech = SpeechClient.create();

        ByteString audioBytes = ByteString.copyFrom(data);
        RecognitionConfig config = RecognitionConfig.newBuilder()
                .setEncoding(AudioEncoding.AMR)
                .setSampleRateHertz(16000)
                .setLanguageCode("en-US")
                .build();
        RecognitionAudio audio = RecognitionAudio.newBuilder()
                .setContent(audioBytes)
                .build();
        RecognizeResponse response = speech.recognize(config, audio);
        List<SpeechRecognitionResult> results = response.getResultsList();
        StringBuilder line= new StringBuilder();

        for (SpeechRecognitionResult result: results) {
            SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
            line.append(alternative.getTranscript());
        }
        textb0x.setText(line.toString());
        speech.close();
    }*/

    public void request () {
        final TextView mTextView = findViewById(R.id.textbox123);
        RequestQueue queue = Volley.newRequestQueue(this);
        mTextView.setText("req");
       // q="";
        String url="http://192.168.100.36:8000/algo?q="+q;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                       // mTextView.setText("Response is: "+ response.substring(0,500));
                        String result=response;
                        if (result.equals("1")) {
                            pushNotification();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mTextView.setText("That didn't work!");
            }
        });
        queue.add(stringRequest);
    }

    public void recognise() {


        sr = SpeechRecognizer.createSpeechRecognizer(this);
        sr.setRecognitionListener(new RecordListener());

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        //Specify the calling package to identify your application
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,getClass().getPackage().getName());
        //Given an hint to the recognizer about what the user is going to say
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        //specify the max number of results
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,1);
        //User of SpeechRecognizer to "send" the intent.
        sr.startListening(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView textbox=findViewById(R.id.textbox123);
        textbox.setVisibility(View.GONE);
        Button startbutton=findViewById(R.id.start);
        startbutton.setOnClickListener(new View.OnClickListener() {
                                      public void onClick(View v) {
                                          //while (stoptrue==0) {
                                              recognise();
                                          //}
                                          //request();
                                      }
                                  });
        /*Button stopbutton=findViewById(R.id.stop);
        stopbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                stoptrue=1;
            }
        });*/
        //request();
//        try {
//            Handler h = new Handler();
//            h.post(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        recognise();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
//        } catch (Exception e) {
//            TextView tt = findViewById(R.id.textbox123);
//            tt.setText("error");
//        }

       /* sr = SpeechRecognizer.createSpeechRecognizer(this);
        sr.setRecognitionListener(new RecordListener());

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        //Specify the calling package to identify your application
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,getClass().getPackage().getName());
        //Given an hint to the recognizer about what the user is going to say
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        //specify the max number of results
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,5);
        //User of SpeechRecognizer to "send" the intent.
        sr.startListening(intent);*/
    }

    class RecordListener implements RecognitionListener	{
        public void onReadyForSpeech(Bundle params)	{
        }
        public void onBeginningOfSpeech(){
        }
        public void onRmsChanged(float rmsdB){
        }
        public void onBufferReceived(byte[] buffer)	{
        }
        public void onEndOfSpeech()	{
        }
        public void onError(int error)	{
            logthis("error " + error);
        }
        public void onResults(Bundle results) {
            // Fill the list view with the strings the recognizer thought it could have heard, there should be 5, based on the call
            ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            //display results.
            logthis("results: "+String.valueOf(matches.size()));
            q=matches.get(0);
            request();
            for (int i = 0; i < matches.size(); i++) {
                logthis("result " +i+":"+ matches.get(i));
                System.out.print("q="+q);
            }

        }
        public void onPartialResults(Bundle partialResults)
        {
        }
        public void onEvent(int eventType, Bundle params) {
        }

        void logthis(String str) {
            System.out.println(str);
        }
    }

}

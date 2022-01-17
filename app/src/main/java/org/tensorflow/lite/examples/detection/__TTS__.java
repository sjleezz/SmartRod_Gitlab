package org.tensorflow.lite.examples.detection;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class __TTS__ extends AppCompatActivity {

    public TextToSpeech tts;
    SpeechRecognizer mRecognizer;
    Button button_register;
    Button button_connect;
    int telCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caller);

        // 화면 조정용 코드
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        // Intent 정의
        Intent intent_register = new Intent(this, Caller_register.class);
        Intent intent_call = new Intent(this, Caller_connect.class);

        button_register = (Button) findViewById(R.id.button_register);
        button_connect = (Button) findViewById(R.id.button_call);

        button_register.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                //startActivity(intent_register);
            }
        });
        button_connect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                tts.setSpeechRate(1f);
                //startActivity(intent_call);

            }
        });

        initTTS();

    }

    public void initTTS() {
        Log.d("initTTS()"," __TTS__");
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int state) {
                if (state == TextToSpeech.SUCCESS) {
                    tts.setLanguage(Locale.KOREAN);
                }
            }
        });
        tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String s) {
            }

            @Override
            public void onDone(String s) {

            }

            @Override
            public void onError(String s) {
            }

            @Override
            public void onRangeStart(String utteranceId, int start, int end, int frame) {
            }
        });
    }

    public void funcVoiceOut(String OutMsg) {                    // 음성을 말해주는 기능
        if (OutMsg.length() < 1)
            return;       // error
        if (!tts.isSpeaking()) {
            tts.speak(OutMsg, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    // TTS 종료용
    public void ttsDestroy() {
        if(tts !=null){
            tts.shutdown();
        }
    }

    public void showState(final String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }


}

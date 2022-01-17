package org.tensorflow.lite.examples.detection;

import android.content.Intent;
import android.os.Bundle;

import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import su.levenetc.android.textsurface.Debug;
import su.levenetc.android.textsurface.TextSurface;
import su.levenetc.android.textsurface.sample.checks.CookieThumperSample;

public class Intro extends __TTS__ {
    private TextSurface textSurface;


    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        textSurface = (TextSurface) findViewById(R.id.text_surface);
        Handler myHandler = new Handler();

        textSurface.postDelayed(new Runnable() {
            @Override public void run() {
                show();
            }
        }, 500);

        myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                funcVoiceOut("");
                while(tts.isSpeaking()) {}

                Intent intent = new Intent(Intro.this, Main.class);

                startActivity(intent); //다음 액티비티 이동
                finish(); // 이 액티비티를 종료
            }
        }, 8500);


        findViewById(R.id.btn_refresh).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                show();
            }
        });

        CheckBox checkDebug = (CheckBox) findViewById(R.id.check_debug);
        checkDebug.setChecked(Debug.ENABLED);
        checkDebug.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Debug.ENABLED = isChecked;
                textSurface.invalidate();
            }
        });
    }

    private void show() {
        textSurface.reset();
        CookieThumperSample.play(textSurface, getAssets());


    }
}

package org.tensorflow.lite.examples.detection;

import android.Manifest;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

import static java.lang.Thread.sleep;

import androidx.core.app.ActivityCompat;

public class Main extends __TTS__ implements TextPlayer {

    SpeechRecognizer mRecognizer;

    private final Bundle params = new Bundle();
    private PlayState playState = PlayState.STOP;
    private int standbyIndex = 0;
    private int lastPlayIndex = 0;
    private Intent intent_stt;
    private boolean isTTSEnd = false;

    String CONTENT =
            "음성인식을 통해 원하는기능에 들어갈수 있습니다..." +
            "화면을 한번 탭하고, 딸깍소리가 나면 원하는기능에해당하는 음성을 말씀해주세요..." +
            "물론, 이 음성이나오는 도중에도 가능합니다..." +
            "장애물탐지기능을 실행하시려면 '인식..'." +
            "긴급전화를 하시려면 '전화..'." +
            "긴급전화에 사용할 번호를 등록하시려면 '등록..'." +
            "건물위치찾기기능을 사용하시려면 '건물' 이라고 말씀해주세요..." +
            "다시 들으시려면 '다시' 라고 말씀해주세요.";

    private Button toggle_btn;
    boolean PAUSE = false;
    String RESULT;
    public final static Integer PERMISSION = 1;

    private SoundPool soundPool;
    int openSound;
    int closeSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("onCreate()"," main");

        // 퍼미션 체크
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET,
                    Manifest.permission.RECORD_AUDIO}, PERMISSION);
        }

        // 화면 조정용 code
        getWindow().getDecorView().getBackground().setDither(true);
        getWindow().setFormat(PixelFormat.RGBA_8888);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        // 버튼 사운드용
        soundPool = new SoundPool(3,AudioManager.STREAM_MUSIC, 0);
        openSound = soundPool.load(this, R.raw.open_button, 1);
        closeSound = soundPool.load(this, R.raw.close_button, 1);
        soundPool.setVolume(openSound, 1.0f, 1.0f);
        soundPool.setVolume(closeSound, 1.0f, 1.0f);

        // STT 초기화
        intent_stt = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent_stt.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent_stt.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        mRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mRecognizer.setRecognitionListener(Rlistener);

        initView();  // View 객체 초기화

        try {
            sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        startPlay_tts();

    }

    @Override
    public void onRestart() {
        Log.d("onRestart()"," main");
        super.onRestart();


    }

    @Override
    public void onPause() {    // startActivity() 시작시 직후 호출됨
        Log.d("onPause()"," main");
        super.onPause();
        //pausePlay_tts();
        tts.stop(); //pausePlay_tts를 호출하면 음성인식 기능이 실행되므로 흐름 제어를 위해 pausePlay_tts를 생략하였음
        playState = PlayState.STOP;
    }

    @Override
    public void onStop() {
        Log.d("onStop()"," main");
        super.onStop();
    }

    @Override
    public void onResume() {
        Log.d("onResume()"," main");

        super.onResume();

    }

    @Override
    public void onDestroy() {

        Log.d("onDestroy()"," main");
        if(tts != null) {
            tts.stop();
            tts.shutdown();
        }
        try{
            mRecognizer.destroy();
        }
        catch (Exception e)
        {
            Log.e("onDestroy()","Exception: "+e.toString());
        }
        if(soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
        super.onDestroy();

    }

    // UI 객체 초기화
    private void initView() {
        toggle_btn = (Button) findViewById(R.id.ttsToggle);
        toggle_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(playState.isPlaying()) {
                    soundPool.play(openSound, 1f, 1f, 0, 0, 1f);
                    pausePlay_tts();
                }
                else {
                    soundPool.play(closeSound, 1f, 1f, 0, 0, 1f);
                    startPlay_tts();
                }
                System.out.println("플레이상태 : "+playState.getState());
            }
        });

    }


    //STT용 리스너 정의
    private final RecognitionListener Rlistener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle params) {
            Toast.makeText(getApplicationContext(),"음성인식을 시작합니다.",Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onBufferReceived(byte[] buffer) {
            // TODO Auto-generated method stub
        }
        @Override
        public void onEndOfSpeech() {
            // TODO Auto-generated method stub
        }
        @Override
        public void onRmsChanged(float rmsdB) {         // 들리는 소리 크기가 변경되었을 때 호출
            // TODO Auto-generated method stub
        }
        @Override
        public void onPartialResults(Bundle partialResults) {
            // TODO Auto-generated method stub
        }
        @Override
        public void onEvent(int eventType, Bundle params) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onBeginningOfSpeech() {
            // TODO Auto-generated method stub
        }
        @Override public void onError(int error) {      // 에러 발생 시 호출
            String message;
            switch (error) {
                case SpeechRecognizer.ERROR_AUDIO: message = "오디오 에러";
                    break;
                case SpeechRecognizer.ERROR_CLIENT: message = "클라이언트 에러";
                    break;
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS: message = "퍼미션 없음";
                    break;
                case SpeechRecognizer.ERROR_NETWORK: message = "네트워크 에러";
                    break;
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT: message = "네트웍 타임아웃";
                    break;
                case SpeechRecognizer.ERROR_NO_MATCH: message = "찾을 수 없음";
                    break;
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY: message = "RECOGNIZER가 바쁨";
                    break;
                case SpeechRecognizer.ERROR_SERVER: message = "서버가 이상함";
                    break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT: message = "말하는 시간초과";

                    break;
                default: message = "알 수 없는 오류";
                    break;
            }
            funcVoiceOut("에러가 발생했습니다... 이유는 다음과 같습니다..." + message);
            while(tts.isSpeaking()){}
            finish();
            startActivity(getIntent());
        }
        @Override
        public void onResults(Bundle results) { // 말을 끝내면 ArrayList에 단어를 넣고 textView에 단어를 이어줍니다.
            ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            StringBuffer word = new StringBuffer();
            for(int i = 0; i < Objects.requireNonNull(matches).size() ; i++){
                word.append(matches.get(i));
            }
            PAUSE = true;
            RESULT = word.toString();
            checkSTT(RESULT);
        }
    };

    // 입력받은 음성을 판단하여 해당 기능의 액티비티로 이동
    public void checkSTT(String str) {
        Intent intent;

        if(str.equals("인식")) {
            intent = new Intent(Main.this, DetectorActivity.class);
        }
        else if(str.equals("전화")) {
            intent = new Intent(Main.this, Caller_connect.class);
        }
        else if(str.equals("건물")) {
            intent = new Intent(Main.this, Compass.class);

        }
        else if(str.equals("등록")) {
            intent = new Intent(Main.this, Caller_register.class);
        }
        else if(str.equals("다시")) {
            intent = getIntent();
        }
        else {
            intent = getIntent();
            funcVoiceOut("잘못된 명령입니다. 다시 말씀해주세요.");

        }
        finish();
        startActivity(intent);
    }

    @Override
    public void funcVoiceOut(final String OutMsg) {                    // 음성을 말해주는 기능
        tts.speak(OutMsg, TextToSpeech.QUEUE_FLUSH, null);
    }

    @Override
    public void startPlay_tts() {
        if (playState.isStopping() && !tts.isSpeaking()) {
            Handler hd = new Handler();
            hd.postDelayed(new Runnable() {
                @Override
                public void run() {
                    playState = PlayState.PLAY;
                    funcVoiceOut(CONTENT);
                }
            }, 1000);

        }
        else if (playState.isWaiting()) {
            standbyIndex += lastPlayIndex;
            System.out.println("현재 :startPlay_tts | standbyIndex : " + standbyIndex);
            Handler hd = new Handler();
            hd.postDelayed(new Runnable() {
                @Override
                public void run() {
                    playState = PlayState.PLAY;
                    funcVoiceOut(CONTENT.substring(standbyIndex));
                }
            }, 1000);
        }
        playState = PlayState.STOP;

    }

    @Override
    public void pausePlay_tts() {
        System.out.println("현재 : pausePlay_tts");
        playState = PlayState.WAIT;
        System.out.println("tts :" + tts.toString());
        tts.shutdown();
        System.out.println("tts :" + tts.toString());
        mRecognizer.startListening(intent_stt); //tts 음성이 멈추면 stt 음성인식을 해야 하므로 STT 실행
    }

    @Override
    public void stopPlay_tts() {
        System.out.println("현재 : stopPlay_tts");
        System.out.println("tts :" + tts.toString());
        tts.shutdown();
        System.out.println("tts :" + tts.toString());
        playState = PlayState.STOP;
        mRecognizer.startListening(intent_stt); //tts 음성이 멈추면 stt 음성인식을 해야 하므로 STT 실행
    }

    @Override
    public void showState(final String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }






}

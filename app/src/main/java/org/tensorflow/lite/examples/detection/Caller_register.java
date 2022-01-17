package org.tensorflow.lite.examples.detection;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;

public class Caller_register extends __TTS__ {
    Intent intent;
    Intent back;
    TextView textView;
    final int PERMISSION = 1;
    Button sttBtn;
    boolean isName = true;
    StringBuffer word = new StringBuffer();
    List<String> data ;
    int errCount = 0;
    private Context mContext;

    String name = "";
    String tel = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caller_register);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        mContext = this;

        if ( Build.VERSION.SDK_INT >= 23 ) { // 퍼미션 체크
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET,
                    Manifest.permission.RECORD_AUDIO}, PERMISSION);
        }

        back = new Intent(this, __TTS__.class);

        // STT
        sttBtn = (Button) findViewById(R.id.sttStart);
        textView = (TextView)findViewById(R.id.sttResult);
        intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ko-KR");
        sttBtn.setOnClickListener(v -> { mRecognizer=SpeechRecognizer.createSpeechRecognizer(this);
            mRecognizer.setRecognitionListener(listener);
            mRecognizer.startListening(intent);

        });
    }

    @Override
    public void onStart() {
        super.onStart();

        // 2초간 멈추게 하고싶다면
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                //Toast.makeText(getApplicationContext(),"[onStart] run",Toast.LENGTH_SHORT).show();
                funcVoiceOut("이름부터 말씀해주세요..");
                while (tts.isSpeaking()) { }
                sttBtn.performClick();
            }
        }, 2000);  // 2000은 2초

    }

    @Override
    public void onResume() {
        super.onResume();
        //Toast.makeText(getApplicationContext(),"[onResume] run",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (tts != null) {
            tts.stop();
        }
    }

    @Override
    public void onDestroy() {
        if (tts != null) {
            ttsDestroy();
        }
        super.onDestroy();
    }

    public void restartTTS() {
        word.setLength(0);
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        funcVoiceOut("지금 이 말이 끝나면 말씀해주세요.");
        while (tts.isSpeaking()) { }
        mRecognizer.startListening(intent);

    }

    public void saveNameNAddress() throws IOException {         // 내부 저장소에 이름과 전화번호 저장
        String filename = "SmartRod";

        tel = tel.replaceAll(" ", "");
        String text = PreferenceManager.getString(mContext, name);

        if (text.equals("")) {
            text = "저장된 데이터가 없습니다.";
            PreferenceManager.setString(mContext, name, tel);
        }

        System.out.println("name : " + name + ", tel : " + tel);
        funcVoiceOut("저장이 완료되었습니다.");
        while (tts.isSpeaking()) { }
    }

    private RecognitionListener listener = new RecognitionListener() {

        @Override public void onReadyForSpeech(Bundle params) {
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
                default: message = "알 수 없는 오류임";
                    break;
            }
            Toast.makeText(getApplicationContext(), "에러가 발생하였습니다. : " +
                    message, Toast.LENGTH_SHORT).show();

            if(errCount == 5) {
                funcVoiceOut("다섯번 실패로 이전 화면으로 돌아갑니다.");
                while (tts.isSpeaking()) { }
                errCount = 0;
                startActivity(back);
                ttsDestroy();
            }
            else {
                funcVoiceOut("입력에 실패하였습니다.");
                while (tts.isSpeaking()) { }
                errCount++;
                restartTTS();
            }
        }

        @Override public void onResults(Bundle results) { // 말을 하면 ArrayList에 단어를 넣고 textView에 단어를 이어줍니다.
            ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

            for(int i = 0; i < matches.size() ; i++){
                System.out.println("matches.get(" + i + ") : " + matches.get(i));
                textView.setText(matches.get(i));
                System.out.println("word : " + word);
                word.append(matches.get(i));
            }
            runOnUiThread(new Runnable() {
                public void run() {
                    if(word.toString().equals("입력 완료") || word.toString().equals("입력완료")) {
                        funcVoiceOut("이름과 전화번호를 저장합니다.");
                        while (tts.isSpeaking()) { }
                        try {
                            saveNameNAddress();            // 파일에 저장
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        ttsDestroy();
                        mRecognizer.destroy();
                        finish();
                    }
                    else {
                        if(word.toString().equals("다시 입력") || word.toString().equals("다시입력")) {
                            funcVoiceOut("이름부터 다시 입력합니다.");
                            while (tts.isSpeaking()) { }
                            name = "";
                            tel = "";
                            restartTTS();
                        }
                        else {
                            funcVoiceOut(word.toString());    // 음성 출력
                            while (tts.isSpeaking()) { }

                            char tmp;
                            for(int i = 0 ; i < word.toString().length() ; i++) {
                                tmp = word.toString().charAt(i);
                                System.out.println("tmp : " + tmp);
                                if(tmp == ' ') {
                                    continue;               // 공백 무시
                                }
                                boolean isDigit = Character.isDigit(tmp);
                                System.out.println("isDigit : "+ isDigit);
                                // word가 전화번호인 경우 false, 이름이면 true
                                if(isDigit) {
                                    isName = false;          // 하나라도 숫자가 나온 경우는 전화번호로 판단함
                                    break;
                                }
                                else {
                                    isName = true;
                                }
                            }

                            if (!isName) {                                                         // 만약 word가 숫자라면
                                tel = word.toString();
                                System.out.println("전화번호 : "+ tel);
                                System.out.println("name : "+ name);
                                System.out.println("tel : "+ tel);
                            }
                            else {                                                                // 이름을 받은 경우
                                name = word.toString();
                                System.out.println("이름 : "+ word);
                                System.out.println("name : "+ name);
                                System.out.println("tel : "+ tel);
                            }
                        }
                    }

                    if(tel.length() == 0 || name.length() == 0) {
                        if(tel.length() == 0 && name.length() != 0) {                // 아직 전화번호가 입력되지 않은 경우
                            funcVoiceOut("전화번호를 입력하겠습니다.");
                            while (tts.isSpeaking()) { }
                            errCount=0;
                            restartTTS();
                        }
                        if(tel.length() != 0 && name.length() == 0) {                // 아직 전화번호가 입력되지 않은 경우
                            funcVoiceOut("이름을 입력하겠습니다.");
                            while (tts.isSpeaking()) { }
                            errCount=0;
                            restartTTS();
                        }
                    }
                    else {       // 모두 입력된 경우
                        while (tts.isSpeaking()) { }
                        try {
                            sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        funcVoiceOut("이름은 " + name + "이고, 전화번호는 " + tel +"입니다.");
                        while (tts.isSpeaking()) { }
                        try {
                            sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        funcVoiceOut("다시 입력하고 싶다면 바꾸고자 하는 이름이나 전화번호를 말씀해주세요. 다 입력하셨다면 '입력 완료'라고 말씀해주세요.");
                        while (tts.isSpeaking()) { }
                        try {
                            sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        errCount=0;
                        restartTTS();
                    }

                }
            });


        }
    };
}



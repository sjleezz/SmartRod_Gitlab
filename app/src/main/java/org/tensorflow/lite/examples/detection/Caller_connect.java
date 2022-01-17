package org.tensorflow.lite.examples.detection;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import static java.lang.Thread.sleep;

public class Caller_connect extends __TTS__ {

    Intent intent;
    Button sttbtn;
    TextView textView;
    String connect_name;
    int errCount = 0;
    private Context mContext;
    int call_fin = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caller_connect);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        mContext = this;

        sttbtn = (Button) findViewById(R.id.btn);
        textView = (TextView) findViewById(R.id.name);
        intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ko-KR");
        sttbtn.setOnClickListener(v -> { mRecognizer=SpeechRecognizer.createSpeechRecognizer(this);
            mRecognizer.setRecognitionListener(listener);
            mRecognizer.startListening(intent);
        });

        // 2초간 멈추게 하고싶다면
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                //Toast.makeText(getApplicationContext(),"[onStart] run",Toast.LENGTH_SHORT).show();
                funcVoiceOut("등록된 이름을 말씀해주세요.");
                while(tts.isSpeaking()) {}
                sttbtn.performClick();
            }
        }, 1000);  // 2000은 2초를 의미합니다.
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();
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
        funcVoiceOut("지금 이 말이 끝나면 말씀해주세요.");
        while (tts.isSpeaking()) { }
        mRecognizer.startListening(intent);

    }

    public String loadTel() throws IOException, ClassNotFoundException {      // 전화번호 불러옴
        String fileName = "SmartRod";
        String text = PreferenceManager.getString(mContext, connect_name);

        if (text.equals("")) {
            funcVoiceOut("저장된 번호가 없습니다.");
            while (tts.isSpeaking()) { }
            tts.stop();
            mRecognizer.destroy();
            Intent intent = getIntent();
            finish();
            startActivity(intent);

        }

        System.out.println("저장소에서 불러온 번호 : " + text);
        return text;
    }

    public void call(String tel){
        String tel_num = "tel:" + tel;
        Uri number = Uri.parse("tel:" + tel);
        Intent intent = new Intent(Intent.ACTION_CALL, number);
        startActivity(intent);


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

            funcVoiceOut("입력에 실패하였습니다.");
            while (tts.isSpeaking()) { }
            restartTTS();

        }

        @Override public void onResults(Bundle results) { // 말을 하면 ArrayList에 단어를 넣고 textView에 단어를 이어줍니다.
            ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            StringBuffer word = new StringBuffer();

            for(int i = 0; i < matches.size() ; i++){
                System.out.println("matches.get(" + i + ") : " + matches.get(i));
                textView.setText(matches.get(i));
                word.append(matches.get(i));
            }
            connect_name = word.toString();

            funcVoiceOut(word.toString());          // 음성 출력
            while (tts.isSpeaking()) { }

            try {
                call(loadTel());

            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            finish();
        }
    };
}




package org.tensorflow.lite.examples.detection;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.Observable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class rxjava_example extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rxjava);

        // Observable 객체 선언
        Observable<String> observable = Observable.create(emitter -> {
                    emitter.onNext(Thread.currentThread().getName() + "\n: RxJava Observer Test");
                    emitter.onComplete();
                }
        );
        // subscribeOn : 해당 스레드에서 observer가 실행
        observable.subscribeOn(Schedulers.io()).subscribe(observer); // io스레드에서 실행

    }

    // 실행을 중단하기 위한 Disposable
    DisposableObserver<String> observer = new DisposableObserver<String>() {
        @Override
        public void onNext(@NonNull String s) {
            TextView rxjava_text = findViewById(R.id.textView);
            rxjava_text.setText(s);
        }

        @Override
        public void onError(@NonNull Throwable e) {
            Log.d("TEST", "Observer Error...");
        }

        @Override
        public void onComplete() {
            Log.d("TEST", "Observer Complete!");
        }
    };

}



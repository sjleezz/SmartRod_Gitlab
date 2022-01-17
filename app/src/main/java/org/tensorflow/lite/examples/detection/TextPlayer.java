package org.tensorflow.lite.examples.detection;

public interface TextPlayer {

    void startPlay_tts() throws InterruptedException;

    void pausePlay_tts();

    void stopPlay_tts();
}
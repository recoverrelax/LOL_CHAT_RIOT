package com.recoverrelax.pt.riotxmppchat.NotificationCenter;


import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;

import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.MyUtil.LogUtils;

import java.util.HashMap;
import java.util.Locale;

import javax.inject.Inject;

import static com.recoverrelax.pt.riotxmppchat.MyUtil.LogUtils.LOGE;

public class MessageSpeechNotification implements TextToSpeech.OnInitListener {

    private static final String MESSAGE_START = "New message from: ";
    private static final long DELAY_FROM_MESSAGE = 500;
    private final Context context;
    private final String TAG = "MessageSpeechNotification";
    private TextToSpeech tts;
    private String message;
    private String from;
    private boolean isReady = false;
    private long lastMessageDateMillis = 0;
    private String lastMessageFrom = null;

    @Inject
    public MessageSpeechNotification(MainApplication context) {
        this.context = context;
    }

    public void getReady() {
        tts = new TextToSpeech(context, this);
    }

    public void sendMessageSpeechNotification(String message, String from) {
        LogUtils.LOGI(TAG, "Enters here");
        if (isReady) {
            speak(from, message);
        } else {
            getReady();
            this.message = message;
            this.from = from;
        }
    }

    public void sendStatusSpeechNotification(String message) {

        if (isReady) {
            speak(message);
        } else {
            getReady();
            this.message = message;
        }
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            tts.setLanguage(Locale.US);
            tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override public void onStart(String s) {
                }

                @Override public void onDone(String s) {
                }

                @Override public void onError(String s) {
                }
            });

            isReady = true;
            speak(this.from, this.message);
        } else {
            isReady = false;
            LOGE(TAG, "Initilization Failed!");
        }
    }

    public void speak(String from, String message) {
        tts.setSpeechRate(0.8f);
        if (from == null)
            speak(message);
        else {

            long dateDifference = (System.nanoTime() - lastMessageDateMillis) / 1000000000;

            HashMap<String, String> map = new HashMap<>();
            map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "messageID");

            if (dateDifference > 10 || !lastMessageFrom.equals(from)) {
                tts.speak(MESSAGE_START + from, TextToSpeech.QUEUE_ADD, null);
                tts.playSilence(DELAY_FROM_MESSAGE, TextToSpeech.QUEUE_ADD, null);
                tts.speak(message, TextToSpeech.QUEUE_ADD, map);
            } else {
                tts.playSilence(DELAY_FROM_MESSAGE, TextToSpeech.QUEUE_ADD, null);
                tts.speak(message, TextToSpeech.QUEUE_ADD, map);
            }
            lastMessageFrom = from;
            lastMessageDateMillis = System.nanoTime();
        }
    }

    public void speak(String message) {
        tts.setSpeechRate(0.8f);

        HashMap<String, String> map = new HashMap<>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "messageID");

        tts.speak(message, TextToSpeech.QUEUE_ADD, map);
        tts.playSilence(DELAY_FROM_MESSAGE, TextToSpeech.QUEUE_ADD, null);
    }
}

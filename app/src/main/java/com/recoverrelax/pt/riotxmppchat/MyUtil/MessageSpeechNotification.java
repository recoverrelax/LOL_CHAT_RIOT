package com.recoverrelax.pt.riotxmppchat.MyUtil;


import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

import com.recoverrelax.pt.riotxmppchat.MyUtil.AppUtils.AppMiscUtils;

import java.util.HashMap;
import java.util.Locale;

import static com.recoverrelax.pt.riotxmppchat.MyUtil.google.LogUtils.LOGE;
import static com.recoverrelax.pt.riotxmppchat.MyUtil.google.LogUtils.LOGI;

public class MessageSpeechNotification implements TextToSpeech.OnInitListener {

    private Context context;
    private static MessageSpeechNotification sInstance;
    private TextToSpeech tts;
    private final String TAG = "MessageSpeechNotification";
    private String message;
    private String from;

    private static final String MESSAGE_START = "New message from: ";
    private static final long DELAY_FROM_MESSAGE = 500;

    private boolean isReady = false;
    private long lastMessageDateMillis = 0;
    private String lastMessageFrom = null;

    /**
     * <p>Initialize the {@link MessageSpeechNotification} singleton</p>
     */
    public static synchronized void init(Context ctx){
        if (sInstance == null) {
            synchronized (MessageSpeechNotification.class) {
                if (sInstance == null) {
                    sInstance = new MessageSpeechNotification(ctx);
                }
            }
        }
    }

    private MessageSpeechNotification(Context context) {
        this.context = context;
    }

    /**
     * Returns singleton class sInstance
     **/
    public static synchronized MessageSpeechNotification getInstance() {
        if(sInstance ==null)
            throw new NullPointerException("You must initialized first");
        return sInstance;
    }

    public void getReady(){
        tts = new TextToSpeech(context, this);
    }

    public void sendMessageSpeechNotification(String message, String from){
        if(!AppMiscUtils.isAppSilenced(context)) {
            if (isReady) {
                speak(from, message);
            } else {
                getReady();
                this.message = message;
                this.from = from;
            }
        }
    }

    public void sendStatusSpeechNotification(String message) {
        if (!AppMiscUtils.isAppSilenced(context)) {
            if (isReady) {
                speak(message);
            } else {
                getReady();
                this.message = message;
            }
        }
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            tts.setLanguage(Locale.US);
            tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override public void onStart(String s) { }
                @Override public void onDone(String s) { }
                @Override public void onError(String s) { }
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
        tts.setSpeechRate(1.0f);

        HashMap<String, String> map = new HashMap<>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "messageID");

        tts.speak(message, TextToSpeech.QUEUE_ADD, map);
        tts.playSilence(DELAY_FROM_MESSAGE, TextToSpeech.QUEUE_ADD, null);
    }
}

package com.recoverrelax.pt.riotxmppchat.MyUtil;


import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static com.recoverrelax.pt.riotxmppchat.MyUtil.google.LogUtils.LOGE;
import static com.recoverrelax.pt.riotxmppchat.MyUtil.google.LogUtils.LOGI;

public class NewMessageSpeechNotification implements TextToSpeech.OnInitListener {

    private Context context;
    private static NewMessageSpeechNotification sInstance;
    private TextToSpeech tts;
    private final String TAG = this.getClass().getSimpleName();
    private String message;
    private String from;

    private static final String MESSAGE_START = "New message from: ";

    /**
     *
     */

    private String lastMessageFrom = null;
    private long lastMessageDateMilis = 0;

    /**
     * <p>Initialize the {@link NewMessageSpeechNotification} singleton</p>
     */
    public static synchronized void init(Context ctx){
        if (sInstance == null) {
            synchronized (NewMessageSpeechNotification.class) {
                if (sInstance == null) {
                    sInstance = new NewMessageSpeechNotification(ctx);
                }
            }
        }
    }

    private NewMessageSpeechNotification(Context context) {
        this.context = context;
    }

    /**
     * Returns singleton class sInstance
     **/
    public static synchronized NewMessageSpeechNotification getInstance() {
        if(sInstance ==null)
            throw new NullPointerException("You must initialized first");
        return sInstance;
    }

    public void sendSpeechNotification(String message, String from){
        tts = new TextToSpeech(context, this);
        tts.setLanguage(Locale.US);
        tts.setSpeechRate(0.8f);
        tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String s) {

            }

            @Override
            public void onDone(String s) {
                lastMessageDateMilis = System.currentTimeMillis();
                lastMessageFrom = from;
                tts.shutdown();

            }

            @Override
            public void onError(String s) {

            }
        });
        this.message = message;
        this.from = from;
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                LOGE(TAG, "This Language is not supported");
            } else {
                speakOut();
            }

        } else {
            LOGE(TAG, "Initilization Failed!");
        }
    }

    public void speakOut(){
        long lastMessageMilis = TimeUnit.SECONDS.toSeconds((System.currentTimeMillis() - lastMessageDateMilis));
        LOGI("123", String.valueOf(lastMessageMilis));

        if(lastMessageFrom != null && lastMessageFrom.equals(from) && lastMessageMilis < 3){
            tts.speak(message, TextToSpeech.QUEUE_ADD, null);
        }else{
            tts.speak(MESSAGE_START + from + " " + message, TextToSpeech.QUEUE_ADD, null);
        }
    }
}

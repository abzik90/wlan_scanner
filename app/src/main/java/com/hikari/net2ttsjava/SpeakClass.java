package com.hikari.net2ttsjava;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.TextView;

import java.util.Locale;
import java.util.regex.Pattern;


public class SpeakClass {
    TextToSpeech mTts;
    TextView messageFromUser;
    Context context;

    Locale localeRus = new Locale("ru");
    Locale localeEn = new Locale("en");

    public SpeakClass(Context context, TextToSpeech mTts, TextView messageFromUser){
        this.context = context;
        this.mTts = mTts;
        this.messageFromUser = messageFromUser;
    }

    public void speak(String message){
        message = message.replace("HTTP/1.1" , "");
        String[] splitted = message.split("\\s+");
        boolean prevCyrillic = true;
        int result = 0;

        for(int i = 0; i < splitted.length; i++){
            //TODO: edit this regex
            if(Pattern.matches("[0-9]+", splitted[i]))
                if (!prevCyrillic) result = mTts.setLanguage(localeEn);
                else result = mTts.setLanguage(localeRus);
            if (Pattern.matches("^[a-zA-Z]*$",splitted[i])){
                prevCyrillic = false;
                result = mTts.setLanguage(localeEn);
            }else {
                prevCyrillic = true;
                result = mTts.setLanguage(localeRus);
            }

            if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED)
                Log.d("TTS", "Language isn't supported");

            mTts.speak(splitted[i], TextToSpeech.QUEUE_ADD,null);
        }

        String finalMessage = message;
        messageFromUser.post(() -> messageFromUser.setText("Current message: " + finalMessage));
    }
    public void stopSpeaking(String stopCode){
        stopCode = stopCode.replace("HTTP/1.1" , "");
        if(stopCode.equals("true")) mTts.stop();
    }
}

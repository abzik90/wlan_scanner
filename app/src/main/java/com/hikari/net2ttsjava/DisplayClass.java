package com.hikari.net2ttsjava;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class DisplayClass {
    WebView webView;

    public DisplayClass(WebView webView){
        this.webView = webView;
    }
    public void display(String toDisplay){
        toDisplay = toDisplay.replace("HTTP/1.1" , "");
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                toDisplay = URLDecoder.decode(toDisplay, StandardCharsets.UTF_8.name());
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String finalToDisplay = toDisplay;
        webView.post(() -> {
            WebViewClient viewClient = new WebViewClient(){
                @Override
                public boolean shouldOverrideUrlLoading(WebView newView, String url){
                    newView.loadUrl(url);
                    return true;
                }

                @TargetApi(Build.VERSION_CODES.N) @Override
                public boolean shouldOverrideUrlLoading(WebView newView, WebResourceRequest request){
                    webView.loadUrl(request.getUrl().toString());
                    return true;
                }
            };

            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setUserAgentString("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/101.0.4951.67 Safari/537.36");
            webView.loadData(finalToDisplay, "text/html; charset=utf-8", "UTF-8");
            webView.setWebViewClient(viewClient);
        });
        Log.d("HTML", toDisplay);
    }
}

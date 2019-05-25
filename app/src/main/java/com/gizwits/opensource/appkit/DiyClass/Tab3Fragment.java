package com.gizwits.opensource.appkit.DiyClass;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.gizwits.opensource.appkit.R;

public class Tab3Fragment extends android.support.v4.app.Fragment {
    private WebView webhealth;//health健康
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case 1: {
                    webViewGoBack();
                }
                break;
            }
        }
    };

    private void webViewGoBack() {
        webhealth.goBack();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_tab3,null);
        webhealth = (WebView) view.findViewById(R.id.webhealth);
        webhealth.getSettings().setJavaScriptEnabled(true);
        webhealth.setWebViewClient(new WebViewClient());
        webhealth.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((keyCode == KeyEvent.KEYCODE_BACK) && webhealth.canGoBack()) {
                    handler.sendEmptyMessage(1);
                    return true;
                }
                return false;
            }
        });
        webhealth.loadUrl("http://jiankang.cctv.com/mobile/index.shtml");//健康专栏health
        return view;
    }
}

package com.hao.group16;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;

import java.net.URL;
import java.util.List;

import okhttp3.Cookie;

public class WebViewActivity extends AppCompatActivity {

    private static String TAG = WebViewActivity.class.getName();
    WebView webView;
    public static final String WEB_VIEW_URI = "URI";
    public static final String WEB_VIEW_DISTANCE = "DISTANCE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);

        String newsId = getIntent().getStringExtra(WEB_VIEW_URI);
        String distance = getIntent().getStringExtra(WEB_VIEW_DISTANCE);

        //webView.loadUrl(RestAPI.baseURI + "news/" + newsId + "/" + distance);
        String url;

        if (MainActivity.mUserDetail.getId().compareTo("-1") ==0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(WebViewActivity.this);
            builder.setTitle("Login required")
                    .setMessage("Please log in to read detail.")
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //nothing
                        }
                    })
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            finish();
                        }
                    })
                    .create().show();
        } else {
            url = RestAPI.baseURI + "news/" + newsId + "/" + distance + "/" + MainActivity.mUserDetail.getId();
            syncCookies(getApplicationContext(), url);
            webView.loadUrl(url);

            webView.requestFocus();
        }

    }
    @SuppressWarnings("deprecation")
    public void syncCookies(Context context, String url) {
        CookieSyncManager.createInstance(context);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);

        cookieManager.removeAllCookie();

        if (null == MainActivity.sharedPrefsCookiePersistor) {
            return;
        }
        List<Cookie> cookies = MainActivity.sharedPrefsCookiePersistor.loadAll();



        StringBuffer sb = new StringBuffer();


        for ( Cookie cookie : cookies)
        {

            String cookieName = cookie.name();
            String cookieValue = cookie.value();
            if (!TextUtils.isEmpty(cookieName)
                    && !TextUtils.isEmpty(cookieValue)) {
                sb.append(cookieName + "=");
                sb.append(cookieValue + ";");
            }
        }


        String[] cookie = sb.toString().split(";");
        for (int i = 0; i < cookie.length; i++) {
            Log.d(TAG, cookie[i]);
            cookieManager.setCookie(url, cookie[i]);// cookies是在HttpClient中获得的cookie
        }


        CookieSyncManager.getInstance().sync();
    }

}

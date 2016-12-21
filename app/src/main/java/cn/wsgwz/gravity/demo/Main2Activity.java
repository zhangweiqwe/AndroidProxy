package cn.wsgwz.gravity.demo;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import cn.wsgwz.gravity.R;
import cn.wsgwz.gravity.util.JavaScriptInterface;

public class Main2Activity extends Activity {
    private WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new JavaScriptInterface(this),"android");
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("file:///android_asset/htmlTestJs.html");


    }

}

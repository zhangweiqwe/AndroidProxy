package cn.wsgwz.gravity.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import cn.wsgwz.gravity.R;

public class HelperActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_explain);
        ActionBar actionBar = getActionBar();
        actionBar.setTitle("help");
        WebView webView = (WebView)findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        //webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("file:///android_asset/explain.html");
        webView.getSettings().setDefaultTextEncodingName("utf-8") ;
        webView.setBackgroundColor(0);
    }
}

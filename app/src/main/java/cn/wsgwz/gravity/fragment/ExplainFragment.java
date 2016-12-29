package cn.wsgwz.gravity.fragment;

import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.example.pull.refreshview.XListView;
import com.example.pull.refreshview.XScrollView;

import cn.wsgwz.gravity.R;
import cn.wsgwz.gravity.util.LogUtil;

public class ExplainFragment extends Fragment {
    private WebView webView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_explain, container, false);
        webView = (WebView) view.findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("file:///android_asset/explain.html");
        webView.setBackgroundColor(0);
        //LogUtil.printSS("  ExplainFragment ");
        //webView.getBackground().setAlpha(0);
        return view;
    }

}

package cn.wsgwz.gravity.util;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

/**
 * Created by Jeremy Wang on 2016/12/8.
 */

public class JavaScriptInterface {
   private Context context ;

    public JavaScriptInterface(Context context) {
        this.context = context;
    }
    @JavascriptInterface
    public boolean showToast(String str){
        Toast.makeText(context,str+"",Toast.LENGTH_SHORT).show();

        return true;
    }
}

package cn.wsgwz.gravity.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import cn.wsgwz.gravity.R;
import cn.wsgwz.gravity.util.FileUtil;

public class ConfigEditActivity extends Activity {



    public static final String INTENT_BUNDLE_TYPE_FILE_KEY = "INTENT_BUNDLE_TYPE_FILE_KEY";
    public static final String  INTENT_BUNDLE_TYPE_CREATE_NEW_KEY = "INTENT_BUNDLE_TYPE_CREATE_NEW_KEY";
    private File file;
    private EditText edit_ET;
    private String bufferStr;
    private boolean isCreateNew;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_edit);
        initIntentData();
        initView();
    }
    private void initView(){
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        edit_ET = (EditText) findViewById(R.id.edit_ET);
        if(!isCreateNew){
            try {
                bufferStr = FileUtil.getString(file).toString();
                edit_ET.setText(bufferStr);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            edit_ET.setText(templateStr);
            bufferStr = templateStr;
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_config,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.save_change:
                try {
                    FileUtil.saveStr(file,edit_ET.getText().toString());
                    bufferStr = edit_ET.getText().toString();
                    Snackbar.make(edit_ET,getString(R.string.already_save_change),Snackbar.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.save_change_exit:
                try {
                    FileUtil.saveStr(file,edit_ET.getText().toString());
                    //bufferStr = edit_ET.getText().toString();
                    Snackbar.make(edit_ET,getString(R.string.already_save_change),Snackbar.LENGTH_SHORT).show();
                    finish();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.give_up_change:
                edit_ET.setText(bufferStr);
                Toast.makeText(this,getString(R.string.already_give_up_change),Toast.LENGTH_SHORT).show();
                break;
            case R.id.exit:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initIntentData(){
        Intent intent  = getIntent();
        if(intent!=null){
            Bundle bundle = intent.getExtras();
            if(bundle!=null){
                Object object = bundle.getSerializable(INTENT_BUNDLE_TYPE_FILE_KEY);
                isCreateNew = bundle.getBoolean(INTENT_BUNDLE_TYPE_CREATE_NEW_KEY,false);
                if(object!=null){
                    if(object instanceof File){
                        file = (File)object;
                        getActionBar().setTitle(file.getName());
                    }
                }
            }
        }
    }
    private static final String templateStr ="<config version=\"2.0\"  apn_apn=\"cmwap\" apn_proxy=\"10.0.0.172\" apn_port=\"80\">\n" +
            "\n" +
            "    <http host=\"10.0.0.172\" port=\"80\">\n" +
            "        <delate>host</delate>\n" +
            "        <first-line>\n" +
            "            [method][tab] [url][tab] [version]\\r\\n\n" +
            "            Host: [tab][host]\\r\\n\n" +
            "        </first-line>\n" +
            "    </http>\n" +
            "\n" +
            "\n" +
            "    <https host=\"10.0.0.172\" port=\"80\" switch=\"on\">\n" +
            "        <delate>host</delate>\n" +
            "        <first-line>\n" +
            "            [method][tab] /[tab] [version]\\r\\n\n" +
            "            Host: [tab][host]:443\\r\\n\n" +
            "        </first-line>\n" +
            "    </https>\n" +
            "\n" +
            "</config>";
}

package cn.wsgwz.gravity.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pull.refreshview.XListView;

import java.lang.reflect.Array;

import cn.wsgwz.gravity.MainActivity;
import cn.wsgwz.gravity.R;
import cn.wsgwz.gravity.adapter.PackageInstallPermissionAdapter;
import cn.wsgwz.gravity.util.LogUtil;
import cn.wsgwz.gravity.util.OnExecResultListenner;
import cn.wsgwz.gravity.util.ShellUtil;
import cn.wsgwz.gravity.view.MyRecyclerView;

public class PackageInstallActivity extends Activity implements View.OnClickListener{

    private String apkPath ;

    private ImageView icon;
    private TextView package_name_TV;
    private MyRecyclerView recyclerView;
    private Button cancel,install;

    private PackageInstallPermissionAdapter installPermissionAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package_install);
        Intent intent = getIntent();
        if(intent!=null){
            Uri uri = intent.getData();
            if(uri!=null){
                apkPath = uri.getPath();
            }
        }

        icon = (ImageView) findViewById(R.id.icon);
        package_name_TV = (TextView) findViewById(R.id.package_name_TV);
        recyclerView = (MyRecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        cancel = (Button) findViewById(R.id.cancel);
        install = (Button) findViewById(R.id.install);
        cancel.setOnClickListener(this);
        install.setOnClickListener(this);
        PackageManager packageManager = getPackageManager();
        PackageInfo packageInfo = packageManager.getPackageArchiveInfo(apkPath,PackageManager.GET_ACTIVITIES);
     /*   installPermissionAdapter = new PackageInstallPermissionAdapter(new PermissionInfo[]{
                new PermissionInfo(),new PermissionInfo(),new PermissionInfo(),new PermissionInfo(),new PermissionInfo(),
                new PermissionInfo(),new PermissionInfo(),new PermissionInfo(),new PermissionInfo(),new PermissionInfo(),
                new PermissionInfo(),new PermissionInfo(),new PermissionInfo(),new PermissionInfo(),new PermissionInfo()
        }
        );*/
       // LogUtil.printSS(packageInfo.permissions.toString());
        if(packageInfo!=null){
           // recyclerView.setAdapter(installPermissionAdapter);
            ApplicationInfo applicationInfo = packageInfo.applicationInfo;
            applicationInfo.sourceDir = apkPath;
            applicationInfo.publicSourceDir = apkPath;
            icon.setImageDrawable(applicationInfo.loadIcon(packageManager));
            package_name_TV.setText(applicationInfo.packageName+"\t"+packageInfo.versionName+"("+packageInfo.versionCode+")");
        }
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.cancel:
                finish();
                break;
            case R.id.install:
                if(apkPath==null){
                    return;
                }
                if(apkPath.endsWith(".apk")){
                    Toast.makeText(PackageInstallActivity.this,getString(R.string.is_doing_install_apk_please_do_not_kill),Toast.LENGTH_LONG).show();
                    ShellUtil.execShell(PackageInstallActivity.this, "pm install -r "+apkPath, new OnExecResultListenner() {
                        @Override
                        public void onSuccess(StringBuffer sb) {
                            Toast.makeText(PackageInstallActivity.this,getString(R.string.install_apk_ok),Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onError(StringBuffer sb) {
                            Toast.makeText(PackageInstallActivity.this,getString(R.string.install_apk_error),Toast.LENGTH_LONG).show();
                        }
                    });
                }
                break;
        }
    }
}

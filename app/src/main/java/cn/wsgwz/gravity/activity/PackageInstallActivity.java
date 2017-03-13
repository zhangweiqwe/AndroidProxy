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

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import cn.wsgwz.gravity.MainActivity;
import cn.wsgwz.gravity.R;
import cn.wsgwz.gravity.adapter.PackageInstallPermissionAdapter;
import cn.wsgwz.gravity.util.FileUtil;
import cn.wsgwz.gravity.util.LogUtil;
import cn.wsgwz.gravity.util.OnExecResultListenner;
import cn.wsgwz.gravity.util.ShellUtil;
import cn.wsgwz.gravity.view.MyRecyclerView;

public class PackageInstallActivity extends Activity implements View.OnClickListener{

    private String apkPath ;

    private ImageView icon;
    private TextView package_name_TV;
    private MyRecyclerView recyclerView;
    private Button install;

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
        install = (Button) findViewById(R.id.install);
        install.setOnClickListener(this);
        PackageManager packageManager = getPackageManager();

        if(apkPath==null){
            return;
        }

        PackageInfo packageInfo = packageManager.getPackageArchiveInfo(apkPath,PackageManager.GET_PERMISSIONS);
        if(packageInfo!=null){
            installPermissionAdapter = new PackageInstallPermissionAdapter(packageInfo.requestedPermissions);
            recyclerView.setAdapter(installPermissionAdapter);
            ApplicationInfo applicationInfo = packageInfo.applicationInfo;
            applicationInfo.sourceDir = apkPath;
            applicationInfo.publicSourceDir = apkPath;
            icon.setImageDrawable(applicationInfo.loadIcon(packageManager));
            package_name_TV.setText(applicationInfo.packageName+"\r\r\r"+packageInfo.versionName+"("+packageInfo.versionCode+")"+"\r\r\r\r"+packageInfo.requestedPermissions.length+"项权限");
        }



        overridePendingTransition(R.anim.dialog_pay_start_animation_set,R.anim.dialog_pay_exit_animation_set);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.install:
                if(apkPath!=null&&apkPath.endsWith(".apk")){
                    File file = new File(apkPath);
                    File releaseFileParent = new File(FileUtil.TEMP_INSTALL);
                    if(!releaseFileParent.exists()){
                        releaseFileParent.mkdirs();
                    }
                    File releaseFile = new File(releaseFileParent,"tempFile"+Math.random()*(1000)+".apk");
                    if(FileUtil.cpFile(file,releaseFile)){
                        Toast.makeText(PackageInstallActivity.this,getString(R.string.is_doing_install_apk_please_do_not_kill),Toast.LENGTH_SHORT).show();
                    ShellUtil.execShell(PackageInstallActivity.this, "pm install -r "+releaseFile.getAbsolutePath(), new OnExecResultListenner() {
                        @Override
                        public void onSuccess(StringBuffer sb) {
                            Toast.makeText(PackageInstallActivity.this,getString(R.string.install_apk_ok)+sb.toString(),Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onError(StringBuffer sb) {
                            Toast.makeText(PackageInstallActivity.this,getString(R.string.install_apk_error)+sb.toString(),Toast.LENGTH_LONG).show();
                        }
                    });
                    }
                    //LogUtil.printSS(""+apkPath+"\n--"+releaseFile.getParentFile()+"\n--"+releaseFile.getParent()+"--\n"+releaseFile.getAbsolutePath());
                    finish();
                }
                break;
        }
    }
}

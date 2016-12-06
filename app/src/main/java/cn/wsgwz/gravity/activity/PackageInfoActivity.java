package cn.wsgwz.gravity.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.wsgwz.gravity.R;
import cn.wsgwz.gravity.fragment.ExplainFragment;
import cn.wsgwz.gravity.fragment.GraspDataFragment;
import cn.wsgwz.gravity.fragment.MainFragment;
import cn.wsgwz.gravity.fragment.PackageIfoFragment;
import cn.wsgwz.gravity.util.LogUtil;
import cn.wsgwz.gravity.view.slidingTabLayout.ScreenSlidePagerAdapter;
import cn.wsgwz.gravity.view.slidingTabLayout.ScreenSlidePagerForPackageInfoAdapter;
import cn.wsgwz.gravity.view.slidingTabLayout.SlidingTabLayout;
import cn.wsgwz.gravity.view.slidingTabLayout.ViewPager;

public class PackageInfoActivity extends FragmentActivity {

    public static String  UID_STR;
    private SlidingTabLayout slidingTabLayout;
    private ViewPager my_viewPager;
    private ScreenSlidePagerForPackageInfoAdapter screenSlidePagerAdapter;

    private PackageIfoFragment packageIfoFragmentSystem = new PackageIfoFragment();
    private PackageIfoFragment packageIfoFragmentUser = new PackageIfoFragment();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UID_STR = "";
        setContentView(R.layout.activity_package_info);
        initView();
    }

    private void initView(){
        getActionBar().setDisplayHomeAsUpEnabled(true);
        slidingTabLayout = (SlidingTabLayout)findViewById(R.id.slidingTabLayout);
        my_viewPager = (ViewPager) findViewById(R.id.my_viewPager);
        screenSlidePagerAdapter = new ScreenSlidePagerForPackageInfoAdapter(getFragmentManager());
        myPackageInfo(this);
        screenSlidePagerAdapter.addTab("系统应用", packageIfoFragmentSystem);
        screenSlidePagerAdapter.addTab("用户应用", packageIfoFragmentUser);
        my_viewPager.setAdapter(screenSlidePagerAdapter);
        my_viewPager.setOffscreenPageLimit(screenSlidePagerAdapter.getCount());
        slidingTabLayout.setViewPager(my_viewPager);



    }

    private void myPackageInfo(Context context){
        List<PackageInfo> listSystem = new ArrayList<>();
        List<PackageInfo> listUser = new ArrayList<>();
        List<PackageInfo> packageInfoList = getPackageManager().getInstalledPackages(0);
        for(int i=0;i<packageInfoList.size();i++){
            PackageInfo packageInfo = packageInfoList.get(i);
            if((packageInfo.applicationInfo.flags& ApplicationInfo.FLAG_SYSTEM)>0){
                listSystem.add(packageInfo);
            }else {
                listUser.add(packageInfo);
            }
         //   LogUtil.printSS("  dsdd "+i+"   "+listSystem.size()+"  "+listUser.size());
        }
        packageIfoFragmentSystem.setInfoList2(listSystem);
        packageIfoFragmentUser.setInfoList2(listUser);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
            case R.id.other_00:
                break;
            case R.id.copy:
                ClipboardManager myClipboard;
                myClipboard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
                ClipData myClip;
                myClip = ClipData.newPlainText("uid", UID_STR);
                myClipboard.setPrimaryClip(myClip);
                Toast.makeText(this,"已复制",Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.package_info_menu,menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        UID_STR = "";
        super.onDestroy();
    }
}

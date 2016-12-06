package cn.wsgwz.gravity.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.wsgwz.gravity.R;
import cn.wsgwz.gravity.fragment.DefinedShellFragment;
import cn.wsgwz.gravity.fragment.ExplainFragment;
import cn.wsgwz.gravity.fragment.GraspDataFragment;
import cn.wsgwz.gravity.fragment.MainFragment;
import cn.wsgwz.gravity.helper.ShellHelper;
import cn.wsgwz.gravity.view.slidingTabLayout.ScreenSlidePagerAdapter;
import cn.wsgwz.gravity.view.slidingTabLayout.ScreenSlidePagerForDefinedShellAdapter;
import cn.wsgwz.gravity.view.slidingTabLayout.SlidingTabLayout;
import cn.wsgwz.gravity.view.slidingTabLayout.ViewPager;

public class DefinedShellActivity extends FragmentActivity {
    private SlidingTabLayout slidingTabLayout;
    private ViewPager my_viewPager;
    private ScreenSlidePagerForDefinedShellAdapter screenSlidePagerForDefinedShellAdapter;
    private ShellHelper shellHelper = ShellHelper.getInstance();
    private List<DefinedShellFragment> definedShellFragmentList  = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_defined_shell);
        initView();
    }
    private void initView(){
        android.app.ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        slidingTabLayout = (SlidingTabLayout)findViewById(R.id.slidingTabLayout);
        my_viewPager = (ViewPager) findViewById(R.id.my_viewPager);
        screenSlidePagerForDefinedShellAdapter = new ScreenSlidePagerForDefinedShellAdapter(getFragmentManager(),this);
        definedShellFragmentList.add(new DefinedShellFragment());
        definedShellFragmentList.add(new DefinedShellFragment());
        definedShellFragmentList.get(0).setStr(shellHelper.getStartStr());
        definedShellFragmentList.get(1).setStr(shellHelper.getStopStr());
        screenSlidePagerForDefinedShellAdapter.addTab("开启脚本", definedShellFragmentList.get(0));
        screenSlidePagerForDefinedShellAdapter.addTab("关闭脚本", definedShellFragmentList.get(1));
        my_viewPager.setAdapter(screenSlidePagerForDefinedShellAdapter);
        my_viewPager.setOffscreenPageLimit(screenSlidePagerForDefinedShellAdapter.getCount());
        slidingTabLayout.setViewPager(my_viewPager);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.restore_shell:
                shellHelper.setStartStr(shellHelper.getInitStartStr(this));
                shellHelper.setStopStr(shellHelper.getInitStopStr());
                definedShellFragmentList.get(0).getEdit_ET().setText(shellHelper.getStartStr());
                definedShellFragmentList.get(1).getEdit_ET().setText(shellHelper.getStopStr());
                SharedPreferences sharedPreferences2 = getSharedPreferences("main",MODE_PRIVATE);
                sharedPreferences2.edit().putString("start.sh", shellHelper.getStartStr()).commit();
                sharedPreferences2.edit().putString("stop.sh", shellHelper.getStopStr()).commit();
                Toast.makeText(this,"已还原",Toast.LENGTH_SHORT).show();
                break;
            case R.id.save_shell:
                shellHelper.setStartStr(definedShellFragmentList.get(0).getEdit_ET().getText().toString());
                shellHelper.setStopStr(definedShellFragmentList.get(1).getEdit_ET().getText().toString());
                SharedPreferences sharedPreferences = getSharedPreferences("main",MODE_PRIVATE);
                sharedPreferences.edit().putString("start.sh", shellHelper.getStartStr()).commit();
                sharedPreferences.edit().putString("stop.sh", shellHelper.getStopStr()).commit();
                Toast.makeText(this,"已保存",Toast.LENGTH_SHORT).show();
                break;
            case R.id.cancel_save_shell:
                definedShellFragmentList.get(0).getEdit_ET().setText(definedShellFragmentList.get(0).getStr());
                definedShellFragmentList.get(1).getEdit_ET().setText(definedShellFragmentList.get(1).getStr());
                Toast.makeText(this,"已取消",Toast.LENGTH_SHORT).show();
                break;
            case R.id.about_uid:
                startActivity(new Intent(this,PackageInfoActivity.class));
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.defined_shell_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
}

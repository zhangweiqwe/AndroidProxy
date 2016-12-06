package cn.wsgwz.gravity.activity;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;

import java.io.File;

import cn.wsgwz.gravity.R;
import cn.wsgwz.gravity.fragment.GraspDataFilePreviewParentFragment;
import cn.wsgwz.gravity.util.LogUtil;

public class GraspDataFilePreviewActivity extends FragmentActivity {
    private File file;
    public static final String BUNDLE_KRY_FILE = "BUNDLE_KRY_FILE";
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grasp_data_file_preview);
        initIntentData();
        initView();
        overridePendingTransition(R.anim.dialog_pay_start_animation_set,R.anim.dialog_pay_exit_animation_set);
    }
    private void initIntentData(){
        Intent intent = getIntent();
        if(intent!=null){
            Bundle bundle = intent.getExtras();
            if(bundle!=null){
                file = (File) bundle.getSerializable(BUNDLE_KRY_FILE);
               // LogUtil.printSS(file.getAbsolutePath());
            }
        }
    }


    private void initView(){
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        GraspDataFilePreviewParentFragment graspDataFilePreviewParentFragment = new GraspDataFilePreviewParentFragment();
        graspDataFilePreviewParentFragment.setFile(file);
        fragmentTransaction.replace(R.id.main_frame,graspDataFilePreviewParentFragment);
        fragmentTransaction.commit();
    }
}

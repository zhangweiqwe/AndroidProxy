package cn.wsgwz.photospreview;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.BaseBundle;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.wsgwz.photospreview.lib.PhotoView;

public class PhotosPreviewActivity extends Activity {
    private ArrayList<String> stringList;
    private ArrayList<File> fileList;
    private int currentNumber;
    public static final String BUNDLE_KEY_LIST = "BUNDLE_KEY_LIST";
    public static final String BUNDLE_KEY_CURRENT_NUMBER = "BUNDLE_KEY_CURRENT_NUMBER";
    private void initIntentData(){
        Intent intent = getIntent();
        if(intent!=null){
            Bundle bundle = intent.getExtras();
            if(bundle!=null){
                currentNumber = bundle.getInt(BUNDLE_KEY_CURRENT_NUMBER);
                Object obj = bundle.getSerializable(BUNDLE_KEY_LIST);
                if(obj!=null){
                    ArrayList<Object> tempList = (ArrayList<Object>) obj;
                    if(tempList.get(0) instanceof String){
                        stringList = (ArrayList<String>) obj;
                    }else{
                        fileList = (ArrayList<File>) obj;
                    }
                }else {
                    noCodeInitIntent();
                }
            }else {
              noCodeInitIntent();
            }
        }
    }
    private ViewPager viewPager;
    private ProgressBar progressBar;
    private TextView hintCurrentPage;
   // private int[] imgsId = new int[]{R.mipmap.aaa, R.mipmap.bbb, R.mipmap.ccc, R.mipmap.ddd};
    private void noCodeInitIntent(){
        stringList = new ArrayList<>();
        stringList.add("http://img.pconline.com.cn/images/upload/upc/tx/photoblog/1112/01/c5/9807116_9807116_1322714493296.jpg");
        stringList.add("http://cyjctrip.qiniudn.com/1384128078/EAD72B4A-734B-4ABD-B3F1-92AA4BA2E993.jpg");
        stringList.add("http://images.ccoo.cn/bbs/2011105/201110522020246.jpg");
        stringList.add("http://img5.duitang.com/uploads/item/201411/09/20141109001647_ELGk2.jpeg");
        stringList.add("http://s11.sinaimg.cn/mw690/d83fda66tx6DCfGOUEqda&690");
        stringList.add("http://img5.imgtn.bdimg.com/it/u=2429826194,3609010411&fm=23&gp=0.jpg");
        stringList.add("http://img.taopic.com/uploads/allimg/130410/240403-130410063T440.jpg");
        stringList.add("http://img1.imgtn.bdimg.com/it/u=2340246007,1647475638&fm=23&gp=0.jpg");
        stringList.add("http://img1.imgtn.bdimg.com/it/u=2102041190,449921621&fm=214&gp=0.jpg");
        currentNumber = 0;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_photos_preview);
        initIntentData();
        initView();
    }
    private void initView(){
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        hintCurrentPage = (TextView) findViewById(R.id.hintCurrentPage);
        hintCurrentPage.setText(currentNumber+1+"/"+(stringList==null?fileList.size():stringList.size()));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                hintCurrentPage.setText(position+1+"/"+(stringList==null?fileList.size():stringList.size()));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return (stringList==null?fileList.size():stringList.size());
            }
            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public int getItemPosition(Object object) {
                return super.getItemPosition(object);
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                final PhotoView view = new PhotoView(PhotosPreviewActivity.this);
                view.enable();
                view.setScaleType(ImageView.ScaleType.FIT_CENTER);
                container.addView(view);
                progressBar.setVisibility(View.VISIBLE);
                if((stringList==null?false:true)){
                    Picasso.with(PhotosPreviewActivity.this).load(stringList.get(position))
                            .config(Bitmap.Config.ARGB_8888).placeholder(new ColorDrawable(Color.parseColor("#00000000"))).error(R.mipmap.error).into(view,
                            new Callback() {
                                @Override
                                public void onSuccess() {
                                    progressBar.setVisibility(View.GONE);
                                }

                                @Override
                                public void onError() {
                                    progressBar.setVisibility(View.GONE);
                                }

                            }
                    )
                    ;

                }else {
                    Picasso.with(PhotosPreviewActivity.this).load(fileList.get(position))
                            .config(Bitmap.Config.ARGB_8888).placeholder(new ColorDrawable(Color.parseColor("#ffffff"))).error(R.mipmap.error).into(view);
                }


                return view;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }
        });
        viewPager.setCurrentItem(currentNumber);



    }
}

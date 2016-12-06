package cn.wsgwz.gravity.fragment;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import java.io.File;

import cn.wsgwz.gravity.R;
import cn.wsgwz.gravity.helper.GraspDataFileHelper;
import cn.wsgwz.gravity.util.LogUtil;
import cn.wsgwz.gravity.view.slidingTabLayout.ScreenSlidePagerAdapter;
import cn.wsgwz.gravity.view.slidingTabLayout.ScreenSlidePagerForGraspPreviewAdapter;
import cn.wsgwz.gravity.view.slidingTabLayout.SlidingTabLayout;
import cn.wsgwz.gravity.view.slidingTabLayout.ViewPager;


public class GraspDataFilePreviewParentFragment extends Fragment {

    private SlidingTabLayout slidingTabLayout;
    private ViewPager my_viewPager;
    private ScreenSlidePagerForGraspPreviewAdapter screenSlidePagerForGraspPreviewAdapter;

    private File file;
    private GraspDataFileHelper graspDataFileHelper = GraspDataFileHelper.getInstance();
    public static final int PAGE_MAX_NUMBER = 10240;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grasp_data_file_preview_parent, container, false);
        initView(view );
        beginningRead();
        return view;
    }
    private void initView(View view){
        slidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.slidingTabLayout);
        my_viewPager = (ViewPager) view.findViewById(R.id.my_viewPager);
        screenSlidePagerForGraspPreviewAdapter = new ScreenSlidePagerForGraspPreviewAdapter(getActivity().getFragmentManager(),getActivity());
        //my_viewPager.setOffscreenPageLimit(screenSlidePagerAdapter.getCount());
        my_viewPager.setAdapter(screenSlidePagerForGraspPreviewAdapter);
        slidingTabLayout.setViewPager(my_viewPager);


        slidingTabLayout.postDelayed(new Runnable() {
            @Override
            public void run() {

            }
        },1000);
    }

    private void beginningRead(){
        if(file==null){
            return;
        }


        graspDataFileHelper.getString(file, new GraspDataFileHelper.OnReponseListenner() {
            @Override
            public void begin(long length) {

            }

            @Override
            public void progress(long length, StringBuffer sb) {
            }

            @Override
            public void success(StringBuffer sb) {
                if(sb!=null){
                    int length = sb.length();
                    if(sb.length()==0){
                        return;
                    }

                    int count = 0;
                    int tempLen = 0;
                    int curenntLen = 0;
                    while ((curenntLen)<length){
                        count++;
                        tempLen = curenntLen;
                        curenntLen +=PAGE_MAX_NUMBER;
                        if(curenntLen>length){
                            curenntLen = length;
                        }
                        screenSlidePagerForGraspPreviewAdapter.addItemContentStr(sb.substring(tempLen,curenntLen));
                        screenSlidePagerForGraspPreviewAdapter.addTab("第 "+count+" 页", GraspDataFilePreviewFragment.class);

                    }
                    screenSlidePagerForGraspPreviewAdapter.notifyDataSetChanged();
                    slidingTabLayout.setViewPager(my_viewPager);
                }
            }

            @Override
            public void error(String errorStr) {

            }
        });
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}

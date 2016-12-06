package cn.wsgwz.gravity.fragment;

import android.app.Fragment;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import cn.wsgwz.gravity.R;
import cn.wsgwz.gravity.adapter.PackageInfoAdapter;
import cn.wsgwz.gravity.util.LogUtil;

public class PackageIfoFragment extends Fragment implements ListView.OnItemClickListener{
    private ListView pakageInfo_List;
    private PackageInfoAdapter packageInfoAdapter;
    private List<PackageInfo> infoList = new ArrayList<>();
    private List<PackageInfo> infoList2 ;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_package_ifo, container, false);
        initView(view);
        return view;
    }
    private void initView(View view){
        pakageInfo_List = (ListView)view.findViewById(R.id.pakageInfo_List);
        if(infoList2!=null){
            infoList.addAll(infoList2);
        }
        packageInfoAdapter = new PackageInfoAdapter(infoList,getActivity());
        pakageInfo_List.setAdapter(packageInfoAdapter);
        pakageInfo_List.setOnItemClickListener(this);
       // setListViewHeight(pakageInfo_List);
    }

    public void setInfoList2(List<PackageInfo> infoList2) {
        this.infoList2 = infoList2;
    }
    private void setListViewHeight(ListView listView){
        if(listView==null){
            return;
        }
        ListAdapter listAdapter =  listView.getAdapter();
        if(listAdapter==null){
            return;
        }
        int totalHeight = 0;
        for(int i=0;i<listAdapter.getCount();i++){
            View v = listAdapter.getView(i,null,listView);
            v.measure(0,0);
            totalHeight += v.getMeasuredHeight();
        }

        listView.getLayoutParams().height = (listView.getDividerHeight()*(listAdapter.getCount()-1)+totalHeight);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        CheckBox ck =  ((CheckBox)(view.findViewById(R.id.checkbox_CB)));
        if(ck.getTag()==null){
            ck.setTag(true);
            ck.setChecked(true);
        }else {
            boolean b = (boolean) ck.getTag();
            if(b){
                ck.setTag(false);
                ck.setChecked(false);
            }else {
                ck.setTag(true);
                ck.setChecked(true);
            }
        }
    }
}

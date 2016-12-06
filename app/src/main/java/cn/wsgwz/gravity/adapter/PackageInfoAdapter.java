package cn.wsgwz.gravity.adapter;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.wsgwz.gravity.R;
import cn.wsgwz.gravity.activity.PackageInfoActivity;
import cn.wsgwz.gravity.util.LogUtil;

/**
 * Created by Administrator on 2016/10/29.
 */

public class PackageInfoAdapter extends BaseAdapter {
    private List<PackageInfo> infoList;
    private List<Boolean> booleanList;
    private Context context;
    private LayoutInflater inflater;

    public PackageInfoAdapter(List<PackageInfo> infoList, Context context) {
        this.infoList = infoList;
        this.context = context;
        inflater = LayoutInflater.from(context);
        booleanList = new ArrayList<>();
        if(infoList!=null){
            for(int i=0;i<infoList.size();i++){
                booleanList.add(false);
            }
        }

       // LogUtil.printSS("  "+infoList.size()+"   "+booleanList.size());
    }

    @Override
    public int getCount() {
        return infoList==null?0:infoList.size();
    }

    @Override
    public Object getItem(int i) {
        return infoList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if(view==null){
            view = inflater.inflate(R.layout.fragment_package_ifo_list_everyone,viewGroup,false);
            viewHolder  = new ViewHolder();
            viewHolder.logo_IV = (ImageView) view.findViewById(R.id.logo_IV);
            viewHolder.appName_TV = (TextView)view.findViewById(R.id.appName_TV);
            viewHolder.uid_TV = (TextView)view.findViewById(R.id.uid_TV);
            viewHolder.packageName_TV = (TextView)view.findViewById(R.id.packageName_TV);

            viewHolder.checkbox_CB = (CheckBox) view.findViewById(R.id.checkbox_CB);


            view.setTag(viewHolder);

        }else {
            viewHolder = (ViewHolder) view.getTag();
        }
        initData(i,view,viewGroup,viewHolder);
        return view;
    }
    /* 备注：
    通过 PackageInfo  获取具体信息方法：
    包名获取方法：packageInfo.packageName
    icon获取获取方法：packageManager.getApplicationIcon(applicationInfo)
    应用名称获取方法：packageManager.getApplicationLabel(applicationInfo)
    使用权限获取方法：packageManager.getPackageInfo(packageName,PackageManager.GET_PERMISSIONS).requestedPermissions
    通过 ResolveInfo 获取具体信息方法：
    包名获取方法：resolve.activityInfo.packageName
    icon获取获取方法：resolve.loadIcon(packageManager)
    应用名称获取方法：resolve.loadLabel(packageManager).toString()
 */
    private void initData(final int i, View view, ViewGroup viewGroup, ViewHolder viewHolder){
        final PackageInfo packageInfo = infoList.get(i);
        PackageManager packageManager = context.getPackageManager();
        viewHolder.logo_IV.setImageDrawable(packageManager.getApplicationIcon(packageInfo.applicationInfo));

        viewHolder.appName_TV.setText(packageManager.getApplicationLabel(packageInfo.applicationInfo));
        viewHolder.uid_TV.setText(packageInfo.applicationInfo.uid+"");
        viewHolder.packageName_TV.setText(packageInfo.packageName);


        viewHolder.checkbox_CB.setOnCheckedChangeListener(null);
        viewHolder.checkbox_CB.setChecked(booleanList.get(i));
        viewHolder.checkbox_CB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    PackageInfoActivity.UID_STR = (PackageInfoActivity.UID_STR+" "+packageInfo.applicationInfo.uid).trim();
                }else {
                    PackageInfoActivity.UID_STR = PackageInfoActivity.UID_STR.replace(packageInfo.applicationInfo.uid+"","").trim();
                }
                booleanList.set(i,b);
               // LogUtil.printSS("-----"+i+"        "+b);
            }
        });
    }
    private class ViewHolder {
        private ImageView logo_IV;
        private TextView appName_TV, uid_TV, packageName_TV;
        private CheckBox checkbox_CB;
    }
}

package cn.wsgwz.gravity.adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import cn.wsgwz.gravity.R;
import cn.wsgwz.gravity.util.LogUtil;

/**
 * Created by Jeremy Wang on 2017/1/17.
 */

public class CapturePackageELVAdapter extends BaseExpandableListAdapter {
    private List<StringBuffer> list;
    private Context context;

    public CapturePackageELVAdapter(Context context, List<StringBuffer> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getGroupCount() {
        return list==null?0:list.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return 1;
    }

    @Override
    public Object getGroup(int i) {
        return getFirstLine(list.get(i));
    }

    @Override
    public Object getChild(int i, int i1) {
        return list.get(i);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }
    //  按函数的名字来理解应该是是否具有稳定的id，这个方法目前一直都是返回false，没有去改动过
    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        GroupViewHolder viewHolder = null;
        if(view==null){
            viewHolder = new GroupViewHolder();
            TextView tV = new TextView(context);
            tV.setTextSize(10);
            tV.setSingleLine();
            tV.setEllipsize(TextUtils.TruncateAt.END);
            tV.setTextColor(context.getResources().getColor(R.color.capture_tv_droup));
            viewHolder.tV = (TextView) (view = tV);
            view.setTag(viewHolder);
        }else {
            viewHolder = (GroupViewHolder) view.getTag();
        }
        viewHolder.tV.setText(((String)getGroup(i)).toString());
        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        ChildViewHolder viewHolder = null;
        if(view==null){
            viewHolder = new ChildViewHolder();
            TextView tV = new TextView(context);
            tV.setTextSize(10);
            tV.setTextColor(context.getResources().getColor(R.color.capture_tv_child));
            viewHolder.tV = (TextView) (view = tV);
            view.setTag(viewHolder);
        }else {
            viewHolder = (ChildViewHolder) view.getTag();
        }
        viewHolder.tV.setText(((StringBuffer)getChild(i,i1)).toString());
        return view;
    }
    private class GroupViewHolder{
        private TextView tV;
    }
    private class ChildViewHolder{
        private TextView tV;
    }


    //  子项是否可选中，如果需要设置子项的点击事件，需要返回true
    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
    private String getFirstLine(StringBuffer sb){
        return sb.substring(0,sb.indexOf("\r\n"));
    }


}

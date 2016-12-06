package cn.wsgwz.gravity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import cn.wsgwz.gravity.R;

/**
 * Created by Jeremy Wang on 2016/10/26.
 */

public class GraspDataAdapter extends BaseAdapter {
    private List<File> list;
    private LayoutInflater inflater;
    private Context context;

    public GraspDataAdapter(List<File> list, Context context) {
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list==null?0:list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(convertView==null){
            convertView = inflater.inflate(R.layout.fragment_grasp_data_listview_everyone,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.time_TV = (TextView) convertView.findViewById(R.id.time_TV);
            viewHolder.size_TV = (TextView) convertView.findViewById(R.id.size_TV);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        File file = list.get(position);
        viewHolder.time_TV.setText(file.getName());
        viewHolder.size_TV.setText(((int)(file.length()/1024)+1)+"KB");
        return convertView;
    }
    private class ViewHolder{
        private TextView time_TV,size_TV;
    }
}

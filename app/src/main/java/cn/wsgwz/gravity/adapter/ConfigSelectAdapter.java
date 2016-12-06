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
 * Created by Jeremy Wang on 2016/11/7.
 */

public class ConfigSelectAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Context context;
    private List<File> fileList;

    public ConfigSelectAdapter(Context context, List<File> fileList) {
        this.context = context;
        this.fileList = fileList;
        inflater  = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return fileList==null?0:fileList.size();
    }

    @Override
    public Object getItem(int position) {
        return fileList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(convertView==null){
            convertView = inflater.inflate(R.layout.dialog_config_select_listview_every_one,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.fileName_TV = (TextView) convertView.findViewById(R.id.fileName_TV);
            viewHolder.filePath_TV = (TextView) convertView.findViewById(R.id.filePath_TV);
            viewHolder.hint_TV = (TextView) convertView.findViewById(R.id.hint_TV);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        initView(position,convertView,parent,viewHolder);
        return convertView;
    }
    private void initView(int position, View convertView, ViewGroup parent,ViewHolder viewHolder){
        File file = fileList.get(position);
        viewHolder.fileName_TV.setText(file.getName());
        viewHolder.filePath_TV.setText(file.getAbsolutePath());
        viewHolder.hint_TV.setText(position+".");
    }
    private class ViewHolder{
        private TextView fileName_TV,filePath_TV,hint_TV;
    }

}

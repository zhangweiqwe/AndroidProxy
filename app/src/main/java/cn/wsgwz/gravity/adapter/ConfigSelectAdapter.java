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
import cn.wsgwz.gravity.config.EnumAssetsConfig;

/**
 * Created by Jeremy Wang on 2016/11/7.
 */

public class ConfigSelectAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Context context;
    private List<Object> list;

    public ConfigSelectAdapter(Context context, List<Object> list) {
        this.context = context;
        this.list = list;
        inflater  = LayoutInflater.from(context);
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

        Object obj = list.get(position);
        if(obj==null){
            return;
        }
        if(obj instanceof File){
            File file = (File) obj;
            viewHolder.fileName_TV.setText(file.getName());
            viewHolder.filePath_TV.setText(file.getAbsolutePath());
        }else if(obj instanceof EnumAssetsConfig){
            EnumAssetsConfig enumAssetsConfig = (EnumAssetsConfig) obj;
            viewHolder.fileName_TV.setText(enumAssetsConfig.getValues());
            viewHolder.filePath_TV.setText(enumAssetsConfig.getKey());
        }
        viewHolder.hint_TV.setText(position+".");
    }
    private class ViewHolder{
        private TextView fileName_TV,filePath_TV,hint_TV;
    }

}

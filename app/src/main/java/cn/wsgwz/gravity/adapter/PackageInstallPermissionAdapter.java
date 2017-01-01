package cn.wsgwz.gravity.adapter;

import android.content.pm.PermissionInfo;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import cn.wsgwz.gravity.R;
import cn.wsgwz.gravity.util.LogUtil;

/**
 * Created by Administrator on 2017/1/1.
 */

public class PackageInstallPermissionAdapter extends RecyclerView.Adapter<PackageInstallPermissionAdapter.ViewHolder>{
    private PermissionInfo[] permissionInfos;

    public PackageInstallPermissionAdapter(PermissionInfo[] permissionInfos) {
        this.permissionInfos = permissionInfos;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_package_install_recycleview_permission_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.aSwitch.setText(permissionInfos[position].toString());
    }

    @Override
    public int getItemCount() {
        return permissionInfos==null?0:permissionInfos.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private Switch aSwitch;
        public ViewHolder(View itemView) {
            super(itemView);
            aSwitch = (Switch) itemView;
        }
    }
}

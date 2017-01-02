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
    private String[] permissions;

    public PackageInstallPermissionAdapter(String[] permissions) {
        this.permissions = permissions;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_package_install_recycleview_permission_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //  PermissionInfo permissionInfo = pm.getPermissionInfo(permName, 0);
        holder.aSwitch.setText(permissions[position].toString());
    }

    @Override
    public int getItemCount() {
        return permissions==null?0:permissions.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private Switch aSwitch;
        public ViewHolder(View itemView) {
            super(itemView);
            aSwitch = (Switch) itemView;
        }
    }
}

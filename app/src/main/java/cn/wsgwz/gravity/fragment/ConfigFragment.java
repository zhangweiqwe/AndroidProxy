package cn.wsgwz.gravity.fragment;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import cn.wsgwz.gravity.MainActivity;
import cn.wsgwz.gravity.R;
import cn.wsgwz.gravity.config.Config;
import cn.wsgwz.gravity.util.FileUtil;
import cn.wsgwz.gravity.util.LogUtil;


public class ConfigFragment extends Fragment {

    private TextView hint_TV;
    private EditText edit_ET;
    private Button select_Bn,new_Bn,delate_Bn,save_Bn;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_config, container, false);
        initView(view);
        return view;
    }
    private void initView(View view){
        hint_TV = (TextView) view.findViewById(R.id.hint_TV);
        edit_ET = (EditText) view.findViewById(R.id.edit_ET);
        select_Bn = (Button) view.findViewById(R.id.select_Bn);
        new_Bn = (Button) view.findViewById(R.id.new_Bn);
        delate_Bn = (Button) view.findViewById(R.id.delate_Bn);
        save_Bn = (Button) view.findViewById(R.id.save_Bn);
    }


}

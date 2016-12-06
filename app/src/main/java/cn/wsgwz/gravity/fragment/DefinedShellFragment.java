package cn.wsgwz.gravity.fragment;


import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.Fragment;
import android.widget.EditText;

import cn.wsgwz.gravity.R;
public class DefinedShellFragment extends Fragment {
    private EditText edit_ET;
    private String str;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_defined_shell, container, false);
        initView(view);
        return view;
    }
    private void initView(View view){
        edit_ET = (EditText) view.findViewById(R.id.edit_ET);
        edit_ET.setText(str);
    }

    public String getStr() {
        return str;
    }

    public void setStr(String str) {
        this.str = str;
    }

    public EditText getEdit_ET() {
        return edit_ET;
    }

    public void setEdit_ET(EditText edit_ET) {
        this.edit_ET = edit_ET;
    }
}

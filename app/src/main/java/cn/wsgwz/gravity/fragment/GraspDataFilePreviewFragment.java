package cn.wsgwz.gravity.fragment;

import android.app.Fragment;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import cn.wsgwz.gravity.R;

public class GraspDataFilePreviewFragment extends Fragment {
    private String content;
    private EditText hint_ET;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grasp_data_file_preview, container, false);
        initView(view);
        return view;
    }
    private void initView(View view){
        hint_ET = (EditText) view.findViewById(R.id.hint_ET );
        hint_ET.setText(content);
        hint_ET.setCursorVisible(false);
       /* hint_ET.setFocusable(false);
        hint_ET.setFocusableInTouchMode(false);
        hint_ET.setClickable(false);*/
        hint_ET.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                hint_ET.setCursorVisible(true);
                return false;
            }
        });
    }

    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
}

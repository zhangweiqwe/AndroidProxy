package cn.wsgwz.gravity.fragment;

import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.pull.refreshview.XListView;
import com.example.pull.refreshview.XScrollView;

import cn.wsgwz.gravity.R;

public class ExplainFragment extends Fragment {
    private TextView explain_TV;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_explain, container, false);
        initView(view);
        return view;
    }
    private void initView(View view){
        explain_TV = (TextView) view.findViewById(R.id.explain_TV);
        explain_TV.setText(Html.fromHtml(getResources().getString(R.string.exlpain)));
    }

}

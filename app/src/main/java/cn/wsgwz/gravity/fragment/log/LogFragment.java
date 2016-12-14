package cn.wsgwz.gravity.fragment.log;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.wsgwz.gravity.R;
import cn.wsgwz.gravity.util.LogUtil;
import cn.wsgwz.gravity.util.NetworkUtil;
import cn.wsgwz.gravity.util.OnExecResultListenner;
import cn.wsgwz.gravity.util.ShellUtil;
import cn.wsgwz.gravity.view.MyRecyclerView;
import cn.wsgwz.gravity.view.slidingTabLayout.ViewPager;

public class LogFragment extends Fragment {
    private MyRecyclerView recyclerView;
    private LogAdapter logAdapter;
    private OnListFragmentInteractionListenner mListenner;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_log, container, false);
        if(view instanceof MyRecyclerView){
            final Context context = view.getContext();
            recyclerView = (MyRecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            logAdapter = new LogAdapter(LogContent.ITEMS,mListenner);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            //Decoration n. 装饰，装潢；装饰品；奖章
            recyclerView.addItemDecoration(new DividerItemDecoration(context,DividerItemDecoration.VERTICAL));
            recyclerView.setAdapter(logAdapter);
            LogContent.initAdapter(logAdapter);
        }

        return view;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if(Build.VERSION.SDK_INT>=23){
            return;
        }
        if(activity instanceof OnListFragmentInteractionListenner){
            mListenner = (OnListFragmentInteractionListenner)activity;
        }else {
            throw new RuntimeException(activity.toString()+" must implement OnListFragmentInteractionListener");
        }
    }

    // //SDK API<23时，onAttach(Context)不执行，需要使用onAttach(Activity)。Fragment自身的Bug，v4的没有此问题
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if(context instanceof OnListFragmentInteractionListenner){
            mListenner = (OnListFragmentInteractionListenner)context;
        }else {
            throw new RuntimeException(context.toString()+" must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListenner = null;
        logAdapter=null;
    }

    /*
            interaction [ɪntər'ækʃ(ə)n]
        n. 相互作用；[数] 交互作用
        n. 互动
             */
    //这是用于监听交互
    public interface OnListFragmentInteractionListenner{
        void onListFRagmentInteraction(String str);
    }

}

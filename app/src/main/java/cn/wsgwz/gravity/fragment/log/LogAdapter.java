package cn.wsgwz.gravity.fragment.log;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cn.wsgwz.gravity.R;

/**
 * Created by Jeremy Wang on 2016/12/2.
 */

public class LogAdapter extends RecyclerView.Adapter<LogAdapter.ViewHolder> {
    private final List<String> mValues;
    private final LogFragment.OnListFragmentInteractionListenner mListenner;

    public LogAdapter(List<String> mValues, LogFragment.OnListFragmentInteractionListenner mListenner) {
        this.mValues = mValues;
        this.mListenner = mListenner;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_log_recycler_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.str = mValues.get(position);
        holder.item.setText(mValues.get(position));
        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(null != mListenner){
                    mListenner.onListFRagmentInteraction(holder.str);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public final TextView item;
        public String str;
        public ViewHolder(View itemView) {
            super(itemView);
            item = (TextView) itemView.findViewById(R.id.item);
        }

        @Override
        public String toString() {
            return "ViewHolder{" +
                    "item=" + item.getText().toString() +
                    '}';
        }
    }
}

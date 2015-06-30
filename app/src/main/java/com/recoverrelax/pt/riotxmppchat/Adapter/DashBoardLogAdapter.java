package com.recoverrelax.pt.riotxmppchat.Adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppUtils.AppDateUtils;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppUtils.AppMiscUtils;
import com.recoverrelax.pt.riotxmppchat.R;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import LolChatRiotDb.InAppLogDb;
import butterknife.Bind;
import butterknife.ButterKnife;

public class DashBoardLogAdapter extends RecyclerView.Adapter<DashBoardLogAdapter.ViewHolder> {

    List<InAppLogDb> logList;
    private LayoutInflater inflater;
    private Context context;
    private Random random;
    private RecyclerView recyclerView;

    @LayoutRes
    int layoutRes = R.layout.dashboard_log_layout;

    public DashBoardLogAdapter(Context context, ArrayList<InAppLogDb> logList, RecyclerView recyclerView) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.logList = logList;
        this.random = new Random();
        this.recyclerView = recyclerView;
    }

    @Override
    public DashBoardLogAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(layoutRes, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DashBoardLogAdapter.ViewHolder holder, int position) {
        holder.inappLogDb = logList.get(position);

        holder.logMessage.setText(holder.inappLogDb.getLogMessage());

        if(holder.inappLogDb.getLogDate()!=null)
            AppDateUtils.setTimeElapsedWithHandler(holder.logDate, holder.inappLogDb.getLogDate());
        else
            holder.logDate.setText("");
    }

    public void setItems(List<InAppLogDb> items) {
        logList = items;
        notifyDataSetChanged();
    }

    public void setSingleItem(InAppLogDb item) {
        logList.add(0, item);
        notifyItemInserted(0);
        recyclerView.scrollToPosition(0);
    }

    @Override
    public int getItemCount() {
        return logList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.logDate)
        TextView logDate;

        @Bind(R.id.logMessage)
        TextView logMessage;

        @Bind(R.id.log_cardview)
        CardView log_cardview;

        InAppLogDb inappLogDb;

        @ColorRes int cardColor;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            if(itemView instanceof CardView) {
                int cardColorId = AppMiscUtils.getRamdomMaterialColor(random);
                cardColor = context.getResources().getColor(cardColorId);
                cardColor = AppMiscUtils.changeColorAlpha(cardColor, 190);
                log_cardview.setCardBackgroundColor(cardColor);
            }
        }
    }
}

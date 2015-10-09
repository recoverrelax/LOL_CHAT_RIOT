package com.recoverrelax.pt.riotxmppchat.Adapter;

import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppDateUtils;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppMiscUtils;
import com.recoverrelax.pt.riotxmppchat.R;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import LolChatRiotDb.InAppLogDb;
import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class DashBoardLogAdapter extends RecyclerView.Adapter<DashBoardLogAdapter.ViewHolder> {

    private List<InAppLogDb> logList;
    private final LayoutInflater inflater;
    private final Context context;
    private final Random random;
    private final RecyclerView recyclerView;
    private CompositeSubscription subscriptions = new CompositeSubscription();
    private final int updateInterval = 30000;

    @LayoutRes
    private final
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

    public void removeSubscriptions(){
        subscriptions.clear();
    }

    @Override
    public void onBindViewHolder(DashBoardLogAdapter.ViewHolder holder, int position) {
        holder.inappLogDb = logList.get(position);

        holder.logMessage.setText(holder.inappLogDb.getLogMessage());

        if(holder.inappLogDb.getLogDate()!=null) {
            subscriptions.add(
                    Observable.interval(updateInterval, TimeUnit.MILLISECONDS)
                            .map(aLong -> AppDateUtils.getFormatedDate(holder.inappLogDb.getLogDate()))
                            .subscribeOn(Schedulers.io())
                            .doOnSubscribe(() -> holder.logDate.setText(AppDateUtils.getFormatedDate(holder.inappLogDb.getLogDate())))
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(holder.logDate::setText)
            );
//       AppDateUtils.setTimeElapsedWithHandler(holder.logDate, holder.inappLogDb.getLogDate());
        }else {
            holder.logDate.setText("");
        }
    }

    public void setItems(List<InAppLogDb> items) {
        logList = items;
        removeSubscriptions();
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

        @Bind(R.id.logLayout)
        LinearLayout logLayout;

        InAppLogDb inappLogDb;

        @ColorRes int cardColor;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            if(itemView instanceof CardView) {
                int cardColorId = AppMiscUtils.getRandomMaterialColor(random);
                cardColor = context.getResources().getColor(cardColorId);
                cardColor = AppMiscUtils.changeColorAlpha(cardColor, 190);
                logLayout.setBackgroundColor(context.getResources().getColor(cardColor));
            }
        }
    }
}

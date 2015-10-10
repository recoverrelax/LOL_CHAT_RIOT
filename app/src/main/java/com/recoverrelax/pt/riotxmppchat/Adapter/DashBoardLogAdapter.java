package com.recoverrelax.pt.riotxmppchat.Adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.recoverrelax.pt.riotxmppchat.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import LolChatRiotDb.InAppLogDb;
import butterknife.Bind;
import butterknife.ButterKnife;
import pt.reco.myutil.MyDate;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class DashBoardLogAdapter extends RecyclerView.Adapter<DashBoardLogAdapter.ViewHolder> {

    private List<InAppLogDb> logList;
    private final LayoutInflater inflater;
    private final Context context;
    private final RecyclerView recyclerView;
    private CompositeSubscription subscriptions = new CompositeSubscription();
    private final int updateInterval = 30000;

    private @LayoutRes int layout;

    public DashBoardLogAdapter(Context context, ArrayList<InAppLogDb> logList, RecyclerView recyclerView, int layout) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.logList = logList;
        this.layout = layout;
        this.recyclerView = recyclerView;
    }

    @Override
    public DashBoardLogAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(layout, parent, false);
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
                            .map(aLong -> MyDate.getFormatedDate(holder.inappLogDb.getLogDate()))
                            .subscribeOn(Schedulers.io())
                            .doOnSubscribe(() -> holder.logDate.setText(MyDate.getFormatedDate(holder.inappLogDb.getLogDate())))
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(holder.logDate::setText)
            );
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

        InAppLogDb inappLogDb;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

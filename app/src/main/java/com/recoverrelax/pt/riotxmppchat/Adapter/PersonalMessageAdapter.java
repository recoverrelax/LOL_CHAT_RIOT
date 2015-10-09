package com.recoverrelax.pt.riotxmppchat.Adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.recoverrelax.pt.riotxmppchat.R;
import com.recoverrelax.pt.riotxmppchat.Storage.MessageDirection;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import LolChatRiotDb.MessageDb;
import butterknife.Bind;
import butterknife.ButterKnife;
import pt.reco.myutil.MyDate;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.recoverrelax.pt.riotxmppchat.MyUtil.LogUtils.LOGI;

public class PersonalMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<MessageDb> personalMessageList;
    private final LayoutInflater inflater;
    private final Context context;
    private final RecyclerView recyclerView;

    @LayoutRes
    int layout_from = R.layout.personal_message_from_layout;
    @LayoutRes
    int layout_to = R.layout.personal_message_to_layout;

    private final int VIEW_HOLDER_FROM_ID = 0;
    private final int VIEW_HOLDER_TO_ID = 1;

    private final CompositeSubscription subscriptions = new CompositeSubscription();
    private final int updateInterval = 10000;

    public PersonalMessageAdapter(Context context, ArrayList<MessageDb> personalMessageList, int layout_from, int layout_to, RecyclerView recycler) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.layout_from = layout_from;
        this.layout_to = layout_to;
        this.personalMessageList = personalMessageList;
        this.recyclerView = recycler;
    }

    @Override
    public int getItemViewType(int position) {

        if (personalMessageList.get(position).getDirection().equals(MessageDirection.FROM.getId()))
            return VIEW_HOLDER_FROM_ID;
        else
            return VIEW_HOLDER_TO_ID;
    }

    public List<MessageDb> getAllMessages(){
        return personalMessageList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View viewFrom = inflater.inflate(layout_from, parent, false);
        View viewTo = inflater.inflate(layout_to, parent, false);

        switch (viewType) {
            case VIEW_HOLDER_FROM_ID:
                return new MyViewHolderFrom(viewFrom);
            case VIEW_HOLDER_TO_ID:
                return new MyViewHolderTo(viewTo);
            default:
                return new MyViewHolderTo(viewTo);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final MessageDb message = personalMessageList.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_HOLDER_FROM_ID:
                final MyViewHolderFrom holderFrom = (MyViewHolderFrom) holder;
                holderFrom.messageDb = message;

                holderFrom.startUpdatingTimeStamp();

                holderFrom.message.setText(holderFrom.messageDb.getMessage());
                break;

            case VIEW_HOLDER_TO_ID:
                final MyViewHolderTo holderTo = (MyViewHolderTo) holder;
                holderTo.messageDb = message;

                holderTo.startUpdatingTimeStamp();

                holderTo.message.setText(holderTo.messageDb.getMessage());
                break;
        }
    }

    public void setItems(List<MessageDb> items, ScrollTo scrollTo) {
        int sizeDifference = items.size() - personalMessageList.size();
        personalMessageList = items;
        sizeDifference = personalMessageList.size() - sizeDifference;
        notifyDataSetChanged();

        if(scrollTo != null)
            this.recyclerView.scrollToPosition(scrollTo.equals(ScrollTo.FIRST_ITEM) ? 0 : personalMessageList.size()-1);
        else
            this.recyclerView.scrollToPosition(sizeDifference);
    }

    public void addItem(MessageDb item) {

        this.personalMessageList.add(0, item);
        notifyItemInserted(0);
        this.recyclerView.scrollToPosition(0);
    }

    public enum ScrollTo{
        LAST_ITEM,
        FIRST_ITEM
    }

    @Override
    public int getItemCount() {
        return personalMessageList.size();
    }

    public void removeSubscriptions(){
        subscriptions.clear();
    }

    class MyViewHolderFrom extends RecyclerView.ViewHolder {

        @Bind(R.id.message)
        TextView message;

        @Bind(R.id.date)
        TextView date;

        private MessageDb messageDb;
        private Subscription subscription;

        public MyViewHolderFrom(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void startUpdatingTimeStamp(){
            date.setText(MyDate.getFormatedDate(messageDb.getDate()));

            subscription = Observable.interval(updateInterval, TimeUnit.MILLISECONDS)
                    .map(aLong -> MyDate.getFormatedDate(messageDb.getDate()))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<String>() {
                        @Override public void onCompleted() { }
                        @Override public void onError(Throwable e) { }

                        @Override
                        public void onNext(String formattedDate) {
                            LOGI("TAG", formattedDate);
                            date.setText(formattedDate);
                        }
                    });
            subscriptions.add(subscription);
        }
    }

    class MyViewHolderTo extends RecyclerView.ViewHolder {

        @Bind(R.id.message)
        TextView message;

        @Bind(R.id.date)
        TextView date;

        private MessageDb messageDb;
        private Subscription subscription;

        public MyViewHolderTo(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void startUpdatingTimeStamp(){
            date.setText(MyDate.getFormatedDate(messageDb.getDate()));

            subscription = Observable.interval(updateInterval, TimeUnit.MILLISECONDS)
                    .map(aLong -> MyDate.getFormatedDate(messageDb.getDate()))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<String>() {
                        @Override public void onCompleted() { }
                        @Override public void onError(Throwable e) { }

                        @Override
                        public void onNext(String formattedDate) {
                            LOGI("TAG", formattedDate);
                            date.setText(formattedDate);
                        }
                    });
            subscriptions.add(subscription);
        }
    }
}

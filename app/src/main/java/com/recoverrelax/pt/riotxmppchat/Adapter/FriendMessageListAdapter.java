package com.recoverrelax.pt.riotxmppchat.Adapter;

import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.recoverrelax.pt.riotxmppchat.Storage.MessageDirection;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppContextUtils;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppDateUtils;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppMiscUtils;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppXmppUtils;
import com.recoverrelax.pt.riotxmppchat.R;
import com.recoverrelax.pt.riotxmppchat.Riot.Model.FriendListChat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.recoverrelax.pt.riotxmppchat.MyUtil.LogUtils.LOGI;

public class FriendMessageListAdapter extends RecyclerView.Adapter<FriendMessageListAdapter.ViewHolder> {

    private List<FriendListChat> friendMessageList;
    private final LayoutInflater inflater;
    private final Context context;
    private final Random random;
    private OnRowClick clickCallback;
    private final CompositeSubscription subscriptions = new CompositeSubscription();

    @LayoutRes
    private final
    int layoutRes = R.layout.friend_message_list_child_layout;

    public FriendMessageListAdapter(Context context, ArrayList<FriendListChat> friendMessageList) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.friendMessageList = friendMessageList;
        this.random = new Random();
    }

    @Override
    public FriendMessageListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(layoutRes, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final FriendMessageListAdapter.ViewHolder holder, int position) {
        holder.friendListChat = friendMessageList.get(position);

        holder.name.setText(holder.friendListChat.getFriendName());
        holder.lastMessage.setText(holder.friendListChat.getFriendLastMessage());

        /**
         * Properly format the date
         */
        final Date friendLastMessageDate = holder.friendListChat.getFriendLastMessageDate();

        if(friendLastMessageDate != null) {
            holder.startTimestampUpdate();
        }else
            holder.stopTimestampUpdate();

        Boolean wasRead = holder.friendListChat.getLastMessage().getWasRead();

        if(!wasRead && holder.friendListChat.getLastMessage().getDirection() == MessageDirection.FROM.getId()){
            holder.wasRead.setVisibility(View.VISIBLE);
            AppContextUtils.setBlinkAnimation(holder.wasRead, true);
        }else{
            holder.wasRead.setVisibility(View.INVISIBLE);
            AppContextUtils.setBlinkAnimation(holder.wasRead, false);
        }
    }

    public void setItems(List<FriendListChat> items) {
        friendMessageList = items;
        notifyDataSetChanged();
    }

    public void setSingleItem(FriendListChat item) {
        String userXmppAddress = AppXmppUtils.parseXmppAddress(item.getFriend().getUserXmppAddress());

        int position = getFriendMessageListPositionByFriendName(userXmppAddress);
        if(position != -1){
            friendMessageList.get(position).setMessage(item.getLastMessage());
            notifyItemChanged(position);
        }
    }

    public void setRowClickListener(OnRowClick clickCallback){
        this.clickCallback = clickCallback;
    }

    public boolean contains(String userXmppAddress){
        for(FriendListChat flc: friendMessageList){
            if(flc.getFriend().getUserXmppAddress().equals(userXmppAddress))
                return true;
        }
        return false;
    }

    /**
     * Get the array position corresponding to the given xmppName of the user
     * @param xmppName
     * @return the position or -1 for cudn't find
     */
    private int getFriendMessageListPositionByFriendName(String xmppName){
        int friendMessageListSize = friendMessageList.size();

        for(int i = 0 ; i < friendMessageListSize; i++){
            if(friendMessageList.get(i).getFriend().getUserXmppAddress().equals(xmppName))
                return i;
        }
        return -1;
    }

    public void removeSubscriptions(){
        subscriptions.clear();
    }

    @Override
    public int getItemCount() {
        return friendMessageList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.name)
        TextView name;

        @Bind(R.id.lastMessage)
        TextView lastMessage;

        @Bind(R.id.date)
        TextView date;

        @Bind(R.id.friends_list_cardview)
        CardView friends_list_cardview;

        @Bind(R.id.wasRead)
        ImageView wasRead;

        FriendListChat friendListChat;

        @ColorRes int cardColor;

        private Subscription subscription;
        private final int updateInterval = 10000;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            if(itemView instanceof CardView) {
                int cardColorId = AppMiscUtils.getRamdomMaterialColor(random);
                cardColor = context.getResources().getColor(cardColorId);
                cardColor = AppMiscUtils.changeColorAlpha(cardColor, 190);
                friends_list_cardview.setCardBackgroundColor(cardColor);
            }
        }

        public void startTimestampUpdate(){
            // The initialValue, the Observable delay applies in the firstTime
            date.setText(AppDateUtils.getFormatedDate(friendListChat.getFriendLastMessageDate()));

            subscription = Observable.interval(updateInterval, TimeUnit.MILLISECONDS)
                    .map(aLong -> AppDateUtils.getFormatedDate(friendListChat.getFriendLastMessageDate()))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<String>() {
                        @Override public void onCompleted() { }
                        @Override public void onError(Throwable e) { }

                        @Override
                        public void onNext(String formattedDate) {
                            date.setText(formattedDate);
                        }
                    });
            subscriptions.add(subscription);
        }

        public void stopTimestampUpdate(){
            subscriptions.remove(subscription);
        }

        @OnClick(R.id.friends_list_cardview)
        public void onClick(View view) {
            if(clickCallback != null)
                clickCallback.onRowClick(view, friendListChat.getFriendName(), friendListChat.getUserXmppAddress(), cardColor);
        }
    }

    public interface OnRowClick{
        void onRowClick(View view, String friendName, String friendXmppAddress, int cardColor);
    }
}

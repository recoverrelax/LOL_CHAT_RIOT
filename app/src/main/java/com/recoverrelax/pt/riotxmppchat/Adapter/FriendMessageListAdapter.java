package com.recoverrelax.pt.riotxmppchat.Adapter;

import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
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

import butterknife.Bind;
import butterknife.ButterKnife;

public class FriendMessageListAdapter extends RecyclerView.Adapter<FriendMessageListAdapter.ViewHolder> {

    List<FriendListChat> friendMessageList;
    private LayoutInflater inflater;
    private Context context;
    private Random random;
    private OnRowClick clickCallback;

    @LayoutRes
    int layoutRes = R.layout.friend_message_list_child_layout;

    public FriendMessageListAdapter(Context context, ArrayList<FriendListChat> friendMessageList) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.friendMessageList = friendMessageList;
        random = new Random();
    }

    @Override
    public FriendMessageListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(layoutRes, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final FriendMessageListAdapter.ViewHolder holder, int position) {
        FriendListChat friendListChat = friendMessageList.get(position);
        holder.friendListChat = friendListChat;

        holder.name.setText(friendListChat.getFriendName());


        holder.lastMessage.setText(friendListChat.getFriendLastMessage());

        /**
         * Properly format the date
         */
        final Date friendLastMessageDate = friendListChat.getFriendLastMessageDate();

        if(friendLastMessageDate != null) {
            AppDateUtils.setTimeElapsedWithHandler(holder.date, friendLastMessageDate);
        }else
            holder.date.setText(friendListChat.getFriendLastMessageDateAsString());

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
     * @return the position or -1 for cudn't fined
     */
    public int getFriendMessageListPositionByFriendName(String xmppName){
        int friendMessageListSize = friendMessageList.size();

        for(int i = 0 ; i < friendMessageListSize; i++){
            if(friendMessageList.get(i).getFriend().getUserXmppAddress().equals(xmppName))
                return i;
        }
        return -1;
    }

    @Override
    public int getItemCount() {
        return friendMessageList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

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

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            if(itemView instanceof CardView) {
                int cardColorId = AppMiscUtils.getRamdomMaterialColor(random);
                cardColor = context.getResources().getColor(cardColorId);
                cardColor = AppMiscUtils.changeColorAlpha(cardColor, 190);
                friends_list_cardview.setCardBackgroundColor(cardColor);
            }
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(clickCallback != null)
                clickCallback.onRowClick(view, friendListChat.getFriendName(), friendListChat.getUserXmppAddress(), cardColor);
        }
    }

    public interface OnRowClick{
        void onRowClick(View view, String friendName, String friendXmppAddress, int cardColor);
    }
}

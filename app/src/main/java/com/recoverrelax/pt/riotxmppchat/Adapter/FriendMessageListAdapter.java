package com.recoverrelax.pt.riotxmppchat.Adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.recoverrelax.pt.riotxmppchat.Database.MessageDirection;
import com.recoverrelax.pt.riotxmppchat.EventHandling.FriendList.OnReplaceMainFragmentEvent;
import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppUtils.AndroidUtils;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppUtils.XmppUtils;
import com.recoverrelax.pt.riotxmppchat.R;
import com.recoverrelax.pt.riotxmppchat.Riot.Model.FriendListChat;

import java.awt.font.TextAttribute;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
public class FriendMessageListAdapter extends RecyclerView.Adapter<FriendMessageListAdapter.ViewHolder> {

    List<FriendListChat> friendMessageList;
    private LayoutInflater inflater;
    private Context context;

    private @LayoutRes int layoutRes;


    public FriendMessageListAdapter(Context context, ArrayList<FriendListChat> friendMessageList, int layoutRes) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.layoutRes = layoutRes;
        this.friendMessageList = friendMessageList;
    }

    @Override
    public FriendMessageListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(layoutRes, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FriendMessageListAdapter.ViewHolder holder, int position) {
        FriendListChat friendListChat = friendMessageList.get(position);
        holder.friendListChat = friendListChat;

        holder.name.setText(friendListChat.getFriendName());


        holder.lastMessage.setText(friendListChat.getFriendLastMessage());

        /**
         * Properly format the date
         */
        Date friendLastMessageDate = friendListChat.getFriendLastMessageDate();
        if(friendLastMessageDate != null) {
            //..
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(friendLastMessageDate);

            String DATE_SEPARATOR = "-";
            String HOUR_SEPARATOR = ":";
            String DATE_SPACE = " ";


            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);

            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minutes = calendar.get(Calendar.MINUTE);

            String date = day + DATE_SEPARATOR + month + DATE_SEPARATOR + year + DATE_SPACE + hour + HOUR_SEPARATOR + minutes;

            holder.date.setText(date);

        }else
            holder.date.setText(friendListChat.getFriendLastMessageDateAsString());

        Boolean wasRead = holder.friendListChat.getLastMessage().getWasRead();
        if(!wasRead && holder.friendListChat.getLastMessage().getDirection() == MessageDirection.FROM.getId()){
            holder.wasRead.setVisibility(View.VISIBLE);
            AndroidUtils.setBlinkAnimation(holder.wasRead, true);
        }else{
            holder.wasRead.setVisibility(View.INVISIBLE);
            AndroidUtils.setBlinkAnimation(holder.wasRead, false);
        }
    }

    public void setItems(List<FriendListChat> items) {
        friendMessageList = items;
        notifyDataSetChanged();
    }

    public void setItem(FriendListChat item) {
        String userXmppAddress = XmppUtils.parseXmppAddress(item.getFriend().getUserXmppAddress());

        int position = getFriendMessageListPositionByFriendName(userXmppAddress);
        if(position != -1){
            friendMessageList.get(position).setMessage(item.getLastMessage());
            Log.i("ASAS", friendMessageList.get(position).toString());
            notifyItemChanged(position);
        }
    }

    /**
     * Get the array position corresponding to the given xmppName of the user
     * @param xmppName
     * @return the position or -1 for cudn't fined
     */
    public int getFriendMessageListPositionByFriendName(String xmppName){
        int friendMessageListSize = friendMessageList.size();

        for(int i = 0 ; i < friendMessageListSize; i++){
            Log.i("ASASAS", "1: " + friendMessageList.get(i).getFriend().getUserXmppAddress() + "\n2: " + xmppName);
            if(friendMessageList.get(i).getFriend().getUserXmppAddress().equals(xmppName))
                return i;
        }
        return -1;
    }

    @Override
    public int getItemCount() {
        return friendMessageList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.name)
        TextView name;

        @InjectView(R.id.lastMessage)
        TextView lastMessage;

        @InjectView(R.id.date)
        TextView date;

        @InjectView(R.id.wasRead)
        ImageView wasRead;

        FriendListChat friendListChat;
        View itemView;


        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            ButterKnife.inject(this, itemView);
        }

        @OnClick(R.id.parent_row)
        public void onRowClick(View view){
            MainApplication.getInstance().getBusInstance().post(new OnReplaceMainFragmentEvent(friendListChat.getFriendName(),
                    friendListChat.getUserXmppAddress()));
        }
    }
}

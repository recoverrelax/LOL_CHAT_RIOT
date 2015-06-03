package com.recoverrelax.pt.riotxmppchat.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.recoverrelax.pt.riotxmppchat.R;
import com.github.mrengineer13.snackbar.SnackBar;
import com.recoverrelax.pt.riotxmppchat.MyUtil.google.LogUtils;
import com.recoverrelax.pt.riotxmppchat.Riot.Enum.GameStatus;
import com.recoverrelax.pt.riotxmppchat.Riot.Enum.PresenceMode;
import com.recoverrelax.pt.riotxmppchat.Riot.Enum.RiotGlobals;
import com.recoverrelax.pt.riotxmppchat.Riot.Model.Friend;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class FriendsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = FriendsListAdapter.class.getSimpleName();

    List<Friend> friendsList;
    private LayoutInflater inflater;
    private Context context;
    private OnFriendClick onFriendClickCallback;
    private RecyclerView recyclerView;
    private
    @LayoutRes int layout_online;
    @LayoutRes int layout_offline;

    private final int VIEW_HOLDER_ONLINE_ID = 0;
    private final int VIEW_HOLDER_OFFLINE_ID = 1;

    public FriendsListAdapter(Fragment frag, ArrayList<Friend> friendsList, int layout_online, int  layout_offline, RecyclerView recyclerView) {

        this.context = frag.getActivity();
        inflater = LayoutInflater.from(this.context);

        this.layout_online = layout_online;
        this.layout_offline = layout_offline;
        this.friendsList = friendsList;
        this.recyclerView = recyclerView;
    }

    public void setOnChildClickListener(OnFriendClick onFriendClickCallback){
        this.onFriendClickCallback = onFriendClickCallback;
    }

    @Override
    public int getItemViewType(int position) {

        if(friendsList.get(position).isOnline())
            return VIEW_HOLDER_ONLINE_ID;
        else
            return VIEW_HOLDER_OFFLINE_ID;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View viewOnline = inflater.inflate(layout_online, parent, false);
        View viewOffline = inflater.inflate(layout_offline, parent, false);

        switch(viewType){
            case VIEW_HOLDER_ONLINE_ID:
                return new MyViewHolderOnline(viewOnline);
            case VIEW_HOLDER_OFFLINE_ID:
                return new MyViewHolderOffline(viewOffline);
            default:
                return new MyViewHolderOffline(viewOffline);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Friend friend = friendsList.get(position);
        PresenceMode friendMode = friend.getFriendMode();

        switch(holder.getItemViewType()){
            case VIEW_HOLDER_ONLINE_ID:
                MyViewHolderOnline holderOnline = (MyViewHolderOnline) holder;

                holderOnline.current = friend;
                holderOnline.friendName.setText(friend.getName());

                if (friend.getGameStatus().equals(GameStatus.IN_QUEUE) || friend.getGameStatus().equals(GameStatus.INGAME)) {
                    holderOnline.startRepeatingTask();
                } else {
                    holderOnline.stopRepeatingTask();
                }

                /**
                 * Check if the user is currently playing
                 */

                holderOnline.friendPresenceMode.setText(friendMode.getDescriptiveName());
                holderOnline.friendPresenceMode.setTextColor(context.getResources().getColor(R.color.white));

                GradientDrawable drawable = (GradientDrawable) holderOnline.friendPresenceMode.getBackground();
                drawable.setColor(context.getResources().getColor(friendMode.getStatusColor()));


                holderOnline.wins.setText(friend.getWins());
                holderOnline.ranked_icon.setImageDrawable(context.getResources().getDrawable(friend.getProfileIconResId()));
                holderOnline.division_league.setText(friend.getLeagueDivisionAndTier().getDescriptiveName());

//                /**
//                 * Set The Status Message
//                 */
//                String statusMsg = friend.getStatusMsg();
//                if (!statusMsg.equals(Friend.PERSONAL_MESSAGE_NO_VIEW)) {
//                    holderOnline.statusMsg.setText(statusMsg);
//                    holderOnline.statusMsg.setVisibility(View.VISIBLE);
//                } else
//                    holderOnline.statusMsg.setVisibility(View.INVISIBLE);

                /**
                 * Set the Game Status
                 */
                String gameStatusToPrint = friend.getGameStatusToPrint();
                if (!gameStatusToPrint.equals(Friend.GAME_STATUS_NO_VIEW)) {
                    holderOnline.gameStatus.setText(gameStatusToPrint);
                    holderOnline.gameStatus.setVisibility(View.VISIBLE);
                } else
                    holderOnline.gameStatus.setVisibility(View.GONE);

                /**
                 * Load Image from LolKing Server
                 */

                if (friend.getProfileIconId().equals("")) {
                    Picasso.with(context)
                            .load(R.drawable.profile_icon_example)
                            .into(holderOnline.profileIcon);
                } else {
                    Picasso pic = Picasso.with(context);
                    pic.load(RiotGlobals.LOLKING_PROFILE_ICON_URL + friend.getProfileIconId() + RiotGlobals.LOLKING_PROFILE_ICON_EXTENSION)
                            .placeholder(R.drawable.profile_icon_example)
                            .error(R.drawable.profile_icon_example)
                            .into(holderOnline.profileIcon);
                }
                break;

            case VIEW_HOLDER_OFFLINE_ID:
                MyViewHolderOffline holderOffline = (MyViewHolderOffline) holder;

                holderOffline.current = friend;
                holderOffline.friendName.setText(friend.getName());

                /**
                 * Check if the user is currently playing
                 */

                holderOffline.friendPresenceMode.setText(friendMode.getDescriptiveName());
                holderOffline.friendPresenceMode.setTextColor(context.getResources().getColor(R.color.white));

                GradientDrawable drawable2 = (GradientDrawable) holderOffline.friendPresenceMode.getBackground();
                drawable2.setColor(context.getResources().getColor(friendMode.getStatusColor()));

                /**
                 * Load Image from LolKing Server
                 */

                if (friend.getProfileIconId().equals("")) {
                    Picasso.with(context)
                            .load(R.drawable.profile_icon_example)
                            .into(holderOffline.profileIcon);
                } else {
                    Picasso pic = Picasso.with(context);
                    pic.load(RiotGlobals.LOLKING_PROFILE_ICON_URL + friend.getProfileIconId() + RiotGlobals.LOLKING_PROFILE_ICON_EXTENSION)
                            .placeholder(R.drawable.profile_icon_example)
                            .error(R.drawable.profile_icon_example)
                            .into(holderOffline.profileIcon);
                }

                break;
        }




    }

    public void setItems(List<Friend> items) {
        friendsList = items;
        sortFriendsList(SortMethod.ONLINE_FIRST);
        notifyDataSetChanged();
    }

    public int getOnlineFriendsCount(){
        int count = 0;
        for(Friend f: friendsList){
            if(f.isOnline())
                count++;
        }
        return count;
    }

    /**
     * Two types of Changed:
     * - User changed Presence.Type
     * - User changed Presence.Mode
     */
    public void setFriendChanged(Friend newFriend) {
        if (friendsList.contains(newFriend)) {
            int positionFriend = friendsList.indexOf(newFriend);

            Friend oldFriend = friendsList.get(positionFriend);

            if (newFriend.isOnline()) { // NEW FRIEND ONLINE
                if (oldFriend.isOnline()) {
                    // ONLINE - ONLINE
                    friendsList.remove(positionFriend);
                    friendsList.add(positionFriend, newFriend);
                    notifyItemChanged(positionFriend);
                } else {
                    // OFFLINE - ONLINE
                    friendsList.remove(positionFriend);
                    friendsList.add(0, newFriend);
                    notifyItemInserted(0);
                    notifyItemRemoved(positionFriend + 1);
                    recyclerView.scrollToPosition(0);
                }
            } else { // NEW FRIEND OFFLINE
                if (oldFriend.isOnline()) {
                    // ONLINE - OFFLINE
                    friendsList.remove(positionFriend);
                    friendsList.add(newFriend);
                    notifyItemRemoved(positionFriend);
                } else {
                    // OFFLINE - OFFLINE
                    // do nothing!
                }
            }
        }
    }

    public void sortFriendsList(SortMethod sortedMethod) {
        if (sortedMethod.isSortOnlineFirst()) {
            Collections.sort(friendsList, new Friend.OnlineOfflineComparator());
        } else if (sortedMethod.isSortAlphabetically()) {
            Collections.sort(friendsList, new Friend.AlphabeticComparator());
        } else { // default
            Collections.sort(friendsList, new Friend.OnlineOfflineComparator());
        }
    }

    @Override
    public int getItemCount() {
        return friendsList.size();
    }

    class MyViewHolderOnline extends RecyclerView.ViewHolder implements View.OnClickListener {

        @InjectView(R.id.friendName)
        TextView friendName;

        @InjectView(R.id.friendPresenceMode)
        TextView friendPresenceMode;

        @InjectView(R.id.gameStatus)
        TextView gameStatus;

        @InjectView(R.id.profileIcon)
        ImageView profileIcon;

        @InjectView(R.id.division_league)
        TextView division_league;

        @InjectView(R.id.wins)
        TextView wins;

        @InjectView(R.id.ranked_icon)
        ImageView ranked_icon;

        Friend current;

        private final int mHandlerInterval = 6000;
        private Handler mHandler;
        private Runnable mStatusChecker;

        public MyViewHolderOnline(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
            mHandler = new Handler();
            mStatusChecker = new Runnable() {
                @Override
                public void run() {
                    String gameStatusToPrint = current.getGameStatusToPrint();
                    gameStatus.setText(gameStatusToPrint);
                    mHandler.postDelayed(mStatusChecker, mHandlerInterval);
                }
            };

            itemView.setOnClickListener(this);
        }

        public void startRepeatingTask() {
            mStatusChecker.run();
        }

        void stopRepeatingTask() {
            mHandler.removeCallbacks(mStatusChecker);
        }

        @Override
        public void onClick(View view) {
            onFriendClickCallback.onFriendClick(current.getName(), current.getUserXmppAddress());
        }
    }

    class MyViewHolderOffline extends RecyclerView.ViewHolder implements View.OnClickListener {

        @InjectView(R.id.friendName)
        TextView friendName;

        @InjectView(R.id.friendPresenceMode)
        TextView friendPresenceMode;

        @InjectView(R.id.profileIcon)
        ImageView profileIcon;

        Friend current;

        public MyViewHolderOffline(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
                    new SnackBar.Builder((Activity)context)
                    .withMessage(current.getName() + " " + context.getResources().getString(R.string.cannot_chat_with))
                    .withTextColorId(R.color.white)
                    .withBackgroundColorId(R.color.primaryColor190T)
                    .withDuration((short) 3000)
                    .show();
        }
    }

    public interface OnFriendClick {
        void onFriendClick(String friendUsername, String friendXmppName);
    }

    public enum SortMethod {
        ALPHABETICALLY,
        ONLINE_FIRST,
        OFFLINE_FIRST;

        public boolean isSortAlphabetically() {
            return this.equals(SortMethod.ALPHABETICALLY);
        }

        public boolean isSortOnlineFirst() {
            return this.equals(SortMethod.ONLINE_FIRST);
        }

    }
}

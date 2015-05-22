package com.recoverrelax.pt.riotxmppchat.Adapter;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.edgelabs.pt.mybaseapp.R;
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


public class FriendsListAdapter extends RecyclerView.Adapter<FriendsListAdapter.MyViewHolder> {

    List<Friend> friendsList;
    ArrayList<Integer> friendsPlayingOrInQueueAdapterPosition;

    private LayoutInflater inflater;
    private Context context;
    private OnItemClickAdapter callback;
    private RecyclerView myRecycer;
    private
    @LayoutRes
    int layout;

    private final String LEVEL_PREFIX = "L ";

    public FriendsListAdapter(Context context, ArrayList<Friend> friendsList, int layout, OnItemClickAdapter callback, RecyclerView myRecycer) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.layout = layout;
        this.callback = callback;
        this.friendsList = friendsList;
        this.myRecycer = myRecycer;

        friendsPlayingOrInQueueAdapterPosition = new ArrayList<>();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Friend friend = friendsList.get(position);
        holder.current = friend;
        holder.friendName.setText(friend.getName());

        if (friend.getGameStatus().equals(GameStatus.IN_QUEUE) || friend.getGameStatus().equals(GameStatus.INGAME)) {
            holder.startRepeatingTask();
        } else {
            holder.stopRepeatingTask();
        }

        /**
         * Check if the user is currently playing
         */

        PresenceMode friendMode = friend.getFriendMode();
        holder.friendPresenceMode.setText(friendMode.getDescriptiveName());
        holder.friendPresenceMode.setTextColor(context.getResources().getColor(R.color.white));


//        holder.friendPresenceMode.setBackgroundColor(context.getResources().getColor(R.color.black_50A));
        GradientDrawable drawable = (GradientDrawable) holder.friendPresenceMode.getBackground();
        drawable.setColor(context.getResources().getColor(friendMode.getStatusColor()));


        holder.wins.setText(friend.getWins());
        holder.ranked_icon.setImageDrawable(context.getResources().getDrawable(friend.getProfileIconResId()));
        holder.division_league.setText(friend.getLeagueDivisionAndTier().getDescriptiveName());

        /**
         * Set The Status Message
         */
        String statusMsg = friend.getStatusMsg();
        if (!statusMsg.equals(Friend.PERSONAL_MESSAGE_NO_VIEW)) {
            holder.statusMsg.setText(statusMsg);
            holder.statusMsg.setVisibility(View.VISIBLE);
        } else
            holder.statusMsg.setVisibility(View.INVISIBLE);

        /**
         * Set the Game Status
         */
        String gameStatusToPrint = friend.getGameStatusToPrint();
        if (!gameStatusToPrint.equals(Friend.GAME_STATUS_NO_VIEW)) {
            holder.gameStatus.setText(gameStatusToPrint);
            holder.gameStatus.setVisibility(View.VISIBLE);
        } else
            holder.gameStatus.setVisibility(View.GONE);

        /**
         * Load Image from LolKing Server
         */

        if (friend.getProfileIconId().equals("")) {
            Picasso.with(context)
                    .load(R.drawable.profile_icon_example)
                    .into(holder.profileIcon);
        } else {
            Picasso pic = Picasso.with(context);
            pic.load(RiotGlobals.LOLKING_PROFILE_ICON_URL + friend.getProfileIconId() + RiotGlobals.LOLKING_PROFILE_ICON_EXTENSION)
                    .placeholder(R.drawable.profile_icon_example)
                    .error(R.drawable.profile_icon_example)
                    .into(holder.profileIcon);
        }
    }

    public void setItems(List<Friend> items) {
        friendsList = items;
        sortFriendsList(SortMethod.ONLINE_FIRST);
        notifyDataSetChanged();
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
                    myRecycer.scrollToPosition(0);
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

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @InjectView(R.id.friendName)
        TextView friendName;

        @InjectView(R.id.friendPresenceMode)
        TextView friendPresenceMode;

        @InjectView(R.id.statusMsg)
        TextView statusMsg;

        @InjectView(R.id.gameStatus)
        TextView gameStatus;

        @InjectView(R.id.profileIcon)
        ImageView profileIcon;

        @InjectView(R.id.friends_list_cardview)
        CardView friends_list_cardview;

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

        public MyViewHolder(View itemView) {
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
        }

        @Override
        public void onClick(View view) {

        }

        public void startRepeatingTask() {
            mStatusChecker.run();
        }

        void stopRepeatingTask() {
            mHandler.removeCallbacks(mStatusChecker);
        }


    }

    public interface OnItemClickAdapter {
        void onFriendClick(Friend friend);
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

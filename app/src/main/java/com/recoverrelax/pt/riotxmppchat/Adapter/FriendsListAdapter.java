package com.recoverrelax.pt.riotxmppchat.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.support.annotation.ColorRes;
import android.support.annotation.LayoutRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.recoverrelax.pt.riotxmppchat.R;
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
import butterknife.OnClick;


public class FriendsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = FriendsListAdapter.class.getSimpleName();

    List<Friend> friendsList;
    private LayoutInflater inflater;
    private Context context;
    private OnAdapterChildClick onAdapterChildClickCallback;
    private RecyclerView recyclerView;
    private
    @LayoutRes
    int layout_online;
    @LayoutRes
    int layout_offline;

    private final int VIEW_HOLDER_ONLINE_ID = 0;
    private final int VIEW_HOLDER_OFFLINE_ID = 1;

    public FriendsListAdapter(Fragment frag, ArrayList<Friend> friendsList, int layout_online, int layout_offline, RecyclerView recyclerView) {

        this.context = frag.getActivity();
        inflater = LayoutInflater.from(this.context);

        this.layout_online = layout_online;
        this.layout_offline = layout_offline;
        this.friendsList = friendsList;
        this.recyclerView = recyclerView;
    }

    public void setAdapterClickListener(OnAdapterChildClick onAdapterChildClickCallback) {
        this.onAdapterChildClickCallback = onAdapterChildClickCallback;
    }

    @Override
    public int getItemViewType(int position) {

        if (friendsList.get(position).isOnline())
            return VIEW_HOLDER_ONLINE_ID;
        else
            return VIEW_HOLDER_OFFLINE_ID;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View viewOnline = inflater.inflate(layout_online, parent, false);
        View viewOffline = inflater.inflate(layout_offline, parent, false);

        switch (viewType) {
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

        MyViewHolder myViewHolder = (MyViewHolder) holder;

        myViewHolder.current = friend;
        myViewHolder.friendName.setText(friend.getName());

        /**
         * Load Image from LolKing Server
         */

        if (friend.getProfileIconId().equals("")) {
            Picasso.with(context)
                    .load(R.drawable.profile_icon_example)
                    .into(myViewHolder.profileIcon);
        } else {
            Picasso pic = Picasso.with(context);
            pic.load(RiotGlobals.LOLKING_PROFILE_ICON_URL + friend.getProfileIconId() + RiotGlobals.LOLKING_PROFILE_ICON_EXTENSION)
                    .placeholder(R.drawable.profile_icon_example)
                    .error(R.drawable.profile_icon_example)
                    .into(myViewHolder.profileIcon);
        }

        switch (holder.getItemViewType()) {
            case VIEW_HOLDER_ONLINE_ID:
                final MyViewHolderOnline holderOnline = (MyViewHolderOnline) holder;

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
                holderOnline.division_league.setSelected(true);

                break;

            case VIEW_HOLDER_OFFLINE_ID:
                break;
        }
    }

    public void setItems(List<Friend> items) {
        friendsList = items;
//        sortFriendsList(SortMethod.ONLINE_FIRST);
        notifyDataSetChanged();
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

    public int getOnlineFriendsCount() {
        int count = 0;
        for (Friend f : friendsList) {
            if (f.isOnline())
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

    @Override
    public int getItemCount() {
        return friendsList.size();
    }

    abstract class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @InjectView(R.id.friendName)
        TextView friendName;

        @InjectView(R.id.profileIcon)
        ImageView profileIcon;

        @InjectView(R.id.card_more)
        ImageView card_more;

        protected Friend current;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);

            itemView.setOnClickListener(this);
        }

        @OnClick(R.id.card_more_layout)
        public void onCardOptionsClick(View view) {
            if (onAdapterChildClickCallback != null)
                onAdapterChildClickCallback.onAdapterFriendOptionsClick(view);
        }

       public void setOptionsMenuDotColor(@ColorRes int color){
            Drawable drawable = card_more.getDrawable();
            drawable.mutate();
            drawable.setColorFilter(context.getResources().getColor(color), PorterDuff.Mode.SRC_IN);
        }
    }

    class MyViewHolderOnline extends MyViewHolder {

        @InjectView(R.id.gameStatus)
        TextView gameStatus;

        @InjectView(R.id.friendPresenceMode)
        TextView friendPresenceMode;

        @InjectView(R.id.division_league)
        TextView division_league;

        @InjectView(R.id.wins)
        TextView wins;

        @InjectView(R.id.ranked_icon)
        ImageView ranked_icon;

        private final int mHandlerInterval = 60000;
        private Handler mHandler;
        private Runnable mStatusChecker;

        @SuppressLint("ResourceAsColor")
        public MyViewHolderOnline(View itemView) {
            super(itemView);

            setOptionsMenuDotColor(R.color.black);

            mHandler = new Handler();
            mStatusChecker = new Runnable() {
                @Override
                public void run() {
                    String gameStatusToPrint = current.getGameStatusToPrint();
                    gameStatus.setText(gameStatusToPrint);
                    gameStatus.setSelected(true);
                    mHandler.postDelayed(mStatusChecker, mHandlerInterval);
                }
            };
        }

        @Override
        public void onClick(View view) {
            if (onAdapterChildClickCallback != null) {
                onAdapterChildClickCallback.onAdapterFriendClick(current.getName(), current.getUserXmppAddress());
            }
        }

        public void startRepeatingTask() {
            mStatusChecker.run();
        }

        void stopRepeatingTask() {
            mHandler.removeCallbacks(mStatusChecker);
            gameStatus.setText("");
        }
    }

    class MyViewHolderOffline extends MyViewHolder {

        @SuppressLint("ResourceAsColor")
        public MyViewHolderOffline(View itemView) {
            super(itemView);

            setOptionsMenuDotColor(R.color.white);
        }

        @Override
        public void onClick(View view) {
                    Snackbar
                            .make(((Activity) context).getWindow().getDecorView().getRootView(),
                                    current.getName() + " " + context.getResources().getString(R.string.cannot_chat_with),
                                    Snackbar.LENGTH_LONG);
        }

    }

    public interface OnAdapterChildClick {
        void onAdapterFriendClick(String friendUsername, String friendXmppAddress);
        void onAdapterFriendOptionsClick(View view);
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

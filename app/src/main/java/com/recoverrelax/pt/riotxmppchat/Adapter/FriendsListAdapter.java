package com.recoverrelax.pt.riotxmppchat.Adapter;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.edgelabs.pt.mybaseapp.R;
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
    private LayoutInflater inflater;
    private Context context;
    private OnItemClickAdapter callback;
    private @LayoutRes int layout;

    private final String LEVEL_PREFIX = "L ";

    public FriendsListAdapter(Context context, ArrayList<Friend> friendsList, int layout, OnItemClickAdapter callback){
        inflater= LayoutInflater.from(context);
        this.context = context;
        this.layout = layout;
        this.callback = callback;
        this.friendsList = friendsList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=inflater.inflate(layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Friend friend = friendsList.get(position);
        holder.current = friend;
        holder.friendName.setText(friend.getName());

//        holder.friends_list_cardview.setCardBackgroundColor(context.getResources().getColor(
//                friend.getUserRosterPresence().isAvailable()
//                        ? R.color.online_alpha50
//                        : R.color.offline_alpha50));

        PresenceMode friendMode = friend.getFriendMode();
        holder.friendStatus.setText(friendMode.getDescriptiveName());
        holder.friendStatus.setTextColor(context.getResources().getColor(R.color.white));


//        holder.friendStatus.setBackgroundColor(context.getResources().getColor(R.color.black_50A));
        GradientDrawable drawable = (GradientDrawable) holder.friendStatus.getBackground();
        drawable.setColor(context.getResources().getColor(friendMode.getStatusColor()));


        holder.wins.setText(friend.getWins());
        holder.ranked_icon.setImageDrawable(context.getResources().getDrawable(friend.getProfileIconResId()));
        holder.division_league.setText(friend.getLeagueDivisionAndTier().getDescriptiveName());

        /**
         * Load Image from LolKing Server
         */

        if(friend.getProfileIconId().equals("")){
            Picasso.with(context)
                    .load(R.drawable.profile_icon_example)
                    .into(holder.profileIcon);
        }else{
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

    public void sortFriendsList(SortMethod sortedMethod){
        if(sortedMethod.isSortOnlineFirst()){
           Collections.sort(friendsList, new Friend.OnlineOfflineComparator());
        }else if(sortedMethod.isSortAlphabetically()){
            Collections.sort(friendsList, new Friend.AlphabeticComparator());
        }else{ // default
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

        @InjectView(R.id.friendStatus)
        TextView friendStatus;

        @InjectView(R.id.friendMessage)
        TextView friendMessage;

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

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }

        @Override
        public void onClick(View view) {
//            callback.onFriendClick(current);
        }
    }

    public interface OnItemClickAdapter{
        void onFriendClick(Friend friend);
    }

    public enum SortMethod{
        ALPHABETICALLY,
        ONLINE_FIRST,
        OFFLINE_FIRST;

        public boolean isSortAlphabetically(){
            return this.equals(SortMethod.ALPHABETICALLY);
        }

        public boolean isSortOnlineFirst(){
            return this.equals(SortMethod.ONLINE_FIRST);
        }

    }
}

package com.recoverrelax.pt.riotxmppchat.Adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edgelabs.pt.mybaseapp.R;
import com.recoverrelax.pt.riotxmppchat.Riot.Model.Friend;

import java.util.ArrayList;
import java.util.List;
import butterknife.ButterKnife;
import butterknife.InjectView;


public class FriendsListAdapter extends RecyclerView.Adapter<FriendsListAdapter.MyViewHolder> {

    List<Friend> friendsList;
    private LayoutInflater inflater;
    private Context context;
    private OnItemClickAdapter callback;
    private @LayoutRes int layout;

    public FriendsListAdapter(Context context, ArrayList<Friend> friendsList, int layout, OnItemClickAdapter callback){
        inflater= LayoutInflater.from(context);
        this.context = context;
        this.layout = layout;
        this.callback = callback;
        this.friendsList = friendsList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=inflater.inflate(layout, parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Friend friend = friendsList.get(position);
        holder.current = friend;
        holder.friendName.setText(friend.getName());

        holder.parent_row.setBackgroundColor(context.getResources().getColor(
                friend.getUserRosterPresence().isAvailable()
                ? R.color.online_alpha50
                : R.color.offline_alpha50));
    }

    public void setItems(List<Friend> items) {
        friendsList = items;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return friendsList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @InjectView(R.id.parent_row)
        LinearLayout parent_row;

        @InjectView(R.id.friendName)
        TextView friendName;

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
}

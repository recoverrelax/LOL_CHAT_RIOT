package com.recoverrelax.pt.riotxmppchat.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.LayoutRes;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppGlobals;
import com.recoverrelax.pt.riotxmppchat.R;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.RiotApiRealmDataVersion;
import com.recoverrelax.pt.riotxmppchat.Riot.Enum.GameStatus;
import com.recoverrelax.pt.riotxmppchat.Riot.Enum.PresenceMode;
import com.recoverrelax.pt.riotxmppchat.Riot.Model.Friend;
import com.recoverrelax.pt.riotxmppchat.Widget.AppProgressBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

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

public class FriendsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = FriendsListAdapter.class.getSimpleName();

    @Inject
    RiotApiRealmDataVersion realmData;

    private List<Friend> friendsList;
    private final LayoutInflater inflater;
    private final Context context;
    private OnAdapterChildClick onAdapterChildClickCallback;
    private final RecyclerView recyclerView;
    private StringBuilder stringBuilder = new StringBuilder();
    private final CompositeSubscription subscriptions = new CompositeSubscription();

    /**
     * Adapter RecyclerGradient Object
     */
    private GradientDrawable gradientDrawable;
    private Drawable drawable;
    
    @LayoutRes
    private final
    int layout_online = R.layout.friends_list_child_online_layout;
    
    @LayoutRes
    private final
    int layout_offline = R.layout.friends_list_child_offline_layout;

    private final int VIEW_HOLDER_ONLINE_ID = 0;
    private final int VIEW_HOLDER_OFFLINE_ID = 1;
    private boolean showOfflineUsers;

    private final
    int COLOR_BLACK;

    private final
    int COLOR_WHITE;

    public FriendsListAdapter(Context context, ArrayList<Friend> friendsList, boolean showOfflineUsers, RecyclerView recyclerView) {
        this.context = context;
        inflater = LayoutInflater.from(this.context);
        this.friendsList = friendsList;
        this.showOfflineUsers = showOfflineUsers;
        this.recyclerView = recyclerView;

        COLOR_BLACK = context.getResources().getColor(R.color.black);
        COLOR_WHITE = context.getResources().getColor(R.color.white);

        MainApplication.getInstance().getAppComponent().inject(this);
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
        final Friend friend = friendsList.get(position);
        final PresenceMode friendMode = friend.getFriendMode();

        switch (holder.getItemViewType()) {
            case VIEW_HOLDER_ONLINE_ID:

                final MyViewHolderOnline holderOnline = (MyViewHolderOnline) holder;

                holderOnline.current = friend;
                holderOnline.friendName.setText(holderOnline.current.getName());

                if (holderOnline.current.getGameStatus().equals(GameStatus.IN_QUEUE)
                        || holderOnline.current.getGameStatus().equals(GameStatus.INGAME)) {

                    holderOnline.startRepeatingTask();
                } else {
                    String statusMsg = holderOnline.current.getStatusMsg();
                    if(statusMsg != null && !statusMsg.equals(Friend.PERSONAL_MESSAGE_NO_VIEW)) {
                        holderOnline.gameStatus.setText(statusMsg);
                     }
                    holderOnline.stopRepeatingTask();
                }

                /**
                 * Check if the user is currently playing
                 */

                holderOnline.friendPresenceMode.setText(friendMode.getDescriptiveName());
                holderOnline.friendPresenceMode.setTextColor(COLOR_WHITE);

                gradientDrawable = null;
                gradientDrawable = (GradientDrawable) holderOnline.friendPresenceMode.getBackground();
                gradientDrawable.setColor(context.getResources().getColor(friendMode.getStatusColor()));


                holderOnline.wins.setText(holderOnline.current.getWins());
                holderOnline.ranked_icon.setImageDrawable(context.getResources().getDrawable(holderOnline.current.getProfileIconResId()));
                holderOnline.division_league.setText(holderOnline.current.getLeagueDivisionAndTier().getDescriptiveName());
                holderOnline.division_league.setSelected(true);

                /**
                 * Load Image from DD Server
                 */

                if (holderOnline.current.getProfileIconId().equals("")) {
                    Glide.with(context)
                            .load(R.drawable.profile_icon_example)
                            .into(holderOnline.profileIcon);
                } else {
//                    Picasso pic = Picasso.with(context);
//
//                    stringBuilder.delete(0, stringBuilder.length());
//                    stringBuilder.append(RiotGlobals.LOLKING_PROFILE_ICON_URL)
//                                 .append(holderOnline.current.getProfileIconId())
//                                 .append(RiotGlobals.LOLKING_PROFILE_ICON_EXTENSION);
//
//                    pic.load(stringBuilder.toString())
//                            .placeholder(R.drawable.profile_icon_example)
//                            .error(R.drawable.profile_icon_example)
//                            .into(holderOnline.profileIcon);

                    realmData.getProfileIconBaseUrl()
                            .subscribe(profileUrl -> {
                                Glide.with(context)
                                        .load(profileUrl + holderOnline.current.getProfileIconId() + AppGlobals.DD_VERSION.PROFILEICON_EXTENSION)
                                        .error(R.drawable.profile_icon_example)
                                        .into(new GlideDrawableImageViewTarget(holderOnline.profileIcon) {
                                            @Override
                                            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                                                super.onResourceReady(resource, animation);
                                                holderOnline.progressBarProfileIcon.setVisibility(View.GONE);
                                            }
                                        });
                            });
                }

                if(holderOnline.current.isPlaying()) {
//                    Picasso.with(context).load(holderOnline.current.getChampionDragonUrl())
//                            .into(holderOnline.championSquare);

                    realmData.getChampionDDBaseUrl()
                            .subscribe(championUrl -> {
                                Glide.with(context)
                                        .load(championUrl + holderOnline.current.getChampionNameFormatted()
                                                + AppGlobals.DD_VERSION.CHAMPION_EXTENSION)
                                        .into(holderOnline.championSquare);
                            });

                }
                else
                    holderOnline.championSquare.setImageDrawable(null);

                break;

            case VIEW_HOLDER_OFFLINE_ID:
                final MyViewHolderOffline holderOffline = (MyViewHolderOffline) holder;

                holderOffline.current = friend;
                holderOffline.friendName.setText(friend.getName());

                Glide.with(context)
                            .load(R.drawable.profile_icon_example)
                            .into(holderOffline.profileIcon);

                break;
        }
    }

    public void setItems(List<Friend> items) {
        friendsList = items;
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
     * Two types of Changes:
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
                    if(showOfflineUsers)
                        friendsList.add(newFriend);
                    notifyItemRemoved(positionFriend);
                } else {
                    // OFFLINE - OFFLINE
                    // do nothing!
                }
            }
        }else{
            if(newFriend.isOnline()) {
                friendsList.add(0, newFriend);
                notifyItemInserted(0);
            }
        }
    }

    public void removeSubscriptions(){
        subscriptions.clear();
    }

    @Override
    public int getItemCount() {
        return friendsList.size();
    }

    class MyViewHolderOnline extends RecyclerView.ViewHolder {

        @Bind(R.id.shardStatus1)
        TextView gameStatus;

        @Bind(R.id.friendPresenceMode)
        TextView friendPresenceMode;

        @Bind(R.id.division_league)
        TextView division_league;

        @Bind(R.id.wins)
        TextView wins;

        @Bind(R.id.ranked_icon)
        ImageView ranked_icon;

        @Bind(R.id.friendName)
        TextView friendName;

        @Bind(R.id.profileIcon)
        ImageView profileIcon;

        @Bind(R.id.progressBarProfileIcon)
        AppProgressBar progressBarProfileIcon;

        @Bind(R.id.card_more)
        ImageView card_more;

        @Bind(R.id.champion_square)
        ImageView championSquare;

        private Friend current;

        private final int updateInterval = 30000;
        private Subscription subscription;

        public MyViewHolderOnline(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            drawable = null;
            drawable = card_more.getDrawable();
            drawable.mutate();
            drawable.setColorFilter(COLOR_BLACK, PorterDuff.Mode.SRC_IN);
        }

        // ParentClick
        @OnClick(R.id.friends_list_cardview)
        public void onClick(View view) {
            if (onAdapterChildClickCallback != null) {
                onAdapterChildClickCallback.onAdapterFriendClick(current.getName(), current.getUserXmppAddress());
            }
        }

        // CardClick
        @OnClick(R.id.card_more_layout)
        public void onCardOptionsClick(View view) {
            if (onAdapterChildClickCallback != null)
                onAdapterChildClickCallback.onAdapterFriendOptionsClick(view, current.getUserXmppAddress(), current.getName(), current.isPlaying());
        }


        public void startRepeatingTask() {
            gameStatus.setText(current.getGameStatusToPrint());

            subscription = Observable.interval(updateInterval, TimeUnit.MILLISECONDS)
                    .map(aLong -> current.getGameStatusToPrint())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<String>() {
                        @Override public void onCompleted() { }
                        @Override public void onError(Throwable e) { }

                        @Override
                        public void onNext(String gameStatusToPrint) {
                            LOGI(TAG, "UI Adapter Updated: " + gameStatusToPrint);
                            gameStatus.setText(gameStatusToPrint);
                        }
                    });
            subscriptions.add(subscription);
        }

        void stopRepeatingTask() {
            subscriptions.remove(subscription);
        }
    }

    class MyViewHolderOffline extends RecyclerView.ViewHolder {

        @Bind(R.id.friendName)
        TextView friendName;

        @Bind(R.id.profileIcon)
        ImageView profileIcon;

        @Bind(R.id.card_more)
        ImageView card_more;

        Friend current;

        public MyViewHolderOffline(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            drawable = null;
            drawable = card_more.getDrawable();
            drawable.mutate();
            drawable.setColorFilter(COLOR_WHITE, PorterDuff.Mode.SRC_IN);
        }

        // RowClick
        @OnClick(R.id.friends_list_cardview)
        public void onClick(View view) {
                    Snackbar
                            .make(((Activity) context).getWindow().getDecorView().getRootView(),
                                    current.getName() + " " + context.getResources().getString(R.string.cannot_chat_with),
                                    Snackbar.LENGTH_LONG).show();
        }

        // CardClick
        @OnClick(R.id.card_more_layout)
        public void onCardOptionsClick(View view) {
            if (onAdapterChildClickCallback != null)
                onAdapterChildClickCallback.onAdapterFriendOptionsClick(view, current.getUserXmppAddress(), current.getName(), false);
        }
    }

    public interface OnAdapterChildClick {
        void onAdapterFriendClick(String friendUsername, String friendXmppAddress);
        void onAdapterFriendOptionsClick(View view, String friendXmppAddress, String friendUsername, boolean isPlaying);
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

package com.recoverrelax.pt.riotxmppchat.Adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.recoverrelax.pt.riotxmppchat.MyUtil.AppUtils.AppDateUtils;
import com.recoverrelax.pt.riotxmppchat.R;
import com.recoverrelax.pt.riotxmppchat.Database.MessageDirection;

import java.util.ArrayList;
import java.util.List;

import LolChatRiotDb.MessageDb;
import butterknife.Bind;
import butterknife.ButterKnife;

public class PersonalMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<MessageDb> personalMessageList;
    private LayoutInflater inflater;
    private Context context;
    private RecyclerView recyclerView;

    @LayoutRes
    int layout_from = R.layout.personal_message_from;
    @LayoutRes
    int layout_to = R.layout.personal_message_to;

    private final int VIEW_HOLDER_FROM_ID = 0;
    private final int VIEW_HOLDER_TO_ID = 1;

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
        MessageDb message = personalMessageList.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_HOLDER_FROM_ID:
                final MyViewHolderFrom holderFrom = (MyViewHolderFrom) holder;
                holderFrom.messageDb = message;
                AppDateUtils.setTimeElapsedWithHandler(holderFrom.date, holderFrom.messageDb.getDate());

                holderFrom.message.setText(message.getMessage());
                break;

            case VIEW_HOLDER_TO_ID:
                final MyViewHolderTo holderTo = (MyViewHolderTo) holder;
                holderTo.messageDb = message;

                AppDateUtils.setTimeElapsedWithHandler(holderTo.date, holderTo.messageDb.getDate());

                holderTo.message.setText(message.getMessage());
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

    class MyViewHolderFrom extends RecyclerView.ViewHolder {

        @Bind(R.id.message)
        TextView message;

        @Bind(R.id.date)
        TextView date;

        MessageDb messageDb;

        public MyViewHolderFrom(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class MyViewHolderTo extends RecyclerView.ViewHolder {

        @Bind(R.id.message)
        TextView message;

        @Bind(R.id.date)
        TextView date;

        MessageDb messageDb;

        public MyViewHolderTo(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

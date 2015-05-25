package com.recoverrelax.pt.riotxmppchat.MyUtil.drawer;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.recoverrelax.pt.riotxmppchat.MyUtil.drawer.Entities.DrawerItemsInfo;
import com.edgelabs.pt.mybaseapp.R;

import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class DrawerAdapter extends RecyclerView.Adapter<DrawerAdapter.MyViewHolder> {

    List<DrawerItemsInfo> data = Collections.emptyList();
    private LayoutInflater inflater;
    private Context context;
    private DrawerAdapterItemSelectedCallback drawerAdapterItemSelectedCallback;
    private int CURRENT_POSITION;

    public DrawerAdapter(Context context, List<DrawerItemsInfo> data, DrawerAdapterItemSelectedCallback drawerAdapterItemSelectedCallback,
                         int currentSelectedDrawerItem) {
        this.context = context;

        inflater = LayoutInflater.from(context);
        this.data = data;
        this.drawerAdapterItemSelectedCallback = drawerAdapterItemSelectedCallback;
        this.CURRENT_POSITION = currentSelectedDrawerItem;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        DrawerItemsInfo current = data.get(position);
        holder.title.setText(current.title);
        holder.icon.setImageResource(current.iconId);
        holder.position = position;

        if (holder.position == CURRENT_POSITION) {
            holder.icon.setImageResource(current.iconTId);
            holder.title.setTextColor(context.getResources().getColor(R.color.primaryColor));
        } else {
            holder.icon.setImageResource(current.iconId);
            holder.title.setTextColor(context.getResources().getColor(R.color.black));
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.listText)
        TextView title;
        @InjectView(R.id.listIcon)
        ImageView icon;

        int position;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }

        @OnClick(R.id.drawer_list_item)
        public void onRowClick(View v) {
            drawerAdapterItemSelectedCallback.onDrawerItemSelected(position);
        }
    }

    public void setCurrentPosition(int position){
        this.CURRENT_POSITION = position;
        notifyDataSetChanged();
    }
}

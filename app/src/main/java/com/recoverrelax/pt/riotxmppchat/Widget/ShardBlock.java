package com.recoverrelax.pt.riotxmppchat.Widget;

import android.content.Context;
import android.support.percent.PercentRelativeLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.recoverrelax.pt.riotxmppchat.R;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ShardBlock extends PercentRelativeLayout {

    @Bind({R.id.shardName1, R.id.shardName2, R.id.shardName3, R.id.shardName4})
    List<TextView> shardNameBlock;

    @Bind({R.id.shardStatus1, R.id.shardStatus2, R.id.shardStatus3, R.id.shardStatus4})
    List<TextView> shardStatusBlock;

    @Bind(R.id.mainContent)
    PercentRelativeLayout mainContent;

    @Bind(R.id.progressBar)
    AppProgressBar progressBar;

    private Context context;

    public ShardBlock(Context context) {
        super(context);
        this.context = context;
        inflateLayout();
    }

    public ShardBlock(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        inflateLayout();
    }

    public ShardBlock(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        inflateLayout();
    }

    public void inflateLayout() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.shard_block_layout, this);
        ButterKnife.bind(this);
    }

    public void enableProgressBar(boolean state) {
        progressBar.setVisibility(state ? View.VISIBLE : View.GONE);
        mainContent.setVisibility(state ? View.INVISIBLE : View.VISIBLE);
    }

    public AppProgressBar getProgressBar() {
        return progressBar;
    }

    public List<TextView> getShardNameBlock() {
        return shardNameBlock;
    }

    public List<TextView> getShardStatusBlock() {
        return shardStatusBlock;
    }

    public PercentRelativeLayout getMainContent() {
        return mainContent;
    }
}

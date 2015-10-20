package com.recoverrelax.pt.riotxmppchat.Widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.recoverrelax.pt.riotxmppchat.R;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FreeChampionRotation extends FrameLayout {

    @Bind({R.id.champ1, R.id.champ2, R.id.champ3, R.id.champ4, R.id.champ5,
                  R.id.champ6, R.id.champ7, R.id.champ8, R.id.champ9, R.id.champ10})
    List<SquareImageViewWidth> getFreeChamps;

    @Bind(R.id.championRotationProgressBar)
    AppProgressBar championRotationProgressBar;

    private Context context;

    public FreeChampionRotation(Context context) {
        super(context);
        this.context = context;
        inflateLayout();
    }

    public FreeChampionRotation(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        inflateLayout();
    }

    public FreeChampionRotation(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        inflateLayout();
    }

    public void inflateLayout() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.free_champ_rotation_layout, this);
        ButterKnife.bind(this);
    }

    public List<SquareImageViewWidth> getGetFreeChamps() {
        return getFreeChamps;
    }

    public void showProgressBar(boolean state) {
        championRotationProgressBar.setVisibility(state ? View.VISIBLE : View.GONE);
    }
}

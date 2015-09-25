package com.recoverrelax.pt.riotxmppchat.Widget;

import android.content.Context;
import android.util.AttributeSet;

import com.recoverrelax.pt.riotxmppchat.R;

public class CurrentGameSingleParticipantLeft extends CurrentGameSingleParticipantBase {

    public CurrentGameSingleParticipantLeft(Context context) {
        super(context);
    }

    public CurrentGameSingleParticipantLeft(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CurrentGameSingleParticipantLeft(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public int getLayout() {
        return R.layout.current_game_participant_custom_left_layout;
    }
}

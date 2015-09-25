package com.recoverrelax.pt.riotxmppchat.Widget;

import android.content.Context;
import android.util.AttributeSet;

import com.recoverrelax.pt.riotxmppchat.R;

public class CurrentGameSingleParticipantRight extends CurrentGameSingleParticipantBase {

    public CurrentGameSingleParticipantRight(Context context) {
        super(context);
    }

    public CurrentGameSingleParticipantRight(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CurrentGameSingleParticipantRight(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public int getLayout() {
        return R.layout.current_game_participant_custom_right_layout;
    }
}

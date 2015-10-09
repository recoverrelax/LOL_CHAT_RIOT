package com.recoverrelax.pt.riotxmppchat.MyUtil;

import android.app.Activity;
import android.support.annotation.StringDef;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;

import com.recoverrelax.pt.riotxmppchat.R;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.RiotApiError;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.RiotApiErrorEnum;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import pt.reco.myutil.MyContext;

public class AppRiotHttpUtils {


    protected int SNACKBAR_ERROR_DURATION = Snackbar.LENGTH_LONG;
    /**
     * Error Category
     */


    public static final String ERROR_RECENT_GAME = "recent_game_errors";
    public static final String ERROR_CURRENT_GAME = "current_game_errors";

    @StringDef({ERROR_RECENT_GAME, ERROR_CURRENT_GAME})
    @Retention(RetentionPolicy.SOURCE)
    public @interface API_ERROR_TYPES {}

    /**
     * Error for each Category
     */

    public AppRiotHttpUtils(){}

    RiotApiError [] recentGameErrors =
            new RiotApiError[]{
                    new RiotApiError(RiotApiErrorEnum.E_404, R.string.recent_game_not_found),
                    new RiotApiError(RiotApiErrorEnum.DEFAULT, R.string.recent_game_try_later)
            };

    RiotApiError [] currentGameErrors =
            new RiotApiError[]{
                    new RiotApiError(RiotApiErrorEnum.E_404, R.string.current_game_not_found),
                    new RiotApiError(RiotApiErrorEnum.DEFAULT, R.string.recent_game_try_later)
            };




    @API_ERROR_TYPES
    public void handleErrors(Fragment fragment, Throwable e, @API_ERROR_TYPES String errorCategory) {
        handleErrors(fragment.getActivity(), e, errorCategory);
    }

    @API_ERROR_TYPES
    public void handleErrors(Activity activity, Throwable e, @API_ERROR_TYPES String errorCategory) {

        RiotApiError [] errors = getErrorsByCategory(errorCategory);

        if(errors == null)
            return;

        @StringRes Integer errorRes = null;

        for(RiotApiError error: errors){
            if(error.getError().is404()){
                errorRes = error.getErrorRes();
            }
        }

        if(errorRes != null){
            MyContext.showSnackbar(activity, errorRes, SNACKBAR_ERROR_DURATION);
        }
    }

    private RiotApiError [] getErrorsByCategory(String errorCategory) {
        if(errorCategory.equals(ERROR_RECENT_GAME)){
           return recentGameErrors;
        }else if(errorCategory.equals(ERROR_CURRENT_GAME))
           return currentGameErrors;
        else{
            return null;
        }
    }
}

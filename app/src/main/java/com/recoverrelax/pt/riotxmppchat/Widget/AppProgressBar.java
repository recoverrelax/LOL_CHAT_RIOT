package com.recoverrelax.pt.riotxmppchat.Widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.support.v7.internal.widget.ThemeUtils;
import android.util.AttributeSet;
import android.view.View;

import com.recoverrelax.pt.riotxmppchat.MyUtil.AppMiscUtils;
import com.recoverrelax.pt.riotxmppchat.R.attr;
import com.recoverrelax.pt.riotxmppchat.R.id;
import com.recoverrelax.pt.riotxmppchat.R.styleable;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AppProgressBar extends RelativeLayout {
    private static final String EMPTY = "";
    private int mProgressBarTintColor;
    private TextView mTextView;
    private ProgressBar mProgressBar;
    private View mMainView;

    public AppProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init(attrs, context);
    }

    public AppProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.init(attrs, context);
    }

    @TargetApi(21)
    private void init(AttributeSet attrs, Context ctx) {
        this.mTextView = new TextView(ctx);
        this.mTextView.setId(id.textprogressbar_text);
        this.mProgressBar = new ProgressBar(ctx, (AttributeSet) null, 16842874);
        this.mProgressBar.setId(id.textprogressbar_progressbar);
        this.mProgressBar.setIndeterminate(true);
        LayoutParams pgbParam = new LayoutParams(-2, -2);
        pgbParam.addRule(13, -1);
        this.mProgressBar.setLayoutParams(pgbParam);
        this.mProgressBarTintColor = ThemeUtils.getThemeAttrColor(ctx, attr.textProgressBarTintColor);
        this.mProgressBarTintColor = this.mProgressBarTintColor == 0 ? ThemeUtils.getThemeAttrColor(ctx, attr.colorPrimary) : this.mProgressBarTintColor;
        LayoutParams tvParam = new LayoutParams(-2, -2);
        tvParam.addRule(14, -1);
        tvParam.addRule(3, this.mProgressBar.getId());
        this.mTextView.setLayoutParams(tvParam);
        this.mTextView.setGravity(17);
        if (attrs != null) {
            TypedArray a = ctx.obtainStyledAttributes(attrs, styleable.TextProgressBar, 0, 0);
            String loadingMsg = a.getString(styleable.TextProgressBar_text);
            this.mTextView.setText(loadingMsg != null ? loadingMsg : "");
            int textAppearence = a.getResourceId(styleable.TextProgressBar_textAppearence, -1);
            if (textAppearence != -1) {
                this.mTextView.setTextAppearance(ctx, textAppearence);
            }

            int textColor = a.getColor(styleable.TextProgressBar_textColor, -1);
            if (textColor != -1) {
                this.mTextView.setTextColor(textColor);
            }

            int textSize = a.getDimensionPixelSize(styleable.TextProgressBar_textSize, -1);
            if (textSize != -1) {
                this.mTextView.setTextSize((float) textSize);
            }

            String fontFamily = a.getString(styleable.TextProgressBar_fontFamily);
            if (fontFamily != null) {
                Typeface tf = Typeface.create(fontFamily, 0);
                this.mTextView.setTypeface(tf);
            }

            this.mProgressBarTintColor = a.getColor(styleable.TextProgressBar_progressBarTintColor, this.mProgressBarTintColor);
            a.recycle();
        }

        if (AppMiscUtils.hasMinimumVersion(21)) {
            this.mProgressBar.setIndeterminateTintMode(PorterDuff.Mode.SRC_ATOP);
            this.mProgressBar.setIndeterminateTintList(ColorStateList.valueOf(this.mProgressBarTintColor));
        } else {
            this.mProgressBar.getIndeterminateDrawable().setColorFilter(this.mProgressBarTintColor, PorterDuff.Mode.SRC_ATOP);
        }

        this.addView(this.mProgressBar);
        this.addView(this.mTextView);
    }

    public void setMainView(View mainView) {
        this.mMainView = mainView;
    }

    public void show() {
        this.showProgress(true);
    }

    public void hide() {
        this.showProgress(false);
    }

    public void showText() {
        this.mTextView.setVisibility(View.VISIBLE);
    }

    public void hideText() {
        this.mTextView.setVisibility(View.GONE);
    }

    public void setText(String string) {
        this.mTextView.setText(string != null ? string : "");
    }

    public boolean isVisible() {
        return this.getVisibility() == View.VISIBLE;
    }

    private void showProgress(final boolean show) {
        byte shortAnimTime = 0;
        this.setVisibility(show ? View.VISIBLE : View.GONE);
        if (this.mMainView != null) {
            this.mMainView.setVisibility(show ? View.GONE : View.VISIBLE);
        }

        this.animate().setDuration((long) shortAnimTime).alpha(show ? 1.0F : 0.0F).setListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                AppProgressBar.this.setVisibility(show ? View.VISIBLE : View.GONE);
                if (AppProgressBar.this.mMainView != null) {
                    AppProgressBar.this.mMainView.setVisibility(show ? View.GONE : View.VISIBLE);
                }

            }
        });
    }
}
package com.recoverrelax.pt.riotxmppchat.MyUtil;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import rx.subscriptions.CompositeSubscription;

public class AppMVPHelper {

    public interface RecyclerViewPresenter extends BasePresenter {
        void configRecyclerView();

        void configAdapter(RecyclerView recyclerView);
    }

    public interface BasePresenter {
        void onResume();

        void onPause();
    }

    public interface RecyclerViewPresenterCallbacks<A> {
        void setRecyclerViewLayoutParams(RecyclerView.LayoutManager layoutManager);

        void setRecyclerViewAdapter(A adapter);
    }

    public static class BasePresenterImpl<M, A> {

        protected Context context;
        protected M model;
        protected A adapter;
        protected CompositeSubscription subscriptions = new CompositeSubscription();

        public BasePresenterImpl(M model, Context context) {
            this.model = model;
            this.context = context;
        }
    }
}

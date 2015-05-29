package com.recoverrelax.pt.riotxmppchat.ui.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.edgelabs.pt.mybaseapp.R;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppUtils.AndroidUtils;
import com.recoverrelax.pt.riotxmppchat.MyUtil.drawer.DividerItemDecoration;
import com.recoverrelax.pt.riotxmppchat.MyUtil.drawer.DrawerAdapter;
import com.recoverrelax.pt.riotxmppchat.MyUtil.drawer.DrawerAdapterItemSelectedCallback;
import com.recoverrelax.pt.riotxmppchat.MyUtil.drawer.ENavDrawer;
import com.recoverrelax.pt.riotxmppchat.MyUtil.drawer.Entities.DrawerItemsInfo;
import com.recoverrelax.pt.riotxmppchat.MyUtil.storage.DataStorage;
import com.recoverrelax.pt.riotxmppchat.ui.activity.SettingActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * A simple {@link Fragment} subclass.
 */
public class NavigationDrawerFragment extends Fragment implements DrawerAdapterItemSelectedCallback {

    @InjectView(R.id.drawerList)
    RecyclerView recyclerView;

    @InjectView(R.id.drawer_username)
    TextView drawer_username;

    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawerLayout;
    private DataStorage sDataStorage = DataStorage.getInstance();

    private DrawerAdapter adapter;
    private Handler mHandler;

    // delay to launch nav drawer item, to allow close animation to play
    private static final int NAVDRAWER_LAUNCH_DELAY = 250;

    private boolean userLearnedDrawer;
    private boolean fromSavedInstanceState;
    private int CURRENT_SELECTED_ITEM = 0;

    public NavigationDrawerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userLearnedDrawer = sDataStorage.userLearnedDrawer();

        if(savedInstanceState != null){
            fromSavedInstanceState = true;
        }

        mHandler = new Handler();
    }

    public List<DrawerItemsInfo> getData() {
        //load only static data inside a drawer
        List<DrawerItemsInfo> data = new ArrayList<>();

        for(int i = 0; i < ENavDrawer.SIZE; i++) {
//        for(int i = 0; i < 1; i++) {
            ENavDrawer navDrawerEnum = ENavDrawer.getById(i);
            if(navDrawerEnum != null)
                data.add(new DrawerItemsInfo(navDrawerEnum.getNavDrawerDrawableId(), navDrawerEnum.getNavDrawerDrawableTranspId(), navDrawerEnum.getNavDrawerTitle()));
        }

        return data;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        drawer_username.setText(getResources().getString(R.string.drawer_default_username_prefix) + " " +
                DataStorage.getInstance().getUsername());

        adapter = new DrawerAdapter(getActivity(), getData(), this, CURRENT_SELECTED_ITEM);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        // allows for optimizations if all item views are of the same size:
        recyclerView.setHasFixedSize(true);
        //recyclerView.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.drawer_divider), 2));
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), null, 2));
        }

    public void setup(int fragmentId, DrawerLayout dl, Toolbar tb) {

        adapter.notifyDataSetChanged();
        View containerView = ButterKnife.findById(getActivity(), fragmentId);

        drawerLayout = dl;
        drawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, tb, R.string.drawer_opened, R.string.drawer_closed) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if(!userLearnedDrawer){
                    sDataStorage.setUserLearnedDrawer();
                }

                // activity should redraw the menu
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActivity().invalidateOptionsMenu();
            }
        };

        if(!userLearnedDrawer && !fromSavedInstanceState){
            drawerLayout.openDrawer(containerView);
        }

        drawerLayout.setDrawerListener(drawerToggle);

        drawerLayout.post(new Runnable() {
            @Override
            public void run() {
                drawerToggle.syncState();
            }
        });
//        onDrawerItemSelected(ENavDrawer.NAVDRAWER_ITEM_0.getNavDrawerId());

        drawerLayout.setScrimColor(getResources().getColor(R.color.blackDrawerScrim));
    }

    /**
     *
     * @param position: position of the drawerItem or -1 for position-unchanged
     */
    @Override
    public void onDrawerItemSelected(final int position) {

        final FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        Fragment fragment = null;

        /**
         * Detect if its the first time
         */

        if(CURRENT_SELECTED_ITEM == position){
            drawerLayout.closeDrawer(Gravity.START);
            return;
        }else if(position == ENavDrawer.NAVDRAWER_ITEM_2.getNavDrawerId()) {

            Intent intent = new Intent(getActivity(), SettingActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
            AndroidUtils.overridePendingTransitionAppDefault(getActivity());
            drawerLayout.closeDrawer(Gravity.START);
            return;

        }else {
            fragment = ENavDrawer.getById(position).getFrag();
        }

        transaction.setCustomAnimations(R.anim.zoom_enter, R.anim.zoom_exit, R.anim.fade_in, R.anim.fade_out);
        transaction.addToBackStack(null);
        transaction.replace(R.id.container, fragment);

        drawerLayout.closeDrawer(Gravity.START);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                transaction.commit();
            }
        }, NAVDRAWER_LAUNCH_DELAY);
        setCurrentSelectedItem(position);
    }

    public int getCurrentSelectedItem() {
        return CURRENT_SELECTED_ITEM;
    }

    public void setCurrentSelectedItem(int position) {
        this.CURRENT_SELECTED_ITEM = position;
        this.adapter.setCurrentPosition(position);
    }
}

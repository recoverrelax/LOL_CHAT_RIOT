package com.recoverrelax.pt.riotxmppchat.ui.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppContextUtils;
import com.recoverrelax.pt.riotxmppchat.R;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.Status.Service;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.RiotApiOperations;
import com.recoverrelax.pt.riotxmppchat.Riot.Enum.RiotServer;
import com.recoverrelax.pt.riotxmppchat.Riot.Enum.ShardStatus;
import com.recoverrelax.pt.riotxmppchat.Storage.DataStorage;
import com.recoverrelax.pt.riotxmppchat.Widget.ShardBlock;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemSelected;
import pt.reco.myutil.MyContext;
import rx.Subscriber;
import rx.Subscription;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShardFragment extends BaseFragment {

    private final String TAG = ShardFragment.this.getClass().getSimpleName();

    @Bind(R.id.shardBlock)
    ShardBlock shardBlock;

    @Bind(R.id.serverName)
    TextView serverName;

    @Bind(R.id.regionSpinner)
    Spinner regionSpinner;

    @Inject
    RiotApiOperations riotApiOperations;

    @Inject
    DataStorage dataStorage;

    private Subscription subscription;
    private boolean spinnerFirstTime = true;

    public ShardFragment() {
        // Required empty public constructor
    }

    public static ShardFragment newInstance() {
        return new ShardFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainApplication.getInstance().getAppComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.shard_fragment, container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.getActivity(), R.layout.spinner_layout_white, R.id.server_textview, RiotServer.getServerList());
        adapter.setDropDownViewResource(R.layout.spinner_layout);
        regionSpinner.setAdapter(adapter);

        subscription = getShardIncident(getRegion());
    }

    @OnItemSelected(R.id.regionSpinner)
    public void OnSelectRegion(AdapterView<?> adapterView, View view, int i, long l){
        if(!spinnerFirstTime) {
            RiotServer byServerPosition = RiotServer.getByServerPosition(i);

            if (!subscription.isUnsubscribed())
                subscription.unsubscribe();
            getShardIncident(byServerPosition);
        }else
            spinnerFirstTime = false;
    }

    public Subscription getShardIncident(RiotServer server){
            return
                    riotApiOperations.getShardIncidents(server.getServerRegion())
                            .doOnSubscribe(() -> shardBlock.enableProgressBar(true))
                            .doOnUnsubscribe(() -> shardBlock.enableProgressBar(false))
                            .subscribe(new Subscriber<Pair<String, List<Service>>>() {
                                @Override
                                public void onCompleted() {
                                    this.unsubscribe();
                                }

                                @Override
                                public void onError(Throwable e) {
                                    String error = ShardFragment.this.getActivity().getResources().getString(R.string.shard_region_error) + " " + (String)regionSpinner.getSelectedItem();
                                    AppContextUtils.showSnackbar(ShardFragment.this.getBaseActivity(), error, Snackbar.LENGTH_LONG, null);
                                    this.unsubscribe();
                                }

                                @Override
                                public void onNext(Pair<String, List<Service>> stringListPair) {
                                    fetchShardInfo(stringListPair);
                                    regionSpinner.setSelection(server.getPosition());
                                }
                            });

    }

    private void fetchShardInfo(Pair<String, List<Service>> stringListPair) {
        String serverName = stringListPair.first;
        List<Service> serviceList = stringListPair.second;

        this.serverName.setText(serverName);

        for(int i = 0; i < serviceList.size(); i++){
            shardBlock.getShardNameBlock().get(i).setText(serviceList.get(i).getName());

            String status = serviceList.get(i).getStatus().toUpperCase();
            ShardStatus shardEnum = ShardStatus.getByName(status);

            TextView shardTextView = shardBlock.getShardStatusBlock().get(i);
            shardTextView.setText(shardEnum.getDescriptiveNameUpperCase());
            shardTextView.setTextColor(MyContext.getColor(this.getActivity(), shardEnum.getStatusColor()));
        }
    }

    private @Nullable RiotServer getRegion() {
        String server = dataStorage.getServer();
        return RiotServer.getRiotServerByName(server);
    }
}

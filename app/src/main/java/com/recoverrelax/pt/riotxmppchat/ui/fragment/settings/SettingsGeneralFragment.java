package com.recoverrelax.pt.riotxmppchat.ui.fragment.settings;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppGlobals;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppMiscUtils;
import com.recoverrelax.pt.riotxmppchat.Storage.DataStorage;
import com.recoverrelax.pt.riotxmppchat.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;
import rx.android.view.ViewObservable;

import static com.recoverrelax.pt.riotxmppchat.MyUtil.LogUtils.LOGI;

public class SettingsGeneralFragment extends Fragment {

    @Bind(R.id.championName)
    EditText championName;

    @Bind(R.id.download)
    Button download;

    private String connectedXmppUser;

    @Inject DataStorage dataStorage;
    private Target target;

    public SettingsGeneralFragment() {

    }

    public static SettingsGeneralFragment newInstance() {
        return new SettingsGeneralFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainApplication.getInstance().getAppComponent().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings_general_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViewObservable.clicks(download)
                .map(onClickEventObservable -> championName.getText() != null && championName.getText().length() > 3)
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        LOGI("123", "OnClick don't pass");
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (aBoolean) {
                            LOGI("123", "OnClick pass with true");
                            downloadImages();
                        } else
                            LOGI("123", "OnClick pass with false");
                    }
                });
    }

    private void downloadImages(){

        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setIndeterminate(true);
        progressDialog.setTitle("Downloading images, please wait");
        progressDialog.show();

        Observable.range(1, Integer.MAX_VALUE)
                .concatMap(integer -> downloadSingleImageObservable(championName.getText().toString(), integer))
                .takeWhile(file -> {

                    LOGI("123", file == null ? "fileNull" : "fileNotNull");

                    return file != null;
                })
                .subscribe(new Subscriber<File>() {
                    @Override
                    public void onCompleted() {
                        LOGI("123", "onCompleted");
                        progressDialog.hide();
                    }

                    @Override
                    public void onError(Throwable e) {
                        LOGI("123", "onError");
                        progressDialog.hide();
                    }

                    @Override
                    public void onNext(File file) {
                        LOGI("123", "Successfully saved: " + file.getAbsolutePath());
                    }
                });
    }

    public void downloadImages2(){
        downloadSingleImageObservable(championName.getText().toString(), 1)
                .subscribe(new Subscriber<File>() {
                    @Override
                    public void onCompleted() {
                        LOGI("123", "onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        LOGI("123", "onError");
                        LOGI("123", e.toString());
                    }

                    @Override
                    public void onNext(File file) {
                        LOGI("123", "Successfully saved: " + file.getAbsolutePath());
                    }
                });
    }

    private Observable<File> downloadSingleImageObservable(String championName, int skinNumber) {

        String finalChampionString = championName.substring(0, 1).toUpperCase() + championName.substring(1).toLowerCase();

        return Observable.create(subscriber -> {

            target = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                    Bitmap bitMapScaled = resizeAndCrop(bitmap);

                    File file = new File(AppMiscUtils.getAppSpecificFolder(getActivity()).getPath() + "/champion_skins/" + finalChampionString + skinNumber + ".jpg");

                    try {
                        FileOutputStream ostream = new FileOutputStream(file);
                        bitMapScaled.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
                        ostream.close();

                        bitMapScaled.recycle();
                        bitMapScaled = null;

                        subscriber.onNext(file);
                        subscriber.onCompleted();
                    } catch (IOException e) {
                        e.printStackTrace();
                        subscriber.onError(new Exception("Can't save file"));
                    }
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                    LOGI("123", "Entered onBitmapFailed");
                    subscriber.onError(new Exception("Bitmap failed to Load"));
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                }
            };

            String imageUrl = getImageUrl(championName, skinNumber);
            LOGI("123", "Built url: " + imageUrl);

            Picasso.with(getActivity())
                    .load(imageUrl)
                    .into(target);
        });
    }

    private String getImageUrl(String championName, int skinNumber) {

        // Format the ChampionName
        // First all to lowerCase
        String finalChampionString = championName.substring(0, 1).toUpperCase() + championName.substring(1).toLowerCase() + "_" + skinNumber;

        return AppGlobals.RiotEndPoint.CHAMPION_SKINS + finalChampionString + ".jpg";
    }

    private Bitmap resizeAndCrop(Bitmap image) {

        int maxWidth = image.getWidth();

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

        int maxHeight = displaymetrics.heightPixels;

        if (maxHeight > 0 && maxWidth > 0) {
            int width = image.getWidth();
            int height = image.getHeight();
            float ratioBitmap = (float) width / (float) height;
            float ratioMax = (float) maxWidth / (float) maxHeight;

            int finalWidth = maxWidth;
            int finalHeight = maxHeight;
            if (ratioMax > 1) {
                finalWidth = (int) ((float)maxHeight * ratioBitmap);
            } else {
                finalHeight = (int) ((float)maxWidth / ratioBitmap);
            }
            image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);

            Bitmap cropped = Bitmap.createBitmap(
                    image,
                    image.getWidth()/3,
                    0,
                    image.getWidth()-(image.getWidth()/3),
                    image.getHeight());

            image.recycle();
            return cropped;
        } else {
            return image;
        }
    }


}

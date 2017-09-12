package com.aioplayer.fragment;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.aioplayer.MainActivity;
import com.aioplayer.R;
import com.aioplayer.application.SongApplication;
import com.aioplayer.event.PhotoCastEvent;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;


/**
 * Created by akankshadhanda on 27/07/17.
 */

public class ImageViewer extends BaseFragment {
    private String url;
    private MainActivity mainActivity;
    public ImageViewer(String imageUrl)
    {
        this.url=imageUrl;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mainActivity=(MainActivity)context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ImageView imageView=(ImageView)inflater.inflate(R.layout.photo_item_view,container,false);
        imageView.setImageURI(Uri.parse(url));
        if(mainActivity.getCasty().isConnected())
        {
            PhotoCastEvent photoCastEvent = new PhotoCastEvent(url);

            ((SongApplication)mainActivity.getApplication()).getEventBus().post(photoCastEvent);
        }
        return imageView;

    }

    @Override
    public void onPause() {
        super.onPause();
        getMainActivity().getSupportActionBar().setHomeAsUpIndicator(new IconicsDrawable(getMainActivity(), GoogleMaterial.Icon.gmd_menu).color(Color.WHITE).sizeDp(IconicsDrawable.ANDROID_ACTIONBAR_ICON_SIZE_DP).paddingDp(IconicsDrawable.ANDROID_ACTIONBAR_ICON_SIZE_PADDING_DP));
        getMainActivity().setShowBackIconButton(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        getMainActivity().getSupportActionBar().setHomeAsUpIndicator(new IconicsDrawable(getMainActivity(), GoogleMaterial.Icon.gmd_arrow_back).color(Color.WHITE).sizeDp(IconicsDrawable.ANDROID_ACTIONBAR_ICON_SIZE_DP).paddingDp(IconicsDrawable.ANDROID_ACTIONBAR_ICON_SIZE_PADDING_DP));
        getMainActivity().setShowBackIconButton(true);
    }
}

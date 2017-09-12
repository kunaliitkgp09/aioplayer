package com.aioplayer.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.aioplayer.MainActivity;
import com.aioplayer.R;
import com.aioplayer.adapter.PhotoAlbumsAdapter;
import com.aioplayer.dao.PhonePhoto;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import java.util.Vector;


/**
 * Created by akankshadhanda on 27/07/17.
 */

public class ImageinGalleryViewerFragment extends BaseFragment {
    private Vector<PhonePhoto> phonePhotos;
    private MainActivity mainActivity;
    public ImageinGalleryViewerFragment(Vector<PhonePhoto> photoVectors)
    {
        this.phonePhotos=photoVectors;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mainActivity=(MainActivity)context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_photo_gallery,container,false);
        PhotoAlbumsAdapter photoAlbumsAdapter = new PhotoAlbumsAdapter(phonePhotos,mainActivity);
        GridView gridView=(GridView) view.findViewById(R.id.gridview);
        gridView.setAdapter(photoAlbumsAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                PhonePhoto phonePhoto=phonePhotos.get(i);
                mainActivity.showNextView(new ImageViewer(phonePhoto.getPhotoUri()),null);
            }
        });

        return view;

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

package com.aioplayer.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.aioplayer.R;
import com.aioplayer.dao.PhonePhoto;

import java.util.List;



/**
 * Created by akankshadhanda on 27/07/17.
 */

public class PhotoAlbumsAdapter extends BaseAdapter {

    private List<PhonePhoto> photoAlbumList;
    private Context context;
    public PhotoAlbumsAdapter(List<PhonePhoto> photoAlbumList, Context context)
    {
        this.photoAlbumList=photoAlbumList;
        this.context=context;
    }
    @Override
    public int getCount() {
        return photoAlbumList.size();
    }

    @Override
    public PhonePhoto getItem(int i) {
        return photoAlbumList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater)   context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.gallery_photo_item_layout, null);
        ImageView albumArt =(ImageView) view.findViewById(R.id.albumart);
        PhonePhoto photoAlbum=getItem(i);
        albumArt.setImageURI(Uri.parse(photoAlbum.getPhotoUri()));
        return view;
    }
}

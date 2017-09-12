package com.aioplayer.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aioplayer.R;
import com.aioplayer.dao.PhotoAlbum;

import java.util.List;



/**
 * Created by akankshadhanda on 27/07/17.
 */

public class GalleryPhotoGridViewAdapter extends BaseAdapter {

    private List<PhotoAlbum> photoAlbumList;
    private Context context;
    public GalleryPhotoGridViewAdapter(List<PhotoAlbum> photoAlbumList, Context context)
    {
        this.photoAlbumList=photoAlbumList;
        this.context=context;
    }
    @Override
    public int getCount() {
        return photoAlbumList.size();
    }

    @Override
    public PhotoAlbum getItem(int i) {
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
        PhotoAlbum photoAlbum=getItem(i);
        albumArt.setImageURI(Uri.parse(photoAlbum.getCoverUri()));
        TextView albumName =(TextView) view.findViewById(R.id.albumname);
        albumName.setText(photoAlbum.getName());
        return view;
    }
}

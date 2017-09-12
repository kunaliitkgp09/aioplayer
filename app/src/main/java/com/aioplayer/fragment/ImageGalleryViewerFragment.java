package com.aioplayer.fragment;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.aioplayer.MainActivity;
import com.aioplayer.R;
import com.aioplayer.adapter.GalleryPhotoGridViewAdapter;
import com.aioplayer.dao.PhonePhoto;
import com.aioplayer.dao.PhotoAlbum;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import java.util.LinkedList;
import java.util.List;



/**
 * Created by akankshadhanda on 27/07/17.
 */

public class ImageGalleryViewerFragment extends BaseFragment {
    private GridView gridView;
    private MainActivity mainActivity;
    private List<PhotoAlbum> phonePhoto;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mainActivity=(MainActivity)context;
    }

    @Override
    public String getTAG() {
        return "imagegalleryviewerfragment";
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_photo_gallery,container,false);
        gridView=(GridView)view.findViewById(R.id.gridview);
        GalleryPhotoGridViewAdapter gridViewAdapter= new GalleryPhotoGridViewAdapter(getPhoneAlbums(mainActivity),mainActivity);
        gridView.setAdapter(gridViewAdapter);
        phonePhoto=getPhoneAlbums(mainActivity);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mainActivity.showNextView(new ImageinGalleryViewerFragment(phonePhoto.get(i).getAlbumPhotos()),null);
            }
        });
        return view;

    }

    public List<PhotoAlbum> getPhoneAlbums(Context context) {
        // Creating vectors to hold the final albums objects and albums names
        List<PhotoAlbum> phoneAlbums = new LinkedList<>();
        List<String> albumsNames = new LinkedList<>();

        // which image properties are we querying
        String[] projection = new String[]{
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media._ID
        };

        // content: style URI for the "primary" external storage volume
        Uri images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        // Make the query.
        Cursor cur = context.getContentResolver().query(images,
                projection, // Which columns to return
                null,       // Which rows to return (all rows)
                null,       // Selection arguments (none)
                null        // Ordering
        );

        if (cur != null && cur.getCount() > 0) {
            Log.i("DeviceImageManager", " query count=" + cur.getCount());

            if (cur.moveToFirst()) {
                String bucketName;
                String data;
                String imageId;
                int bucketNameColumn = cur.getColumnIndex(
                        MediaStore.Images.Media.BUCKET_DISPLAY_NAME);

                int imageUriColumn = cur.getColumnIndex(
                        MediaStore.Images.Media.DATA);

                int imageIdColumn = cur.getColumnIndex(
                        MediaStore.Images.Media._ID);

                do {
                    // Get the field values
                    bucketName = cur.getString(bucketNameColumn);
                    data = cur.getString(imageUriColumn);
                    imageId = cur.getString(imageIdColumn);

                    // Adding a new PhonePhoto object to phonePhotos vector
                    PhonePhoto phonePhoto = new PhonePhoto();
                    phonePhoto.setAlbumName(bucketName);
                    phonePhoto.setPhotoUri(data);
                    phonePhoto.setId(Integer.valueOf(imageId));

                    if (albumsNames.contains(bucketName)) {
                        for (PhotoAlbum album : phoneAlbums) {
                            if (album.getName().equals(bucketName)) {
                                album.getAlbumPhotos().add(phonePhoto);
                                Log.i("DeviceImageManager", "A photo was added to album => " + bucketName);
                                break;
                            }
                        }
                    } else {
                        PhotoAlbum album = new PhotoAlbum();
                        album.setId(phonePhoto.getId());
                        album.setName(bucketName);
                        album.setCoverUri(phonePhoto.getPhotoUri());
                        album.getAlbumPhotos().add(phonePhoto);

                        phoneAlbums.add(album);
                        albumsNames.add(bucketName);
                    }

                } while (cur.moveToNext());
            }

            cur.close();

        }
        return phoneAlbums;
    }


}

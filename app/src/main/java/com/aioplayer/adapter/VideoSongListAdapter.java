package com.aioplayer.adapter;

import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.aioplayer.MainActivity;
import com.aioplayer.R;
import com.aioplayer.dao.AudioSong;
import com.aioplayer.dao.VideoSong;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;



/**
 * Created by akankshadhanda on 13/07/17.
 */

public class VideoSongListAdapter extends BaseAdapter {

    private List<VideoSong> songList;
    private Context context;
    private MediaPlayer player;
    private ExecutorService service;
    private Handler handler;
    private int currentPosition=0;
    private ListView listView;

    public VideoSongListAdapter(List<VideoSong> songList, Context context,ListView listView) {
        this.songList = songList;
        this.context=context;
        this.player=new MediaPlayer();
        this.service= Executors.newFixedThreadPool(3);
        this.handler = new Handler();
        this.listView=listView;

    }

    @Override
    public int getCount() {
        return songList.size();
    }

    @Override
    public VideoSong getItem(int i) {
        return songList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        currentPosition=i;
        LayoutInflater inflater = (LayoutInflater)   context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Holder holder = null;
        if(view ==null) {
            view = inflater.inflate(R.layout.song_video, null);
            holder = new Holder(view);
            view.setTag(holder);
        }
        else
        {
            holder=(Holder) view.getTag();
        }
        final VideoSong song =getItem(i);
        final ImageView imageView=holder.getImageView();
        imageView.setImageDrawable(new IconicsDrawable(context, GoogleMaterial.Icon.gmd_music_note).color(Color.BLACK).sizeDp(IconicsDrawable.ANDROID_ACTIONBAR_ICON_SIZE_DP).paddingDp(IconicsDrawable.ANDROID_ACTIONBAR_ICON_SIZE_PADDING_DP));
        holder.getMusictitle().setText(song.getThisTitle());
        if(song.getThumbNail()==null) {
            service.execute(new Runnable() {
                @Override
                public void run() {
                    Bitmap thumb=null;
                    File file = new File(context.getFilesDir(),song.getThisTitle());
                    if(!file.exists()) {
                        thumb = ThumbnailUtils.createVideoThumbnail(song.getThisdata(), MediaStore.Video.Thumbnails.MICRO_KIND);
                        FileOutputStream out = null;
                        try {
                            out = new FileOutputStream(file);
                            thumb.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
                            // PNG is a lossless format, the compression factor (100) is ignored
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                if (out != null) {
                                    out.close();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    else
                    {
                        try {
                            thumb = BitmapFactory.decodeStream(new FileInputStream(file));
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                    song.setThumbNail(thumb);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            if(isIndexVisible(i)&&song.getThumbNail()!=null) {
                               imageView.setImageBitmap(song.getThumbNail());
                            }
                        }
                    });

                }
            });

        }
        else
        {
            imageView.setImageBitmap(song.getThumbNail());
        }





        return view;
    }

    public boolean isIndexVisible(int index)
    {
        if(index<=listView.getLastVisiblePosition()&& index<=listView.getFirstVisiblePosition())
        {
            return true;
        }
        return false;
    }

    public Bitmap getAlbumart(Long album_id)
    {
        Bitmap bm = null;
        try
        {
            final Uri sArtworkUri = Uri
                    .parse("content://media/external/video/albumart");

            Uri uri = ContentUris.withAppendedId(sArtworkUri, album_id);

            ParcelFileDescriptor pfd = context.getContentResolver()
                    .openFileDescriptor(uri, "r");

            if (pfd != null)
            {
                FileDescriptor fd = pfd.getFileDescriptor();
                bm = BitmapFactory.decodeFileDescriptor(fd);
            }
        } catch (Exception e) {
        }
        return bm;
    }

    private class Holder
    {
        private TextView musictitle;
                private ImageView imageView;

        public TextView getMusictitle() {
            return musictitle;
        }

        public void setMusictitle(TextView musictitle) {
            this.musictitle = musictitle;
        }

        public ImageView getImageView() {
            return imageView;
        }

        public void setImageView(ImageView imageView) {
            this.imageView = imageView;
        }

        public Holder(View view)
              {
                  this.musictitle=(TextView) view.findViewById(R.id.musictitle);
                  this.imageView=(ImageView) view.findViewById(R.id.circularimageview);
              }
    }

}

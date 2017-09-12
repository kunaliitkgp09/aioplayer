package com.aioplayer.adapter;

import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.aioplayer.MainActivity;
import com.aioplayer.R;
import com.aioplayer.application.SongApplication;
import com.aioplayer.dao.AudioSong;
import com.aioplayer.dao.PlayAudioMusic;
import com.aioplayer.dao.PlayListItem;
import com.aioplayer.event.AudioMusicEvent;
import com.aioplayer.manager.DbManager;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.squareup.otto.Bus;

import java.io.FileDescriptor;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



/**
 * Created by akankshadhanda on 12/07/17.
 */

public class AudioSongListAdapter extends BaseAdapter {

    private List<AudioSong> songList;
    private Context context;
    private MediaPlayer player;
    private ExecutorService service;
    private ListView listView;
    private DbManager dbManager;
    private Bus eventBus;

    public AudioSongListAdapter(List<AudioSong> songList, Context context,ListView listView) {
        this.songList = songList;
        this.context=context;
        this.player=new MediaPlayer();
        this.service=Executors.newFixedThreadPool(3);
        this.listView=listView;
        this.dbManager= new DbManager(context);
        this.eventBus=((SongApplication)((MainActivity)context).getApplication()).getEventBus();

    }

    @Override
    public int getCount() {
        return songList.size();
    }

    @Override
    public AudioSong getItem(int i) {
        return songList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater)   context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final AudioSong song =getItem(i);

        Bitmap albumArt=song.getAlbumArt();
        Holder holder= null;
        if(view==null)
        {
            view = inflater.inflate(R.layout.song_item, null);
            holder=new Holder(view);
            view.setTag(holder);
        }
        else
        {
         holder=(Holder) view.getTag();
        }
//        holder.getCircularImageView().setImageDrawable(new IconicsDrawable(context, GoogleMaterial.Icon.gmd_music_note).color(Color.BLACK).sizeDp(24).paddingDp(2));
//       final ImageView imageView=  holder.getCircularImageView();
//        imageView.post(new Runnable() {
//            @Override
//            public void run() {
//                imageView.setImageResource(android.R.color.transparent);
//            }
//        });
        if(albumArt==null)
        {

            final Handler handler = new Handler();
            final Holder dummyPoint=holder;
             service.execute(new Runnable() {
                 @Override
                 public void run() {
                     final Bitmap albumArt=getAlbumart(song.getThisalbumId());
                     song.setAlbumArt(albumArt);
                     handler.post(new Runnable() {
                         @Override
                         public void run() {
                             if(isIndexVisible(i))
                             {
                                 if(albumArt!=null) {
                                     dummyPoint.getCircularImageView().setImageBitmap(albumArt);
                                 }
                             }

                         }
                     });

                 }
             });


        }
       holder.getMusicTitle().setText(song.getThisTitle());
       holder.getAlbumName().setText(song.getAlbumName());
        final View playlistView=holder.getPlaylistmenu();
        playlistView.setBackground(new IconicsDrawable(context, GoogleMaterial.Icon.gmd_add_circle).color(Color.BLACK).sizeDp(IconicsDrawable.ANDROID_ACTIONBAR_ICON_SIZE_DP).paddingDp(IconicsDrawable.ANDROID_ACTIONBAR_ICON_SIZE_PADDING_DP));
        playlistView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopup(playlistView,song);
            }
        });


        holder.getCircularImageView().setVisibility(View.INVISIBLE);
        if(albumArt!=null) {
            holder.getCircularImageView().setVisibility(View.VISIBLE);
            holder.getCircularImageView().setImageBitmap(albumArt);
        }


        return view;
    }
    public void showPopup(View v,final AudioSong song) {
        PopupMenu popup = new PopupMenu(context, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.main, popup.getMenu());
        popup.show();
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId()==R.id.createplaylist)
                {
                   createPlayList(false,song);
                }
                if(item.getItemId()==R.id.addtoplaylist)
                {
                    createAddtoPlaylist(song);
                }
                if(item.getItemId()==R.id.addtoqueque)
                {
                    List<PlayAudioMusic> playAudioMusics = new ArrayList<PlayAudioMusic>();
                    PlayAudioMusic playAudioMusic = new PlayAudioMusic(song);
                    playAudioMusics.add(playAudioMusic);
                    eventBus.post(new AudioMusicEvent(AudioMusicEvent.ADD,playAudioMusics));
                }

                return false;
            }
        });
    }

    public void createAddtoPlaylist(final AudioSong song)
    {
        final Dialog dialog = new Dialog(context);
        dialog.setCanceledOnTouchOutside(true);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_playlist_dropdown_name);
        final ListView playList = (ListView) dialog.findViewById(R.id.playlistname);
        final ArrayList<String> playListNames = new ArrayList<String>();
        List<PlayListItem> playListItems= dbManager.getAllPlayListItems();
        for(PlayListItem playListItem:playListItems)
        {
            playListNames.add(playListItem.getPlayListName());
        }
        ArrayAdapter<String> dropDownAdapter = new ArrayAdapter<String>(context,
                R.layout.simple_list_item, android.R.id.text1, playListNames);
        playList.setAdapter(dropDownAdapter);
        Button doneButton =(Button)dialog.findViewById(R.id.mainlayout);
        playList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                PlayListItem playListItem = dbManager.findPlayListItemwithId(playListNames.get(i));
                playListItem.getAudioSongs().add(song);
                dbManager.updatePlayList(playListItem);
                dialog.dismiss();
            }
        });
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createPlayList(true,song);

            }
        });
        dialog.show();
    }

    public void createPlayList(final boolean fromPlayList, final AudioSong audioSong)
    {
        final Dialog dialog = new Dialog(context);
        dialog.setCanceledOnTouchOutside(true);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_enter_playlist_name);
        final EditText text = (EditText) dialog.findViewById(R.id.playistname);
        Button doneButton =(Button)dialog.findViewById(R.id.savebutton);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!text.getText().toString().isEmpty()) {
                    PlayListItem playListItem = new PlayListItem();
                    playListItem.setPlayListName(text.getText().toString());

                    dbManager.savePlayList(playListItem);
                    dialog.dismiss();
                    if(fromPlayList)
                    {
                        createAddtoPlaylist(audioSong);
                    }
                }
                else
                {
                    text.setError("Playlist name cannot be empty");
                }

            }
        });
        dialog.show();
    }


    class Holder {
        private TextView musicTitle;
        private TextView albumName;
        private ImageButton playlistmenu;

        public ImageView getCircularImageView() {
            return circularImageView;
        }

        public void setCircularImageView(ImageView circularImageView) {
            this.circularImageView = circularImageView;
        }

        public ImageButton getPlaylistmenu() {
            return playlistmenu;
        }

        public void setPlaylistmenu(ImageButton playlistmenu) {
            this.playlistmenu = playlistmenu;
        }

        private ImageView circularImageView;
        public Holder(View view)
        {
            this.musicTitle=((TextView)view.findViewById(R.id.musictitle));
            this.albumName=((TextView)view.findViewById(R.id.albumname));
            this.circularImageView= ((ImageView) view.findViewById(R.id.circularimageview));
            this.playlistmenu=(ImageButton)view.findViewById(R.id.playlistmenu);
        }

        public TextView getMusicTitle() {
            return musicTitle;
        }

        public void setMusicTitle(TextView musicTitle) {
            this.musicTitle = musicTitle;
        }

        public TextView getAlbumName() {
            return albumName;
        }

        public void setAlbumName(TextView albumName) {
            this.albumName = albumName;
        }
    }

    public Bitmap getAlbumart(Long album_id)
    {
        Bitmap bm = null;
        try
        {
            final Uri sArtworkUri = Uri
                    .parse("content://media/external/audio/albumart");

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

    public boolean isIndexVisible(int index)
    {
        if(index<=listView.getLastVisiblePosition()&& index<=listView.getFirstVisiblePosition())
        {
            return true;
        }
        return false;
    }
}

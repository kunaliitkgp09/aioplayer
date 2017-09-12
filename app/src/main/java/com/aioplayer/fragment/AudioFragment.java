package com.aioplayer.fragment;


import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;

import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.VideoView;

import com.aioplayer.MainActivity;
import com.aioplayer.R;
import com.aioplayer.adapter.AudioSongListAdapter;
import com.aioplayer.application.SongApplication;
import com.aioplayer.dao.AudioSong;
import com.aioplayer.dao.PlayAudioMusic;
import com.aioplayer.dao.RssFeedItem;
import com.aioplayer.event.AudioMusicEvent;
import com.aioplayer.event.MusicProgressViewUpdate;
import com.aioplayer.server.FileServer;
import com.aioplayer.utils.AppState;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


import pl.droidsonroids.casty.MediaData;

import static android.content.Context.WIFI_SERVICE;

/**
 * Created by akankshadhanda on 13/07/17.
 */

public class AudioFragment extends BaseFragment{

    private ListView songView;
    private MainActivity mainActivity;
    //service

    private Intent playIntent;
    //binding
    private boolean musicBound=false;
    private int currentWindow;

    //	private SimpleExoPlayerView exoplayerview;
    private SimpleExoPlayer simpleExoPlayer;
    private long playbackPosition;
    private boolean playWhenReady=true;
    private VideoView videoView;
    private int position=0;
    private MediaPlayer mediaPlayer;
    //	private ProgressBar progressBar;
    private boolean musicPlayerPrepared;
    private ListView songList;
    private ToggleButton toggleButton;
    private MediaController mediaControls;
    private Bus songBus;
    private int currentPosition;
    private AudioSong currentSong;
    private AudioSongListAdapter songListAdapter;
    private SeekBar seekBar;
    private View playerController;
    private MaterialSearchBar searchView;
    FileServer fileServer;
    private boolean  closrSuggestion;
    public static String TAG="audiofragment";
    private boolean playerLayout=false;




//    private View.OnClickListener onClickListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//
//           switch (view.getId())
//           {
//               case R.id.toggleplaypause:
//                   songBus.post(new AudioMusicEvent(AudioMusicEvent.PAUSE,null));
//                   break;
//               case R.id.onreplay:
//                   songBus.post(new AudioMusicEvent(AudioMusicEvent.REPLAY,null));
//                   break;
//
//           }
//        }
//    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mainActivity=(MainActivity) getActivity();
    }
    public String getTAG() {
        return TAG;
    }

//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.putBoolean("isPlaying",playerController.getVisibility()==View.VISIBLE);
//    }
//
//    @Override
//    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
//        super.onViewStateRestored(savedInstanceState);

//    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_audio_song,container,false);

        songList=(ListView)view.findViewById(R.id.contentList);
        songBus=((SongApplication)mainActivity.getApplication()).getEventBus();
        searchView=(MaterialSearchBar)view.findViewById(R.id.floating_search_view);
        searchView.setTextColor(android.R.color.black);
        songBus.register(this);
        toggleButton=(ToggleButton)mainActivity.getToggleButton();
        seekBar=mainActivity.getSeekBar();
//       toggleButton.setOnClickListener(onClickListener);
//         mainActivity.getRewindIcon().setOnClickListener(onClickListener);
//        mainActivity.getFastforwardicon().setOnClickListener(onClickListener);
//        mainActivity.getOnNext().setOnClickListener(onClickListener);
//       mainActivity.getOnReplay().setOnClickListener(onClickListener);
//       mainActivity.getPreviousICon().setOnClickListener(onClickListener);

        songList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                currentSong=songListAdapter.getItem(i);
               mainActivity.getToggleButton().setBackground(new IconicsDrawable(mainActivity, GoogleMaterial.Icon.gmd_pause).color(Color.BLACK).sizeDp(IconicsDrawable.ANDROID_ACTIONBAR_ICON_SIZE_DP).paddingDp(IconicsDrawable.ANDROID_ACTIONBAR_ICON_SIZE_PADDING_DP));
                if(!mainActivity.getCasty().isConnected()) {
                    List<PlayAudioMusic> urls = new ArrayList<>();
                    urls.add(new PlayAudioMusic(currentSong));
                    songBus.post(new AudioMusicEvent(AudioMusicEvent.PLAY,urls));
                    AudioFragment.this.currentPosition = i;
                    playerController.setVisibility(View.VISIBLE);
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    layoutParams.setMargins(0, 0, 0, (int) dpFromPx(mainActivity, 70.0f));
                    seekBar.setVisibility(View.VISIBLE);
                }
                else
                {
                    if(currentSong!=null)
                    songBus.post(currentSong);
                }



            }
        });

       playerController= mainActivity.getPlayercontroller();
        prepareMusicList();
        return view;
    }


//    @Override
//    public void onResume() {
//        super.onResume();
//        AppState appState=AppState.getInstance(mainActivity);
//        if(appState.getBoolean("isPlaying")) {
//            playerController.setVisibility(View.VISIBLE);
//            appState.saveBoolean("isPlaying",false);
//            seekBar.setVisibility(View.VISIBLE);
//        }
//        try {
//            songBus.register(this);
//        }
//        catch (Exception exception)
//        {
//            exception.printStackTrace();;
//        }
//
//
//
//    }


    public static float dpFromPx(final Context context, final float px) {
        return px / context.getResources().getDisplayMetrics().density;
    }
    @Subscribe
    public void updateProgressView(final MusicProgressViewUpdate musicProgressViewUpdate)
    {
        seekBar.setProgress((int)musicProgressViewUpdate.getProgressValue());

    }
    private void prepareMusicList()
    {
        songListAdapter = new AudioSongListAdapter(getAudioSongList(),mainActivity,songList);
        songList.setAdapter(songListAdapter);
        songListAdapter.notifyDataSetChanged();
    }


    @Override
    public void onStop() {
        super.onStop();
        try {
            songBus.register(this);
        }
        catch (Exception exception)
        {
            exception.printStackTrace();;
        }
        songBus.unregister(this);
    }

    public List<AudioSong> getAudioSongList() {
        List<AudioSong> listSongs = new ArrayList<>();
        //retrieve song info
        listSongs.addAll(getAudioSongList(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI));
//        listSongs.addAll(getAudioSongList(MediaStore.Audio.Media.INTERNAL_CONTENT_URI));
        return listSongs;
    }

    private List<AudioSong> getAudioSongList(Uri uri) {
        ContentResolver musicResolver = mainActivity.getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
        final List<AudioSong> listSongs = new ArrayList<>();
        final LinkedList<String>suggestions = new LinkedList<>();
        //iterate over results if valid
        if(musicCursor!=null && musicCursor.moveToFirst()){
            //get columns
            int titleColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);
            int albumId = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.ALBUM_ID);
            int data= musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            int albumkey=musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_KEY);
            int duration =musicCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
            int albumNameIndex=musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);

            //add songs to list
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                long thisalbumId = musicCursor.getLong(albumId);
                String thisdata= musicCursor.getString(data);
                String AlbumKey = musicCursor.getString(albumkey);
                long songduration=musicCursor.getLong(duration);
                String albumName=musicCursor.getString(albumNameIndex);
                listSongs.add(new AudioSong(thisId,thisTitle,thisalbumId,thisdata,AlbumKey,getImagePath(albumId),String.valueOf(songduration),albumName));

                suggestions.add(thisTitle);

            }
            while (musicCursor.moveToNext());


        }
        musicCursor.close();
        initSearchView(suggestions,listSongs);
        return listSongs;
    }

    public void initSearchView(final List<String> suggestions,final List<AudioSong> listSongs)
    {
        searchView.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!closrSuggestion) {
                    List<String> filteredSuggestion = new ArrayList<String>();
                    for (String suggestion : suggestions) {
                        if (suggestion.toLowerCase().contains(charSequence.toString().toLowerCase())) {
                            filteredSuggestion.add(suggestion);
                        }

                    }
                    searchView.setLastSuggestions(filteredSuggestion);
                    searchView.enableSearch();
                }
                closrSuggestion=false;

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        searchView.setSuggstionsClickListener(new SuggestionsAdapter.OnItemViewClickListener() {
            @Override
            public void OnItemClickListener(int position, View v) {
                searchView.disableSearch();
                searchView.hideSuggestionsList();
                closrSuggestion=true;
                for(AudioSong audioSong:listSongs) {
                    if(audioSong.getThisTitle().equalsIgnoreCase(searchView.getLastSuggestions().get(position).toString())) {
                        LinkedList<PlayAudioMusic> playAudioMusics= new LinkedList<PlayAudioMusic>();
                        PlayAudioMusic playAudioMusic = new PlayAudioMusic(audioSong);
                        playAudioMusics.add(playAudioMusic);
                        songBus.post(new AudioMusicEvent(AudioMusicEvent.PLAY,playAudioMusics));
                    }
                }

            }

            @Override
            public void OnItemDeleteListener(int position, View v) {

            }
        });
    }

    private String getImagePath(long albumId)
    {
        Uri sArtworkUri = Uri
                .parse("content://media/external/audio/albumart");
        return ContentUris.withAppendedId(sArtworkUri, albumId).toString();

    }







    @Override
    public void onPause() {
        super.onPause();
        try {
            songBus.unregister(this);
        }
        catch (Exception exception)
        {
            exception.printStackTrace();;
        }

    }




}

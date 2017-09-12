package com.aioplayer.fragment;


import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.aioplayer.MainActivity;
import com.aioplayer.R;
import com.aioplayer.adapter.VideoSongListAdapter;
import com.aioplayer.application.SongApplication;
import com.aioplayer.dao.AudioSong;
import com.aioplayer.dao.PlayAudioMusic;
import com.aioplayer.dao.VideoSong;
import com.aioplayer.event.AudioMusicEvent;
import com.aioplayer.event.ChromeCastEvent;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


import static android.view.View.GONE;

/**
 * Created by akankshadhanda on 13/07/17.
 */

public class VideoFragment extends BaseFragment{
    private MainActivity mainActivity;
    private ListView videoSongs;
    public static String TAG="videofragment";
    private boolean isChromeCastConnected;
    private Bus eventBus;
    private MaterialSearchBar searchView;
    private boolean closrSuggestion=false;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mainActivity=(MainActivity)context;
        this.eventBus=((SongApplication)mainActivity.getApplication()).getEventBus();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_video_song,container,false);
        videoSongs=(ListView)view.findViewById(R.id.videosongs);
        searchView=(MaterialSearchBar)view.findViewById(R.id.floating_search_view);
        searchView.setTextColor(android.R.color.black);
        final List<VideoSong> videoSongsList=getVideoSongList();
        VideoSongListAdapter videoSongListAdapter = new VideoSongListAdapter(videoSongsList,mainActivity,videoSongs);
        videoSongs.setAdapter(videoSongListAdapter);

        videoSongs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                VideoSong videoSong = videoSongsList.get(i);
                if(!mainActivity.getCasty().isConnected()) {
                    Bundle bundle = new Bundle();
                    bundle.putString("uri", videoSong.getThisdata());
                    mainActivity.showNextView(new PlayVideoFragment(), bundle);
                }
                else {
                     ((SongApplication) mainActivity.getApplication()).getEventBus().post(videoSong);
                }
            }
        });
      eventBus.register(this);

        return view;
    }

    public  String getTAG() {
        return TAG;
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            eventBus.unregister(this);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Subscribe
    public void chromeCastEvent(ChromeCastEvent chromeCastEvent)
    {
        isChromeCastConnected =chromeCastEvent.getState()==ChromeCastEvent.CONNECTED;
    }
    public List<VideoSong> getVideoSongList()
    {
        List<VideoSong> listSongs = new ArrayList<>();
        listSongs.addAll(getVideoSongList(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI));
//        listSongs.addAll(getVideoSongList(MediaStore.Audio.Media.INTERNAL_CONTENT_URI));
        return listSongs;
    }

    public List<VideoSong> getVideoSongList(Uri uri) {

        //retrieve song info
        ContentResolver musicResolver = mainActivity.getContentResolver();
        Uri musicUri = android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
        List<VideoSong> listSongs = new ArrayList<>();
        final LinkedList<String> suggestions = new LinkedList<>();
        //iterate over results if valid
        if(musicCursor!=null && musicCursor.moveToFirst()){
            //get columns
            int titleColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Video.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Video.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Video.Media.ARTIST);
            int data= musicCursor.getColumnIndex(MediaStore.Video.Media.DATA);
            int duration =musicCursor.getColumnIndex(MediaStore.Video.Media.DURATION);

            //add songs to list
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                String thisdata= musicCursor.getString(data);
                long songduration=musicCursor.getLong(duration);
                suggestions.add(thisTitle);
                listSongs.add(new VideoSong(thisId, thisTitle,thisArtist,thisdata,duration));
            }
            while (musicCursor.moveToNext());

        }
        musicCursor.close();
        initSearchView(suggestions,listSongs);
        return listSongs;
    }

    public void initSearchView(final List<String> suggestions,final List<VideoSong> listSongs)
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
                        if (suggestion.contains(charSequence.toString())) {
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
                for(VideoSong videoSong:listSongs) {
                    if(videoSong.getThisTitle().equalsIgnoreCase(searchView.getLastSuggestions().get(position).toString())) {
                        PlayVideoFragment playVideoFragment = new PlayVideoFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("uri",videoSong.getThisdata());
                        bundle.putString("filename",videoSong.getThisTitle());
                        mainActivity.showNextView(playVideoFragment,bundle);
                    }
                }

            }

            @Override
            public void OnItemDeleteListener(int position, View v) {

            }
        });
    }


}

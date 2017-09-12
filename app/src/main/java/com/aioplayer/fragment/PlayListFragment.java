package com.aioplayer.fragment;

import android.content.ContentUris;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ListView;

import com.aioplayer.R;
import com.aioplayer.adapter.PlayListAdapter;
import com.aioplayer.application.SongApplication;
import com.aioplayer.dao.AudioSong;
import com.aioplayer.dao.PlayAudioMusic;
import com.aioplayer.dao.PlayListHeader;
import com.aioplayer.dao.PlayListItem;
import com.aioplayer.event.AudioMusicEvent;
import com.aioplayer.manager.DbManager;
import com.aioplayer.utils.AppState;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.io.FileDescriptor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;



/**
 * Created by akankshadhanda on 21/07/17.
 */

public class PlayListFragment extends BaseFragment {
    private DbManager dbManager;
    private ExpandableListView expandableListView;
    private Bus eventBus;
    private int groupPosition;
    private int childPosition;

    @Override
    public String getTAG() {
        return "playlistfragment";
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view=inflater.inflate(R.layout.playlist_fragment,container,false);
        dbManager= new DbManager(getMainActivity());
        eventBus=((SongApplication)getMainActivity().getApplication()).getEventBus();

        expandableListView=(ExpandableListView)view;
        expandableListView.setAdapter(getPlayListAdapter());
        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                groupPosition=i;
                return false;
            }
        });
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                childPosition=i;
                return false;
            }
        });
        return view;
    }
    @Override
    public void onResume() {
        super.onResume();

        try {
            eventBus.register(this);
        }
        catch (Exception exception)
        {
            exception.printStackTrace();;
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        try {
            eventBus.unregister(this);
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }

    }



    public PlayListAdapter getPlayListAdapter()
    {
        List<PlayListItem> playListItemList= dbManager.getAllPlayListItems();
        HashMap<String,Collection<AudioSong>> audioSongMapping = new HashMap<>();
        LinkedList<PlayListHeader> headers  = new LinkedList<>();
        for(PlayListItem playListItem:playListItemList)
        {
            PlayListHeader playListHeader = new PlayListHeader();

            Iterator<AudioSong> audioSongIterator=playListItem.getAudioSongs().iterator();
            Bitmap audioSongImage=null;
            while(audioSongImage==null&&audioSongIterator.hasNext())
            {
                audioSongImage=getAlbumart(audioSongIterator.next().getThisalbumId());
                playListHeader.setImgaeUrl(audioSongImage);

            }
            playListHeader.setTitle(playListItem.getPlayListName());
            headers.add(playListHeader);
            audioSongMapping.put(playListItem.getPlayListName(),playListItem.getAudioSongs());
        }
        for(PlayListItem listItem:playListItemList)
        {
           return new PlayListAdapter(getMainActivity(),headers,audioSongMapping);
        }
        return null;
    }

    public Bitmap getAlbumart(Long album_id)
    {
        Bitmap bm = null;
        try
        {
            final Uri sArtworkUri = Uri
                    .parse("content://media/external/audio/albumart");

            Uri uri = ContentUris.withAppendedId(sArtworkUri, album_id);

            ParcelFileDescriptor pfd = getMainActivity().getContentResolver()
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

    @Subscribe
    public void audioMusicEvent(AudioMusicEvent audioMusicEvent) {
        if (audioMusicEvent.getCommand() == AudioMusicEvent.NEXT) {
            onNext(null);
        } else if (audioMusicEvent.getCommand() == AudioMusicEvent.PREVIOUS) {
            onPrevious(null);
        }

     }
    public void onNext(View v)
    {
        PlayListAdapter playListAdapter=(PlayListAdapter) expandableListView.getExpandableListAdapter();
        int size=expandableListView.getAdapter().getCount();
        this.childPosition=(++childPosition)%size;
        AudioSong audioSong =playListAdapter.getChild(groupPosition,childPosition);
        PlayAudioMusic playListItem = new PlayAudioMusic(audioSong);
        List<PlayAudioMusic> urls = new ArrayList<PlayAudioMusic>();
        urls.add(playListItem);
        eventBus.post(new AudioMusicEvent(AudioMusicEvent.PLAY,urls));
    }
    public void onPrevious(View v)
    {
        PlayListAdapter playListAdapter=(PlayListAdapter) expandableListView.getExpandableListAdapter();
        int size=expandableListView.getAdapter().getCount();
        this.childPosition=(--childPosition);
        if(childPosition<0)
        {
            this.childPosition=size+childPosition;
        }
        AudioSong audioSong =playListAdapter.getChild(groupPosition,childPosition);
        PlayAudioMusic playListItem = new PlayAudioMusic(audioSong);
        List<PlayAudioMusic> urls = new ArrayList<PlayAudioMusic>();
        urls.add(playListItem);
        eventBus.post(new AudioMusicEvent(AudioMusicEvent.PLAY,urls));
    }
}

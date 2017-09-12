package com.aioplayer.fragment;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcel;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.util.TimeUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.aioplayer.R;
import com.aioplayer.adapter.PodCastItemsAdapter;
import com.aioplayer.application.SongApplication;
import com.aioplayer.dao.PlayAudioMusic;
import com.aioplayer.dao.PodCastItem;
import com.aioplayer.dao.PodCastItemDownload;
import com.aioplayer.event.AudioMusicEvent;
import com.aioplayer.event.ShowAudioController;

import com.aioplayer.manager.DbManager;
import com.coolerfall.download.DownloadCallback;
import com.coolerfall.download.DownloadManager;
import com.coolerfall.download.DownloadRequest;
import com.coolerfall.download.OkHttpDownloader;
import com.coolerfall.download.Priority;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import org.w3c.dom.NodeList;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;



/**
 * Created by akankshadhanda on 30/07/17.
 */

public class PodCastItemFragment extends BaseFragment {
    private List<PodCastItem> nodeList;
    private Bus songBus;
    private PodCastItemsAdapter podCastItemsAdapter;
    private int currentPosition;
    private PodCastItem podCastItem;
    private ToggleButton toggleButton;
    private SeekBar seekBar;
    private LinearLayout playercontroller;
    private TextView maxTime;
    private TextView startTime;
    private ProgressBar progressBar;
    private DbManager dbManager;
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId())
            {
                case R.id.toggleplaypause:

                    songBus.post(new AudioMusicEvent(AudioMusicEvent.PAUSE,null));
                    break;
                case R.id.onreplay:
                    songBus.post(new AudioMusicEvent(AudioMusicEvent.REPLAY,null));
                    break;
                case R.id.onnext:
                    onNext(null);
                    break;
                case R.id.previousicon:
                    onPrevious(null);
                    break;
                case R.id.fastforwardicon:
                    fastForward(null);
                    break;
                case R.id.rewindicon:
                    reWind(null);
                    break;
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        songBus=((SongApplication)getMainActivity().getApplication()).getEventBus();
        songBus.register(this);
        dbManager = new DbManager(getMainActivity());
    }

    public PodCastItemFragment(List<PodCastItem> nodeList)
    {
        this.nodeList=nodeList;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view=inflater.inflate(R.layout.podcast_item_fragment,container,false);
        final ListView podCastItem=(ListView)view.findViewById(R.id.listview);
        podCastItemsAdapter = new PodCastItemsAdapter(nodeList,getMainActivity());
        podCastItem.setAdapter(podCastItemsAdapter);
        playercontroller =getMainActivity().getPlayercontroller();
        progressBar=getMainActivity().getProgressBar();
        toggleButton=getMainActivity().getToggleButton();
        toggleButton.setOnClickListener(onClickListener);
        maxTime=getMainActivity().getMaxTime();
        startTime=getMainActivity().getStartTime();
        podCastItem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
             @Override
             public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                 if(!getMainActivity().getCasty().isConnected()) {
                     PodCastItem podCastItemValue = nodeList.get(i);
                     progressBar.setVisibility(View.VISIBLE);
                     RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                     layoutParams.setMargins(0,0,0,0);
                     getMainActivity().getBodyContent().setLayoutParams(layoutParams);
                     playercontroller.setVisibility(View.GONE);
                     seekBar.setVisibility(View.GONE);
                     maxTime.setVisibility(View.GONE);
                     startTime.setVisibility(View.GONE);
                     maxTime.setVisibility(View.GONE);
                     PodCastItemDownload podCastItemDownload=dbManager.findPodCastItemwithId(podCastItemValue.getFeedUrl());
                     if(podCastItemDownload!=null&&podCastItemDownload.isDownloadCompeleted()) {
                         try {
                             File file =fileExits(podCastItemValue.getFeedUrl());
                             List<PlayAudioMusic> urls = new ArrayList<PlayAudioMusic>();
                             PlayAudioMusic playAudioMusic = new PlayAudioMusic(podCastItemDownload);
                             playAudioMusic.setValue(file.getCanonicalPath());
                             urls.add(playAudioMusic);
                             songBus.post(new AudioMusicEvent(AudioMusicEvent.PLAY,urls));
                         } catch (IOException e) {
                             e.printStackTrace();
                         }
                     }
                     else
                     {
                         List<PlayAudioMusic> urls = new ArrayList<PlayAudioMusic>();
                         PlayAudioMusic playAudioMusic = new PlayAudioMusic(podCastItemDownload);
                         playAudioMusic.setValue(podCastItemValue.getFeedUrl());
                         urls.add(playAudioMusic);
                         songBus.post(new AudioMusicEvent(AudioMusicEvent.PLAY,urls));
                     }
                 }
                 else
                 {
                     songBus.post(nodeList.get(i));
                 }

             }
         });

//        getMainActivity().getRewindIcon().setOnClickListener(onClickListener);
//        getMainActivity().getFastforwardicon().setOnClickListener(onClickListener);
//        getMainActivity().getOnNext().setOnClickListener(onClickListener);
//        getMainActivity().getOnReplay().setOnClickListener(onClickListener);
//        getMainActivity().getPreviousICon().setOnClickListener(onClickListener);
        seekBar=getMainActivity().getSeekBar();
        seekBar.getProgressDrawable().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(podCastItem!=null) {
                    List<PlayAudioMusic> urls = new ArrayList<>();
                    PlayAudioMusic playAudioMusic = new PlayAudioMusic();
                    playAudioMusic.setValue(String.valueOf(seekBar.getProgress() ));
                    urls.add(playAudioMusic);
                    songBus.post(new AudioMusicEvent(AudioMusicEvent.MOVE,urls));
                }
            }
        });


        return view;
    }



    public void onNext(View v)
    {
        int size=podCastItemsAdapter.getCount();
        this.currentPosition=(++currentPosition)%size;
        podCastItem =podCastItemsAdapter.getItem(currentPosition);
        if(toggleButton.isChecked())
        {
            toggleButton.toggle();
        }
        PlayAudioMusic playAudioMusic = new PlayAudioMusic(podCastItem);
        playAudioMusic.setValue(podCastItem.getFeedUrl());
        List<PlayAudioMusic> urls = new ArrayList<>();
        urls.add(playAudioMusic);
        songBus.post(new AudioMusicEvent(AudioMusicEvent.PLAY,urls));
    }
    public void onPrevious(View v)
    {
        int size=podCastItemsAdapter.getCount();
        this.currentPosition=(--currentPosition);
        if(currentPosition<0)
        {
            this.currentPosition=size+currentPosition;
        }
        if(toggleButton.isChecked())
        {
            toggleButton.toggle();
        }
        podCastItem =podCastItemsAdapter.getItem(currentPosition);
        PlayAudioMusic playAudioMusic = new PlayAudioMusic(podCastItem);
        playAudioMusic.setValue(podCastItem.getFeedUrl());
        List<PlayAudioMusic> urls = new ArrayList<>();
        urls.add(playAudioMusic);
        songBus.post(new AudioMusicEvent(AudioMusicEvent.PLAY,urls));
    }


    public void reWind(View v)
    {
        songBus.post(new AudioMusicEvent(AudioMusicEvent.REWIND,null));
    }



    public void fastForward(View v)
    {
        songBus.post(new AudioMusicEvent(AudioMusicEvent.FASTFORWARD,null));
    }

    public File fileExits(String url)
    {
        String[] fileParts=url.replaceAll("//","").split("/");
        File file = new File(getMainActivity().getFilesDir().getAbsolutePath(),fileParts[fileParts.length-1]);
        return file;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        songBus.unregister(this);
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

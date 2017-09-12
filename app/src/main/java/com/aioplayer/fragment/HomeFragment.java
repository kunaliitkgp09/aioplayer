package com.aioplayer.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.MediaRouteButton;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.aioplayer.MainActivity;
import com.aioplayer.R;
import com.aioplayer.application.SongApplication;
import com.aioplayer.dao.VideoSong;
import com.aioplayer.event.ChromeCastEvent;
import com.aioplayer.pager.Pager;
import com.aioplayer.server.FileServer;
import com.aioplayer.service.AudioMusicService;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;




import static android.content.Context.WIFI_SERVICE;

/**
 * Created by akankshadhanda on 14/07/17.
 */

public class HomeFragment extends BaseFragment {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private MainActivity mainActivity;
    private Pager adapter;
    private FileServer contentfileServer;
    private FileServer thumbnailfileServer;
    private Bus eventBus;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;


    public static String TAG="homefragment";
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mainActivity=(MainActivity)context;
        this.eventBus=((SongApplication)mainActivity.getApplication()).getEventBus();
    }
    public  String getTAG() {
        return TAG;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_main,container,false);
        viewPager=(ViewPager)view.findViewById(R.id.pager);
        tabLayout=(TabLayout)view.findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Audio"));
        tabLayout.addTab(tabLayout.newTab().setText("Video"));
        tabLayout.addTab(tabLayout.newTab().setText("Playlist"));

//        mainActivity.getCasty().setUpMediaRouteButton((MediaRouteButton) view.findViewById(R.id.media_route_button));
////        mainActivity.getCasty().setOnConnectChangeListener(new Casty.OnConnectChangeListener() {
//            @Override
//            public void onConnected() {
//            eventBus.post( new ChromeCastEvent(ChromeCastEvent.CONNECTED));
//            }
//
//            @Override
//            public void onDisconnected() {
//                    eventBus.post( new ChromeCastEvent(ChromeCastEvent.DISCONNECTED));
//            }
//        });
        adapter = new Pager(getChildFragmentManager(), tabLayout.getTabCount());
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        //Adding adapter to pager
        viewPager.setAdapter(adapter);

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                tabLayout.setScrollPosition(position,0,false);
            }

            @Override
            public void onPageSelected(int position) {
                tabLayout.setScrollPosition(position,0,false);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });



        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        eventBus.unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
        eventBus.register(this);
    }









}

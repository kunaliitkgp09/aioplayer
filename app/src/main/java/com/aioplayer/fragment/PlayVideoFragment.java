package com.aioplayer.fragment;


import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;


import com.aioplayer.MainActivity;
import com.aioplayer.R;
import com.aioplayer.application.SongApplication;
import com.aioplayer.event.AudioMusicEvent;
import com.aioplayer.opensubtitle.OpenSubtitle;
import com.aioplayer.opensubtitle.SubtitleInfo;
import com.aioplayer.subtitle.Caption;
import com.aioplayer.subtitle.FormatSRT;
import com.aioplayer.subtitle.TimedTextObject;
import com.devbrackets.android.exomedia.listener.OnCompletionListener;
import com.devbrackets.android.exomedia.listener.OnPreparedListener;
import com.devbrackets.android.exomedia.listener.OnSeekCompletionListener;
import com.devbrackets.android.exomedia.listener.OnVideoSizeChangedListener;
import com.devbrackets.android.exomedia.ui.widget.VideoControls;
import com.devbrackets.android.exomedia.ui.widget.VideoView;
import com.google.android.exoplayer2.drm.ExoMediaDrm;
import com.google.android.exoplayer2.drm.MediaDrmCallback;
import com.google.android.exoplayer2.text.Cue;
import com.google.android.exoplayer2.ui.SubtitleView;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.squareup.otto.Bus;

import org.apache.xmlrpc.XmlRpcException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.zip.GZIPInputStream;


/**
 * Created by akankshadhanda on 14/07/17.
 */

public class PlayVideoFragment extends BaseFragment implements MediaPlayer.OnPreparedListener{
    private VideoView videoView;
    private MainActivity mainActivity;

    public static String TAG="playvideofragment";
    private int videoOffset=0;
    private MediaPlayer mediaPlayer;
    private long currentPosition;
    private boolean pastVisibility;
    private  Bus eventBus;
    private TextView subtitleView;
    private TimedTextObject srt;

    private Handler subtitleDisplayHandler = new Handler();
    private Runnable subtitle = new Runnable() {
        public void run() {
            if (videoView.isPlaying()) {
                int currentPos = mediaPlayer.getCurrentPosition();
                Collection<Caption> subtitles =  srt.captions.values();
                for(Caption caption : subtitles) {
                    if (currentPos >= caption.start.getMseconds() && currentPos <= caption.end.getMseconds()) {
                        onTimedText(caption);
                        break;
                    } else if (currentPos > caption.end.getMseconds()) {
                        onTimedText(null);
                    }
                }
            }
            subtitleDisplayHandler.postDelayed(this, 100);
        };
    };

    public void onTimedText(Caption text) {

        if (text == null) {
            subtitleView.setVisibility(View.INVISIBLE);
            return;
        }
        subtitleView.setText(Html.fromHtml(text.content));
        subtitleView.setVisibility(View.VISIBLE);
    }




    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mainActivity=(MainActivity)context;
    }
    public String getTAG() {
        return TAG;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);outState.putLong("time",videoView.getCurrentPosition());
        outState.putBoolean("state",videoView.isPlaying());
    }
//
//    @Override
//    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
//        super.onViewStateRestored(savedInstanceState);
//        if(savedInstanceState!=null&&savedInstanceState.containsKey("time"))
//        videoOffset=savedInstanceState.getInt("time");
////        videoView.pause();
//
//
//
////        }
//    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view=inflater.inflate(R.layout.play_video_fragment,container,false);
        final Bundle bundle =getArguments();
        videoView=(VideoView) view.findViewById(R.id.videoview);

        videoView.setVideoURI(Uri.parse(bundle.getString("uri")));
        videoView.setKeepScreenOn(true);

        videoView.requestFocus();
        eventBus=((SongApplication)mainActivity.getApplication()).getEventBus();

        subtitleView=(TextView)view.findViewById(R.id.subtitle);

new Thread(new Runnable() {
    @Override
    public void run() {
        OpenSubtitle openSubtitle=new OpenSubtitle();
        try {
            openSubtitle.login();
            String filename = bundle.getString("filename");
            List<SubtitleInfo> subtitleInfoList = openSubtitle.getMovieSubsByName(filename, "1", "eng");
            if (subtitleInfoList.size() > 0) {
                subtitleInfoList.get(0).getSubDownloadLink();
                URL url = new URL(subtitleInfoList.get(0).getSubDownloadLink());
                InputStream stream = url.openStream();
                byte[] buffer = new byte[1024];
                GZIPInputStream gzis = new GZIPInputStream(stream);
                FileOutputStream out = new FileOutputStream(new File(mainActivity.getFilesDir(), filename));
                int len;
                while ((len = gzis.read(buffer)) > 0) {
                    out.write(buffer, 0, len);
                }
                gzis.close();
                out.close();
                System.out.println("Done");
                FileInputStream inputStream = new FileInputStream(new File(mainActivity.getFilesDir(), filename));
                FormatSRT formatSRT = new FormatSRT();
                srt = formatSRT.parseFile(filename, inputStream);
                subtitleDisplayHandler.post(subtitle);
            }

            } catch(XmlRpcException e){
                e.printStackTrace();
            } catch(MalformedURLException e){
                e.printStackTrace();
            } catch(IOException e){
                e.printStackTrace();
            }

    }
}).start();


//         videoView.seekTo(currentPosition);

        MediaController mediaController = new MediaController(mainActivity);
        mediaController.setAnchorView(videoView);

//        videoView.setMediaController(mediaController);
        videoView.setOnPreparedListener(new OnPreparedListener() {
            @Override
            public void onPrepared() {
                videoView.seekTo(currentPosition);
                videoView.start();
//                videoView.setOnCompletionListener(new OnCompletionListener() {
//                    @Override
//                    public void onCompletion() {
//                        mainActivity.onBackPressed();
//                    }
//                });

            }
        });

//        OpenSubtitle openSubtitle = new OpenSubtitle();
//        try {
//            openSubtitle.login();
//          List<SubtitleInfo> subtitleInfoList= openSubtitle.getMovieSubsByName("now you see me","20","eng");
//        } catch (XmlRpcException e) {
//            e.printStackTrace();
//        }


        return view;
    }



    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {

    }

    @Override
    public void onResume() {
        super.onResume();
        ToggleButton toggleButton=mainActivity.getToggleButton();
        pastVisibility=(mainActivity.getPlayercontroller().getVisibility()==View.VISIBLE);
        if(pastVisibility&&!toggleButton.isChecked()) {
            eventBus.post(new AudioMusicEvent(AudioMusicEvent.PAUSE, null));
            toggleButton.setChecked(true);
        }
        if(pastVisibility)
        {
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.setMargins(0,0,0,0);
            mainActivity.getBodyContent().setLayoutParams(layoutParams);
            mainActivity.getPlayercontroller().setVisibility(View.GONE);
            mainActivity.getMaxTime().setVisibility(View.GONE);
            mainActivity.getStartTime().setVisibility(View.GONE);
        }
        getMainActivity().getSupportActionBar().setHomeAsUpIndicator(new IconicsDrawable(getMainActivity(), GoogleMaterial.Icon.gmd_arrow_back).color(Color.WHITE).sizeDp(IconicsDrawable.ANDROID_ACTIONBAR_ICON_SIZE_DP).paddingDp(IconicsDrawable.ANDROID_ACTIONBAR_ICON_SIZE_PADDING_DP));
        getMainActivity().setShowBackIconButton(true);

    }


    @Override
    public void onPause() {
        super.onPause();
        ToggleButton toggleButton = mainActivity.getToggleButton();
        if(pastVisibility&&toggleButton.isChecked()) {
            eventBus.post(new AudioMusicEvent(AudioMusicEvent.PAUSE, null));
            toggleButton.setChecked(false);
        }
        if(pastVisibility) {
            this.currentPosition = videoView.getCurrentPosition();
            mainActivity.getPlayercontroller().setVisibility(View.VISIBLE);
            mainActivity.getMaxTime().setVisibility(View.VISIBLE);
            mainActivity.getStartTime().setVisibility(View.VISIBLE);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.setMargins(0, 0, 0, (int) dpFromPx(mainActivity, 90));
            mainActivity.getBodyContent().setLayoutParams(layoutParams);
        }
        getMainActivity().getSupportActionBar().setHomeAsUpIndicator(new IconicsDrawable(getMainActivity(), GoogleMaterial.Icon.gmd_menu).color(Color.WHITE).sizeDp(IconicsDrawable.ANDROID_ACTIONBAR_ICON_SIZE_DP).paddingDp(IconicsDrawable.ANDROID_ACTIONBAR_ICON_SIZE_PADDING_DP));
        getMainActivity().setShowBackIconButton(false);


    }


    public static float dpFromPx(final Context context, final float px) {
        return px / context.getResources().getDisplayMetrics().density;
    }
}

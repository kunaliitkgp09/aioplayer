package com.aioplayer.adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.text.Html;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.aioplayer.MainActivity;
import com.aioplayer.R;
import com.aioplayer.application.SongApplication;
import com.aioplayer.dao.AudioSong;
import com.aioplayer.dao.PlayAudioMusic;
import com.aioplayer.dao.PlayListItem;
import com.aioplayer.dao.PodCastItem;
import com.aioplayer.dao.PodCastItemDownload;
import com.aioplayer.event.AudioMusicEvent;
import com.aioplayer.event.FileDownloadEvent;
import com.aioplayer.fragment.PlayVideoFragment;
import com.aioplayer.fragment.VideoFragment;
import com.aioplayer.manager.DbManager;
import com.bumptech.glide.Glide;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.squareup.otto.Bus;
import com.tonyodev.fetch.request.Request;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;



/**
 * Created by akankshadhanda on 30/07/17.
 */

public class PodCastItemsAdapter extends BaseAdapter {
    private List<PodCastItem> podCastItem;
    private Context context;
    private Bus eventBus;
    private DbManager dbManager;
    public PodCastItemsAdapter(List<PodCastItem> podCastItem,Context context)
    {
    this.podCastItem=podCastItem;
        this.context=context;
        this.eventBus=((SongApplication)context.getApplicationContext()).getEventBus();
        this.dbManager=new DbManager(context);
    }
    @Override
    public int getCount() {
        return podCastItem.size();
    }

    @Override
    public PodCastItem getItem(int i) {
        return podCastItem.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater)   context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.podcast_list_item, null);
        ImageView imageView=(ImageView) view.findViewById(R.id.imageview);
        final ImageView menupopup=(ImageView) view.findViewById(R.id.menupopup);
        final PodCastItem podCastItem=getItem(i);
        menupopup.setImageDrawable(new IconicsDrawable(context, GoogleMaterial.Icon.gmd_add_circle).color(Color.BLACK).sizeDp(IconicsDrawable.ANDROID_ACTIONBAR_ICON_SIZE_DP).paddingDp(IconicsDrawable.ANDROID_ACTIONBAR_ICON_SIZE_PADDING_DP));
        menupopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              showPopup(menupopup,podCastItem);
            }
        });
        TextView title=(TextView)view.findViewById(R.id.title);
        title.setText(podCastItem.getTitle());
        TextView subtitle=(TextView)view.findViewById(R.id.subtitle);
        subtitle.setText(Html.fromHtml(podCastItem.getDescription()));
        final ImageView downloadICon=(ImageView) view.findViewById(R.id.downloadicon);
        if(podCastItem.getFeedUrl()!=null) {
            PodCastItemDownload podCastItemDownload = dbManager.findPodCastItemwithId(podCastItem.getFeedUrl());
            downloadICon.setImageDrawable(new IconicsDrawable(context, GoogleMaterial.Icon.gmd_file_download).color(Color.BLACK).sizeDp(IconicsDrawable.ANDROID_ACTIONBAR_ICON_SIZE_DP).paddingDp(IconicsDrawable.ANDROID_ACTIONBAR_ICON_SIZE_PADDING_DP));
            if (podCastItemDownload == null)

            {
                downloadICon.setVisibility(View.VISIBLE);
            }
            else if(!podCastItemDownload.isDownloadCompeleted()) {
                downloadICon.setVisibility(View.VISIBLE);
                downloadICon.setImageDrawable(new IconicsDrawable(context, GoogleMaterial.Icon.gmd_pause_circle_outline).color(Color.BLACK).sizeDp(IconicsDrawable.ANDROID_ACTIONBAR_ICON_SIZE_DP).paddingDp(IconicsDrawable.ANDROID_ACTIONBAR_ICON_SIZE_PADDING_DP));
            } else {
                downloadICon.setVisibility(View.VISIBLE);
                downloadICon.setImageDrawable(new IconicsDrawable(context, GoogleMaterial.Icon.gmd_done).color(Color.BLACK).sizeDp(IconicsDrawable.ANDROID_ACTIONBAR_ICON_SIZE_DP).paddingDp(IconicsDrawable.ANDROID_ACTIONBAR_ICON_SIZE_PADDING_DP));
            }

            downloadICon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!dbManager.podCastItemExits(new PodCastItemDownload(podCastItem))) {
                        FileDownloadEvent fileDownloadEvent = new FileDownloadEvent();
                        fileDownloadEvent.setUrl(podCastItem.getFeedUrl());
                        fileDownloadEvent.setType("download");
                        eventBus.post(fileDownloadEvent);
                        dbManager.savePodcastItemObject(new PodCastItemDownload(podCastItem));
                        downloadICon.setImageDrawable(new IconicsDrawable(context, GoogleMaterial.Icon.gmd_pause_circle_outline).color(Color.BLACK).sizeDp(IconicsDrawable.ANDROID_ACTIONBAR_ICON_SIZE_DP).paddingDp(IconicsDrawable.ANDROID_ACTIONBAR_ICON_SIZE_PADDING_DP));

                    } else {
                        downloadICon.setImageDrawable(new IconicsDrawable(context, GoogleMaterial.Icon.gmd_file_download).color(Color.BLACK).sizeDp(IconicsDrawable.ANDROID_ACTIONBAR_ICON_SIZE_DP).paddingDp(IconicsDrawable.ANDROID_ACTIONBAR_ICON_SIZE_PADDING_DP));
                        FileDownloadEvent fileDownloadEvent = new FileDownloadEvent();
                        fileDownloadEvent.setUrl(podCastItem.getFeedUrl());
                        fileDownloadEvent.setType("cancel");
                        dbManager.removeObject(new PodCastItemDownload(podCastItem));
                        eventBus.post(fileDownloadEvent);
                    }

                }
            });
        }
        TextView podCastDuration=(TextView)view.findViewById(R.id.duration);
        TextView timeStamp=(TextView)view.findViewById(R.id.timestamp);
        try
        {
        long duration=Long.parseLong(podCastItem.getDuration());
            podCastDuration.setText(podCastItem.getDuration());
            long minutes=duration;
            long hours=minutes/60;
            minutes=minutes%60;
            if(hours==0)
            {
                podCastDuration.setText(String.valueOf(minutes));
            }
            else
            {
                podCastDuration.setText(String.valueOf(hours+":"+minutes));
            }
        }
        catch (Exception e)
        {
            podCastDuration.setText(podCastItem.getDuration());
        }



        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss -0000");
        Date simpleDate=simpleDateFormat.parse(podCastItem.getPubDate(),new ParsePosition(0));
        if(simpleDate!=null) {
            String newstring = new SimpleDateFormat("yyyy-MM-dd").format(simpleDate);
            timeStamp.setText(newstring);
        }


//
        Glide.with(context)
                .load(podCastItem.getPodcastUrl()).placeholder(R.drawable.cast_ic_notification_play)
                .into(imageView);
        return view;
    }

    public void showPopup(View v,final PodCastItem podCastItem) {
        PopupMenu popup = new PopupMenu(context, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.main, popup.getMenu());
        popup.show();
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId()==R.id.createplaylist)
                {
                    createPlayList(false,podCastItem);
                }
                if(item.getItemId()==R.id.addtoplaylist)
                {
                    createAddtoPlaylist(podCastItem);
                }
                if(item.getItemId()==R.id.addtoqueque)
                {
                    List<PlayAudioMusic> playAudioMusics = new ArrayList<PlayAudioMusic>();
                    PlayAudioMusic playAudioMusic = new PlayAudioMusic(podCastItem);
                    playAudioMusics.add(playAudioMusic);
                    eventBus.post(new AudioMusicEvent(AudioMusicEvent.ADD,playAudioMusics));
                }

                return false;
            }
        });
    }

    public void createAddtoPlaylist(final PodCastItem song)
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
                playListItem.getAudioSongs().add(new AudioSong(song));
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

    public void createPlayList(final boolean fromPlayList, final PodCastItem audioSong)
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

    public boolean fileExits(String url)
    {
        String[] fileParts=url.replaceAll("//","").split("/");
        File file = new File(context.getFilesDir().getAbsolutePath(),fileParts[fileParts.length-1]);
        return file.exists();

    }

    public void runWithType(String type,String url)
    {
        if(type.contains("video"))
        {
            Bundle bundle = new Bundle();
            bundle.putString("uri", url);
            ((MainActivity)context).showNextView(new PlayVideoFragment(),bundle);
        }
    }


}

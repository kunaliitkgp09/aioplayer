package com.aioplayer.adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.PopupMenu;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aioplayer.MainActivity;
import com.aioplayer.R;
import com.aioplayer.application.SongApplication;
import com.aioplayer.dao.AudioSong;
import com.aioplayer.dao.PlayAudioMusic;
import com.aioplayer.dao.PlayListHeader;
import com.aioplayer.dao.PlayListItem;
import com.aioplayer.event.AudioMusicEvent;
import com.aioplayer.fragment.AudioFragment;
import com.aioplayer.manager.DbManager;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.squareup.otto.Bus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;





/**
 * Created by akankshadhanda on 07/08/17.
 */
 public class PlayListAdapter extends BaseExpandableListAdapter {

        private MainActivity _mainActivity;
        private List<PlayListHeader> _listDataHeader; // header titles
        // child data in format of header title, child title
        private HashMap<String, Collection<AudioSong>> _listDataChild;
        private Bus bus;
        private DbManager dbManager;

        public PlayListAdapter(MainActivity mainActivity, List<PlayListHeader> listDataHeader,
                                     HashMap<String, Collection<AudioSong>> listChildData) {
            this._mainActivity = mainActivity;
            this._listDataHeader = listDataHeader;
            this._listDataChild = listChildData;
            this.bus=((SongApplication)mainActivity.getApplication()).getEventBus();
            this.dbManager = new DbManager(_mainActivity);
        }

        @Override
        public AudioSong getChild(int groupPosition, int childPosititon) {
            Collection<AudioSong> audioSongs=_listDataChild.get(_listDataHeader.get(groupPosition).getTitle());
            LinkedList<AudioSong> audioSongTemp = new LinkedList<>();
            audioSongTemp.addAll(audioSongs);
            return audioSongTemp.get(childPosititon);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public View getChildView(int groupPosition, final int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {

            final AudioSong audioSong = (AudioSong) getChild(groupPosition, childPosition);

            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this._mainActivity
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.song_item, null);
            }

            TextView musicTitle=((TextView)convertView.findViewById(R.id.musictitle));
            TextView albumName=((TextView)convertView.findViewById(R.id.albumname));
            ImageView circularImageView= ((ImageView) convertView.findViewById(R.id.circularimageview));



            convertView.findViewById(R.id.playlistmenu).setBackground(new IconicsDrawable(_mainActivity, GoogleMaterial.Icon.gmd_add_circle).color(Color.BLACK).sizeDp(IconicsDrawable.ANDROID_ACTIONBAR_ICON_SIZE_DP).paddingDp(IconicsDrawable.ANDROID_ACTIONBAR_ICON_SIZE_PADDING_DP));
//            ImageButton playlistmenu=(ImageButton)convertView.findViewById(R.id.playlistmenu);

            musicTitle.setText(audioSong.getThisTitle());
            albumName.setText(audioSong.getAlbumName());
            circularImageView.setImageBitmap(audioSong.getAlbumArt());
//            playlistmenu.setImageDrawable(_mainActivity.getResources().getDrawable(R.drawable.musicicon));
//            playlistmenu.setVisibility(View.GONE);

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!_mainActivity.getCasty().isConnected()) {
                        _mainActivity.getToggleButton().setBackground(new IconicsDrawable(_mainActivity, GoogleMaterial.Icon.gmd_pause).color(Color.BLACK).sizeDp(IconicsDrawable.ANDROID_ACTIONBAR_ICON_SIZE_DP).paddingDp(IconicsDrawable.ANDROID_ACTIONBAR_ICON_SIZE_PADDING_DP));
                        List<PlayAudioMusic> urls = new ArrayList<>();
                        PlayAudioMusic audioMusic=new PlayAudioMusic(audioSong);
                        urls.add(audioMusic);
                        bus.post(new AudioMusicEvent(AudioMusicEvent.PLAY, urls));
                    }
                    else
                    {
                        bus.post(audioSong);
                    }

                }
            });
            return convertView;
        }


        public void createDialog(View anchorView, final PlayListHeader headerTitle)
        {
            PopupMenu popup = new PopupMenu(_mainActivity,anchorView);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.menu_playlist_group, popup.getMenu());
            popup.show();
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if(item.getItemId()==R.id.playall)
                    {
                       Collection<AudioSong>  audioSongs=_listDataChild.get(headerTitle.getTitle());
                        PlayListItem playListItem=dbManager.findPlayListItemwithId(headerTitle.getTitle());
                        List<PlayAudioMusic> playAudioMusics = new ArrayList<PlayAudioMusic>();
                        for(AudioSong audioSong:audioSongs)
                        {

                         PlayAudioMusic playAudioMusic = new PlayAudioMusic(audioSong);
                            playAudioMusics.add(playAudioMusic);
                        }

                        AudioMusicEvent audioMusicEvent = new AudioMusicEvent(AudioMusicEvent.PLAY,playAudioMusics);
                        bus.post(audioMusicEvent);

                    }
                    if(item.getItemId()==R.id.removeplaylist)
                    {
                       PlayListItem playListItem=dbManager.findPlayListItemwithId(headerTitle.getTitle());
                        dbManager.removeObject(playListItem);
                        _listDataHeader.remove(headerTitle);
                        notifyDataSetChanged();
                    }
                    return false;
                }
            });
        }

    public static float dpFromPx(final Context context, final float px) {
        return px / context.getResources().getDisplayMetrics().density;
    }

        @Override
        public int getChildrenCount(int groupPosition) {
            return this._listDataChild.get(this._listDataHeader.get(groupPosition).getTitle())
                    .size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return this._listDataHeader.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            return this._listDataHeader.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {


            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this._mainActivity
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.playlist_group_view, null);
            }

            final PlayListHeader headerTitle = (PlayListHeader) getGroup(groupPosition);
            ImageView lblListImage = convertView.findViewById(R.id.playmusicvideo);
            TextView lblListHeader = convertView.findViewById(R.id.playlistitle);
            final ImageView  optionpopup= ((ImageView) convertView.findViewById(R.id.optionpopup));
            optionpopup.setImageDrawable(new IconicsDrawable(_mainActivity, GoogleMaterial.Icon.gmd_add_circle).color(Color.BLACK).sizeDp(IconicsDrawable.ANDROID_ACTIONBAR_ICON_SIZE_DP).paddingDp(IconicsDrawable.ANDROID_ACTIONBAR_ICON_SIZE_PADDING_DP));
            optionpopup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    createDialog(optionpopup,headerTitle);
                }
            });


            lblListHeader.setText(headerTitle.getTitle());
            lblListImage.setImageBitmap(headerTitle.getImgaeUrl());


            return convertView;
        }
    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = _mainActivity.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }
        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

}

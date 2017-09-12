package com.aioplayer;


import android.Manifest;
import android.app.ActionBar;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.content.Intent;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.MediaRouteButton;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.aioplayer.application.SongApplication;
import com.aioplayer.dao.PlayAudioMusic;
import com.aioplayer.event.AudioMusicEvent;
import com.aioplayer.event.PlayMediaContent;
import com.aioplayer.event.ShowAudioController;
import com.aioplayer.fragment.AudioFragment;
import com.aioplayer.fragment.BaseFragment;
import com.aioplayer.fragment.HomeFragment;
import com.aioplayer.fragment.ImageGalleryViewerFragment;
import com.aioplayer.fragment.PlayListFragment;
import com.aioplayer.fragment.PodCastFragment;
import com.aioplayer.fragment.VideoFragment;
import com.aioplayer.server.FileServer;
import com.aioplayer.service.AudioMusicService;
import com.aioplayer.service.FileDownloadService;
import com.aioplayer.service.MediaCastService;
import com.aioplayer.utils.Constants;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;


import be.ceau.itunessearch.Searcher;
import be.ceau.itunessearch.enums.Entity;
import be.ceau.itunessearch.enums.Media;
import be.ceau.itunessearch.models.Request;
import be.ceau.itunessearch.models.Response;
import be.ceau.itunessearch.models.Result;
import pl.droidsonroids.casty.Casty;

import static android.view.View.GONE;
import static java.security.AccessController.getContext;



/*
 * This is demo code to accompany the Mobiletuts+ series:
 * Android SDK: Creating a Music Player
 * 
 * Sue Smith - February 2014
 */

public class MainActivity extends AppCompatActivity {
	private TabLayout tabLayout;
	private ViewPager viewPager;
	private FrameLayout frameLayout;
	private ListView mDrawerList;
	private DrawerLayout drawerLayout;
	private Casty casty;
	private ActionBarDrawerToggle mDrawerToggle;
	private boolean isDrawerOpen;

	private ToggleButton toggleButton;
	private SeekBar seekBar;
	private LinearLayout playercontroller;
	private TextView maxTime;
	private TextView startTime;
	private ProgressBar progressBar;
	private View rewindIcon;
	private View fastforwardicon;
	private View onNext;
	private View onReplay;
	private View previousICon;
   private View bodyContent;
	private boolean showBackIconButton;
	private int shuffleIndex=0;
	private int playIcon=0;

	public boolean isShowBackIconButton() {
		return showBackIconButton;
	}

	public void setShowBackIconButton(boolean showBackIconButton) {
		this.showBackIconButton = showBackIconButton;
	}

	public View getRewindIcon() {
		return rewindIcon;
	}

	public void setRewindIcon(View rewindIcon) {
		this.rewindIcon = rewindIcon;
	}

	public View getFastforwardicon() {
		return fastforwardicon;
	}

	public void setFastforwardicon(View fastforwardicon) {
		this.fastforwardicon = fastforwardicon;
	}

	public View getOnNext() {
		return onNext;
	}

	public void setOnNext(View onNext) {
		this.onNext = onNext;
	}

	public View getOnReplay() {
		return onReplay;
	}

	public void setOnReplay(View onReplay) {
		this.onReplay = onReplay;
	}

	public View getPreviousICon() {
		return previousICon;
	}

	public void setPreviousICon(View previousICon) {
		this.previousICon = previousICon;
	}

	public ToggleButton getToggleButton() {
		return toggleButton;
	}

	public void setToggleButton(ToggleButton toggleButton) {
		this.toggleButton = toggleButton;
	}

	public SeekBar getSeekBar() {
		return seekBar;
	}

	public void setSeekBar(SeekBar seekBar) {
		this.seekBar = seekBar;
	}

	public LinearLayout getPlayercontroller() {
		return playercontroller;
	}

	public void setPlayercontroller(LinearLayout playercontroller) {
		this.playercontroller = playercontroller;
	}

	public TextView getMaxTime() {
		return maxTime;
	}

	public void setMaxTime(TextView maxTime) {
		this.maxTime = maxTime;
	}

	public TextView getStartTime() {
		return startTime;
	}

	public void setStartTime(TextView startTime) {
		this.startTime = startTime;
	}

	public ProgressBar getProgressBar() {
		return progressBar;
	}

	public void setProgressBar(ProgressBar progressBar) {
		this.progressBar = progressBar;
	}

	public Casty getCasty(){
		return casty;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_ACTION_BAR);
		setContentView(R.layout.activity_main);
		frameLayout=(FrameLayout) findViewById(R.id.mainlayout);
		playercontroller =(LinearLayout) findViewById(R.id.playercontroller);
		progressBar=(ProgressBar)findViewById(R.id.progressBar);
		toggleButton=(ToggleButton)findViewById(R.id.toggleplaypause);
		maxTime=(TextView)findViewById(R.id.maxtime);
		startTime=(TextView)findViewById(R.id.currenttime);
        seekBar=(SeekBar)findViewById(R.id.seekbar);
		startTime=(TextView)findViewById(R.id.currenttime);
		rewindIcon=findViewById(R.id.rewindicon);
		fastforwardicon=findViewById(R.id.fastforwardicon);
		onNext=findViewById(R.id.onnext);
		onReplay=findViewById(R.id.onreplay);
		previousICon=findViewById(R.id.previousicon);
		bodyContent=findViewById(R.id.actionbarrelative);

        toggleButton.setBackground(new IconicsDrawable(MainActivity.this, GoogleMaterial.Icon.gmd_pause).color(Color.BLACK).sizeDp(IconicsDrawable.ANDROID_ACTIONBAR_ICON_SIZE_DP).paddingDp(IconicsDrawable.ANDROID_ACTIONBAR_ICON_SIZE_PADDING_DP));

		toggleButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				playIcon=playIcon%2;
				if(playIcon==0)
				{
					toggleButton.setBackground(new IconicsDrawable(MainActivity.this, GoogleMaterial.Icon.gmd_play_arrow).color(Color.BLACK).sizeDp(IconicsDrawable.ANDROID_ACTIONBAR_ICON_SIZE_DP).paddingDp(IconicsDrawable.ANDROID_ACTIONBAR_ICON_SIZE_PADDING_DP));
				}
				if(playIcon==1)
				{
					toggleButton.setBackground(new IconicsDrawable(MainActivity.this, GoogleMaterial.Icon.gmd_pause).color(Color.BLACK).sizeDp(IconicsDrawable.ANDROID_ACTIONBAR_ICON_SIZE_DP).paddingDp(IconicsDrawable.ANDROID_ACTIONBAR_ICON_SIZE_PADDING_DP));
				}
				playIcon++;
				Bus bus=((SongApplication)getApplication().getApplicationContext()).getEventBus();
				bus.post(new AudioMusicEvent(AudioMusicEvent.PAUSE,null));
			}
		});
		onNext.setBackground(new IconicsDrawable(MainActivity.this, GoogleMaterial.Icon.gmd_skip_next).color(Color.BLACK).sizeDp(IconicsDrawable.ANDROID_ACTIONBAR_ICON_SIZE_DP).paddingDp(IconicsDrawable.ANDROID_ACTIONBAR_ICON_SIZE_PADDING_DP));
		previousICon.setBackground(new IconicsDrawable(MainActivity.this, GoogleMaterial.Icon.gmd_skip_previous).color(Color.BLACK).sizeDp(IconicsDrawable.ANDROID_ACTIONBAR_ICON_SIZE_DP).paddingDp(IconicsDrawable.ANDROID_ACTIONBAR_ICON_SIZE_PADDING_DP));
		fastforwardicon.setBackground(new IconicsDrawable(MainActivity.this, GoogleMaterial.Icon.gmd_fast_forward).color(Color.BLACK).sizeDp(IconicsDrawable.ANDROID_ACTIONBAR_ICON_SIZE_DP).paddingDp(IconicsDrawable.ANDROID_ACTIONBAR_ICON_SIZE_PADDING_DP));
		rewindIcon.setBackground(new IconicsDrawable(MainActivity.this, GoogleMaterial.Icon.gmd_fast_rewind).color(Color.BLACK).sizeDp(IconicsDrawable.ANDROID_ACTIONBAR_ICON_SIZE_DP).paddingDp(IconicsDrawable.ANDROID_ACTIONBAR_ICON_SIZE_PADDING_DP));
		onReplay.setBackground(new IconicsDrawable(MainActivity.this, GoogleMaterial.Icon.gmd_loupe).color(Color.BLACK).sizeDp(IconicsDrawable.ANDROID_ACTIONBAR_ICON_SIZE_DP).paddingDp(IconicsDrawable.ANDROID_ACTIONBAR_ICON_SIZE_PADDING_DP));

		PrimaryDrawerItem item1 = new PrimaryDrawerItem().withIdentifier(1).withName("Audio").withIcon(GoogleMaterial.Icon.gmd_library_music);
		SecondaryDrawerItem item2 = new SecondaryDrawerItem().withIdentifier(2).withName("Video").withIcon(GoogleMaterial.Icon.gmd_featured_video);
		SecondaryDrawerItem item3 = new SecondaryDrawerItem().withIdentifier(4).withName("PodCast").withIcon(GoogleMaterial.Icon.gmd_radio);
		SecondaryDrawerItem item4 = new SecondaryDrawerItem().withIdentifier(5).withName("PlayList").withIcon(GoogleMaterial.Icon.gmd_radio);
		final Drawer result = new DrawerBuilder().withActivity(this).withRootView(R.id.actionbarrelative)
				.withActionBarDrawerToggle(true).withCloseOnClick(true).withActionBarDrawerToggle(true).withActionBarDrawerToggleAnimated(true).withDisplayBelowStatusBar(true)
				.addDrawerItems(new DividerDrawerItem(),new DividerDrawerItem(),new DividerDrawerItem(),
						item1,
						item2,
						new SecondaryDrawerItem().withName("Photo").withIdentifier(3).withIcon(GoogleMaterial.Icon.gmd_photo),item3
				,item4).build();
		result.setOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
			@Override
			public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
				// do something with the clicked item :D
				popBackStack();
				android.support.v7.app.ActionBar actionBar=getSupportActionBar();
				result.closeDrawer();

				if(drawerItem.getIdentifier()==1)
				{
					showNextView(new AudioFragment(),null);
					actionBar.setTitle("Audio");
				}
				if(drawerItem.getIdentifier()==2)
				{
					showNextView(new VideoFragment(),null);
					actionBar.setTitle("Video");
				}
				if(drawerItem.getIdentifier()==3)
				{
					showNextView(new ImageGalleryViewerFragment(),null);
					actionBar.setTitle("Photo");
				}
				if(drawerItem.getIdentifier()==4)
				{
					showNextView(new PodCastFragment(),null);
					actionBar.setTitle("PodCast");
				}
				if(drawerItem.getIdentifier()==5)
				{
					showNextView(new PlayListFragment(),null);
					actionBar.setTitle("PlayList");
				}
				return true;
			}
		});
      fastforwardicon.setOnClickListener(new View.OnClickListener() {
		  @Override
		  public void onClick(View view) {
			  Bus bus=((SongApplication)getApplication().getApplicationContext()).getEventBus();
			  bus.post(new AudioMusicEvent(AudioMusicEvent.FASTFORWARD,null));
		  }
	  });
		rewindIcon.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Bus bus=((SongApplication)getApplication().getApplicationContext()).getEventBus();
				bus.post(new AudioMusicEvent(AudioMusicEvent.REWIND,null));
			}
		});

		onReplay.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				shuffleIndex=shuffleIndex%3;
				if(shuffleIndex==0) {
					onReplay.setBackground(new IconicsDrawable(MainActivity.this, GoogleMaterial.Icon.gmd_loupe).color(Color.BLACK).sizeDp(IconicsDrawable.ANDROID_ACTIONBAR_ICON_SIZE_DP).paddingDp(IconicsDrawable.ANDROID_ACTIONBAR_ICON_SIZE_PADDING_DP));
					Bus bus=((SongApplication)getApplication().getApplicationContext()).getEventBus();
					bus.post(new AudioMusicEvent(AudioMusicEvent.NORMALPLAY,null));

				}
				if(shuffleIndex==1) {
					onReplay.setBackground(new IconicsDrawable(MainActivity.this, GoogleMaterial.Icon.gmd_loop).color(Color.BLACK).sizeDp(IconicsDrawable.ANDROID_ACTIONBAR_ICON_SIZE_DP).paddingDp(IconicsDrawable.ANDROID_ACTIONBAR_ICON_SIZE_PADDING_DP));
					Bus bus=((SongApplication)getApplication().getApplicationContext()).getEventBus();
					bus.post(new AudioMusicEvent(AudioMusicEvent.LOOPONE,null));

				}
				if(shuffleIndex==2) {
					onReplay.setBackground(new IconicsDrawable(MainActivity.this, GoogleMaterial.Icon.gmd_shuffle).color(Color.BLACK).sizeDp(IconicsDrawable.ANDROID_ACTIONBAR_ICON_SIZE_DP).paddingDp(IconicsDrawable.ANDROID_ACTIONBAR_ICON_SIZE_PADDING_DP));
					Bus bus=((SongApplication)getApplication().getApplicationContext()).getEventBus();
					bus.post(new AudioMusicEvent(AudioMusicEvent.SHUFFLE,null));
				}
				shuffleIndex++;
			}
		});
		drawerLayout=result.getDrawerLayout();
		onNext.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Bus bus=((SongApplication)getApplication().getApplicationContext()).getEventBus();
				bus.post(new AudioMusicEvent(AudioMusicEvent.NEXT,null));
			}
		});
		previousICon.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Bus bus=((SongApplication)getApplication().getApplicationContext()).getEventBus();
				bus.post(new AudioMusicEvent(AudioMusicEvent.PREVIOUS,null));
			}
		});
		getSupportActionBar().setHomeAsUpIndicator(new IconicsDrawable(MainActivity.this, GoogleMaterial.Icon.gmd_menu).color(Color.WHITE).sizeDp(IconicsDrawable.ANDROID_ACTIONBAR_ICON_SIZE_DP).paddingDp(IconicsDrawable.ANDROID_ACTIONBAR_ICON_SIZE_PADDING_DP));
		result.getDrawerLayout().addDrawerListener(new DrawerLayout.DrawerListener() {
			@Override
			public void onDrawerSlide(View drawerView, float slideOffset) {

			}

			@Override
			public void onDrawerOpened(View drawerView) {
				getSupportActionBar().setHomeAsUpIndicator(new IconicsDrawable(MainActivity.this, GoogleMaterial.Icon.gmd_arrow_back).color(Color.WHITE).sizeDp(IconicsDrawable.ANDROID_ACTIONBAR_ICON_SIZE_DP).paddingDp(IconicsDrawable.ANDROID_ACTIONBAR_ICON_SIZE_PADDING_DP));
			   isDrawerOpen=true;
			}

			@Override
			public void onDrawerClosed(View drawerView) {
				getSupportActionBar().setHomeAsUpIndicator(new IconicsDrawable(MainActivity.this, GoogleMaterial.Icon.gmd_menu).color(Color.WHITE).sizeDp(IconicsDrawable.ANDROID_ACTIONBAR_ICON_SIZE_DP).paddingDp(IconicsDrawable.ANDROID_ACTIONBAR_ICON_SIZE_PADDING_DP));
				isDrawerOpen=false;
			}

			@Override
			public void onDrawerStateChanged(int newState) {

			}
		});
//		RelativeLayout.LayoutParams drawerlayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//		drawerlayoutParams.topMargin=100;
//		((DrawerLayout.LayoutParams)frameLayout.getLayoutParams()).topMargin=-(100);
//		drawerLayout.setLayoutParams(drawerlayoutParams);


//		ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, mToolbar, R.string.open, R.string.close);
//		// Setting the actionbarToggle to drawer layout
//		drawerLayout.setDrawerListener(mDrawerToggle);
		// Calling sync state is necessary to show your hamburger icon...
		// or so I hear. Doesn't hurt including it even if you find it works
		// without it on your test device(s)
//		mDrawerToggle.syncState();

		casty = Casty.create(this)
				.withMiniController();

		if(null == savedInstanceState) {
			// set you initial fragment object

			Dexter.withActivity(this)
					.withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.ACCESS_WIFI_STATE,Manifest.permission.WRITE_EXTERNAL_STORAGE)
					.withListener(new MultiplePermissionsListener() {
						@Override
						public void onPermissionsChecked(MultiplePermissionsReport report) {
							showNextView(new AudioFragment(),null);
							Intent service = new Intent(MainActivity.this,AudioMusicService.class);
							service.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
							startService(service);
							service = new Intent(MainActivity.this,MediaCastService.class);
							startService(service);
							service = new Intent(MainActivity.this,FileDownloadService.class);
							startService(service);
						}

						@Override
						public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

						}
					}).check();
//			result.getActionBarDrawerToggle().setDrawerIndicatorEnabled(false);
		    getSupportActionBar().setHomeButtonEnabled(true);
			getSupportActionBar().setDisplayShowHomeEnabled(true);
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			getSupportActionBar().show();
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
					List<PlayAudioMusic> urls = new ArrayList<>();
					PlayAudioMusic playAudioMusic = new PlayAudioMusic();
					playAudioMusic.setValue(String.valueOf(seekBar.getProgress()));
					urls.add(playAudioMusic);
					Bus bus=((SongApplication)getApplication().getApplicationContext()).getEventBus();
					bus.post(new AudioMusicEvent(AudioMusicEvent.MOVE,urls));
				}
			});


			};


//			result.setActionBarDrawerToggle(mDrawerToggle);
		casty.setOnConnectChangeListener(new Casty.OnConnectChangeListener() {
			@Override
			public void onConnected() {
				Bus bus=((SongApplication)getApplication().getApplicationContext()).getEventBus();
				bus.post(new AudioMusicEvent(AudioMusicEvent.PAUSE,null));
				playercontroller.setVisibility(GONE);
			}

			@Override
			public void onDisconnected() {
				Bus bus=((SongApplication)getApplication().getApplicationContext()).getEventBus();
				bus.post(new AudioMusicEvent(AudioMusicEvent.PAUSE,null));
				playercontroller.setVisibility(View.VISIBLE);
			}
		});
		playercontroller.setVisibility(GONE);
			((SongApplication)getApplication()).getEventBus().register(this);
//		MainActivityPermissionsDispatcher.fetchWifiPermissionWithCheck(this);
//		MainActivityPermissionsDispatcher.fetchReadStoragePermissionWithCheck(this);
//		MainActivityPermissionsDispatcher.fetchWriteStoragePermissionWithCheck(this);

	}

	public void enablingWiFiDisplay() {
		try {
			startActivity(new Intent("android.settings.WIFI_DISPLAY_SETTINGS"));
			return;
		} catch (ActivityNotFoundException activitynotfoundexception) {
			activitynotfoundexception.printStackTrace();
		}

		try {
			startActivity(new Intent("android.settings.CAST_SETTINGS"));
			return;
		} catch (Exception exception1) {
			Toast.makeText(getApplicationContext(), "Device not supported", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		((SongApplication)getApplication()).getEventBus().unregister(this);
	}


	public void showNextView(BaseFragment fragment, Bundle bundle)
	{

		if(bundle!=null)
		{
			fragment.setArguments(bundle);
		}
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.mainlayout, fragment);
		transaction.addToBackStack(fragment.getTag());
		transaction.commitAllowingStateLoss();
		getSupportActionBar().setHomeAsUpIndicator(new IconicsDrawable(MainActivity.this, GoogleMaterial.Icon.gmd_menu).color(Color.WHITE).sizeDp(IconicsDrawable.ANDROID_ACTIONBAR_ICON_SIZE_DP).paddingDp(IconicsDrawable.ANDROID_ACTIONBAR_ICON_SIZE_PADDING_DP));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		casty.addMediaRouteMenuItem(menu);
		return super.onCreateOptionsMenu(menu);
	}
	public void popBackStack()
	{
	   getSupportFragmentManager().popBackStack();
	}

	@Override
	public void onBackPressed() {


		if(getSupportFragmentManager().getBackStackEntryCount()==1)
		{
			finish();
		}
		super.onBackPressed();

	}
	@Subscribe
	public void subscribedMediaContent(PlayMediaContent playMediaContent)
	{
		try {
			getCasty().getPlayer().loadMediaAndPlay(playMediaContent.getMediaData());
		}
		catch (Exception exception)
		{
			exception.printStackTrace();
		}
	}
//	@Override
//	protected void onPostCreate(Bundle savedInstanceState) {
//		super.onPostCreate(savedInstanceState);
//		// Sync the toggle state after onRestoreInstanceState has occurred.
//		mDrawerToggle.syncState();
//	}
//
//	@Override
//	public void onConfigurationChanged(Configuration newConfig) {
//		super.onConfigurationChanged(newConfig);
//		mDrawerToggle.onConfigurationChanged(newConfig);
//	}
//
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Pass the event to ActionBarDrawerToggle, if it returns
		// true, then it has handled the app icon touch event

		if(item.getItemId()==android.R.id.home)
		{
			if(isDrawerOpen)
			{
				drawerLayout.closeDrawers();

			}
			else if(showBackIconButton)
			{
				onBackPressed();
			}
			else
			{
				drawerLayout.openDrawer(Gravity.START);
			}

		}
		// Handle your other action bar items...

		return super.onOptionsItemSelected(item);
	}

	class DrawerListener implements AdapterView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

		}
	}
	public int dpToPx(int dp) {
		DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
		return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
	}

	public View getBodyContent() {
		return bodyContent;
	}

	public void setBodyContent(View bodyContent) {
		this.bodyContent = bodyContent;
	}



	@Subscribe
	public void showMusicController(ShowAudioController audioController)
	{
		if(audioController.getMaxtime()>0) {
			startTime.setVisibility(View.VISIBLE);
			maxTime.setVisibility(View.VISIBLE);
			progressBar.setVisibility(GONE);

			playercontroller.setVisibility(View.VISIBLE);
			seekBar.setVisibility(View.VISIBLE);
			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
			layoutParams.setMargins(0,0,0,dpToPx(90));
			bodyContent.setLayoutParams(layoutParams);

			long startTimeMilli=audioController.getStartTime();
			long seconds=startTimeMilli/1000;
			long minutes=seconds/60;
			seconds=seconds%60;
			long hours=minutes/60;
			minutes=minutes%60;
			if(hours==0)
			{
				startTime.setText(String.valueOf(minutes + ":" + seconds));
			}
			else {
				startTime.setText(String.valueOf(hours + ":" + minutes + ":" + seconds));
			}

			startTimeMilli=audioController.getMaxtime();
			seconds=startTimeMilli/1000;
			minutes=seconds/60;
			seconds=seconds%60;
			hours=minutes/60;
			minutes=minutes%60;
			if(hours==0)
			{
				maxTime.setText(String.valueOf(minutes + ":" + seconds));
			}
			else {
				maxTime.setText(String.valueOf(hours + ":" + minutes + ":" + seconds));
			}

			seekBar.setProgress((int) ((100 * audioController.getStartTime()) / audioController.getMaxtime()));
		}
	}






}

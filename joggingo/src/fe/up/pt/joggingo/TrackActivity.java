package fe.up.pt.joggingo;


import java.util.List;

import oauth2.OAuthAccessTokenActivity;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.ActionBar.TabListener;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import fe.up.pt.joggingo.R;

import fragments.TrackFragment;

public class TrackActivity extends SherlockFragmentActivity implements TabListener {

	public static MenuItem menu_login;
	public static int user_id;
	Activity a = this;
	ViewPager mViewPager;
	ActionBar mActionBar;
	TabsAdapter mTabsAdapter;
	Bundle extras;
	private String access_token = null;
	private long track_id;
	GPSTracker gps = null;
	private DatabaseHandler db;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		extras = getIntent().getExtras();	
		db = new DatabaseHandler(this);
		String tab_title = null;
		if(extras != null){
			tab_title = extras.getString("name");
			String id =extras.getString("id");
			track_id = Long.valueOf(id);
		}
		
		if(PreferenceManager.getDefaultSharedPreferences(a).getString("access_token",null) != null){
			access_token = PreferenceManager.getDefaultSharedPreferences(a).getString("access_token",null);
			extras.putString("access_token", access_token);
		}

		mViewPager = new ViewPager(this);
		mViewPager.setId(R.id.pager);
		mViewPager.setBackgroundColor(Color.BLACK);
		setContentView(mViewPager);

		mActionBar = getSupportActionBar();
		mActionBar.setDisplayShowTitleEnabled(true);
		mActionBar.setHomeButtonEnabled(true);
		mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		mTabsAdapter = new TabsAdapter(this, mViewPager);

		mTabsAdapter.addTab(mActionBar.newTab().setText(tab_title),
				TrackFragment.TrackFragmentAux.class, extras);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_main, menu);
		menu_login = (MenuItem) menu.findItem(R.id.menu_LogIn);
		if(PreferenceManager.getDefaultSharedPreferences(a).getString("access_token", null) != null)
			menu_login.setTitle(JoggingoAPI.Strings.LOGIN);
		else
			menu_login.setTitle(JoggingoAPI.Strings.LOGOUT);
		
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			
			return false;

		case R.id.menu_LogIn:

			Intent intent = new Intent(TrackActivity.this,  OAuthAccessTokenActivity.class );
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			if(PreferenceManager.getDefaultSharedPreferences(a).getString("access_token", null) != null)
				intent.putExtra("logout", "true");
			
			
			startActivity(intent);
			//finish();

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onStop() {
		setResult(0);
		db.close();
		super.onStop();
	}
	@Override
	protected void onDestroy() {
		setResult(0);
		db.close();
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode==0){
			finish();
		}
	}

	public void goToMap(View v){

		gps = new GPSTracker(this);

		if(gps.canGetLocation()){

			double latitude = gps.getLatitude(); // returns latitude
			double longitude = gps.getLongitude(); // returns longitude

			Intent intent = new Intent(this, MainMapActivity.class);

			intent.putExtra("track", track_id);
			intent.putExtra("latitude", latitude);
			intent.putExtra("longitude", longitude);

			startActivity(intent);
		}
		else{
			gps.showSettingsAlert();
		}	
	}
	
	public void syncTrack(View v){
		
		if(PreferenceManager.getDefaultSharedPreferences(a).getString("access_token",null) != null){
			
			
			String id = null;
		
			if(extras != null){			
				id = extras.getString("id");
				Toast.makeText(TrackActivity.this, "Synchronizing...",
						Toast.LENGTH_SHORT).show();
				long track_id = Long.parseLong(id);
				new DownloadFilesTask(a,track_id, this, null,MainActivity.user_id).execute(db);
			}
		}
		else{
			TrackFragment.sync_one.setEnabled(false);
			Intent intent = new Intent(TrackActivity.this,  OAuthAccessTokenActivity.class );
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}
	}
	
	public void removeTrack(View v){
		

		String id = null;
	
		if(extras != null)	
			id = extras.getString("id");
		
		db.deleteTrack(Integer.parseInt(id));
		
		/*
		 * MUDAR A NOTIFICACAO
		 * */
		
		List<Track> tracks = db.getAllTracks();
		
		if(tracks.size() == 0)
			MainActivity.notifications_layout.setVisibility(View.GONE);
		else
			MainActivity.notifications_text.setText(MainActivity.notificationMessage(tracks.size()));
		
		Intent intent = new Intent(TrackActivity.this,TracksActivity.class);
		if(PreferenceManager.getDefaultSharedPreferences(a).getString("access_token",null) != null){
			intent.putExtra("access_token", PreferenceManager.getDefaultSharedPreferences(a).getString("access_token",null));
			startActivity(intent);
		}
		
		setResult(0);
		finish();
	
	}
	

	//--------------------------------------------------------------------------------------
	public void getAdvancedSearch(View v){


	}


	public void onTabSelected(Tab tab, FragmentTransaction ft) {

	}

	public void onTabUnselected(Tab tab, FragmentTransaction ft) {


	}

	public void onTabReselected(Tab tab, FragmentTransaction ft) {


	}
}
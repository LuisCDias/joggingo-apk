package fe.up.pt.joggingo;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import oauth2.OAuthAccessTokenActivity;
import android.app.Activity;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Camera.Size;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import fragments.MainFragment;
import fragments.MainFragment_Results;
import fragments.TracksFragment;

public class TracksActivity extends SherlockFragmentActivity implements TabListener {

	Activity a = this;
	ViewPager mViewPager;
	ActionBar mActionBar;
	TabsAdapter mTabsAdapter;
	Bundle extras;
	GPSTracker gps = null;
	private int user_id = 0;
	private String access_token = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		access_token = PreferenceManager.getDefaultSharedPreferences(a).getString("access_token",null);
		extras = new Bundle();
		
		extras = getIntent().getExtras();
		
		

		
		mViewPager = new ViewPager(this);
		mViewPager.setId(R.id.pager);
		mViewPager.setBackgroundColor(Color.BLACK);
		setContentView(mViewPager);

		mActionBar = getSupportActionBar();
		mActionBar.setDisplayShowTitleEnabled(true);
		mActionBar.setHomeButtonEnabled(true);
		mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		mTabsAdapter = new TabsAdapter(this, mViewPager);

		
		mTabsAdapter.addTab(mActionBar.newTab().setText("Unsynchronized"), //precisa do user_id para sincronizar
				TracksFragment.TracksFragmentAux.class, extras);
		if(access_token!=null){
			mTabsAdapter.addTab(mActionBar.newTab().setText("Synchronized"), //precisa do access_token para /profile/tracks
					TracksFragment.TracksFragmentSynced.class, extras);
		
			if(extras != null)
				if(extras.containsKey("user_id"))	
					mActionBar.setSelectedNavigationItem(1);
		
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_main, menu);
		MenuItem menu_login = (MenuItem) menu.findItem(R.id.menu_LogIn);
		if(PreferenceManager.getDefaultSharedPreferences(a).getString("access_token", null) == null)
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

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onStop() {
		setResult(0);
		super.onStop();
	}
	@Override
	protected void onDestroy() {
		setResult(0);
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode==0){
			finish();
		}
	}

	public boolean isOnline(){
		
		ConnectivityManager conMgr =  (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		if ( conMgr.getNetworkInfo(0).getState() == NetworkInfo.State.DISCONNECTED 
			    ||  conMgr.getNetworkInfo(1).getState() == NetworkInfo.State.DISCONNECTED)
			return false;
		else
			return true;
		
		
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
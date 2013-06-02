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
import AsyncTasks.ResponseCommand;
import AsyncTasks.ResponseCommand.ERROR_TYPE;
import ListAdapter.ListAdapter;
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
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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


/*Era giro isto estar dividido em duas actividades para depois poder voltar atrás*/
public class StartActivity extends SherlockFragmentActivity implements TabListener {

	SherlockFragmentActivity a =this;
	ViewPager mViewPager;
	ActionBar mActionBar;
	TabsAdapter mTabsAdapter;
	private String access_token;
	public static MenuItem menu_login;
	public static RelativeLayout profile_button;
	Bundle extras;
	GPSTracker gps = null;

	private int elapsed_time = 0;
	private float distance_ran = 0;
	private Handler handler = new Handler();

	private boolean paused = false;

	private long track_id;
	public static int user_id = 0; //default 0 porque nunca irá haver este id
	private DatabaseHandler db;

	private double latitude_was = 0.0;
	private double longitude_was = 0.0;
	private View vi;




	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		Bundle b = new Bundle();
		extras = getIntent().getExtras();

		access_token = null;
		latitude_was = 0.0;
		longitude_was = 0.0;
		elapsed_time = 0;
		distance_ran = 0;

		if(PreferenceManager.getDefaultSharedPreferences(a).getString("access_token",null) != null){

			access_token = PreferenceManager.getDefaultSharedPreferences(a).getString("access_token",null);
			getUserInfo("http://belele.herokuapp.com/profile.json?access_token="+access_token);
			Toast.makeText(StartActivity.this, "Getting user information...",
					Toast.LENGTH_LONG).show();
		}		
		
		String tab_title = "Welcome to JogginGo!";

		gps = new GPSTracker(this);

		if(gps.canGetLocation()){

			double latitude = gps.getLatitude(); // returns latitude
			double longitude = gps.getLongitude(); // returns longitude

			b.putDouble("latitude", latitude);
			b.putDouble("longitude", longitude);
		}
		else{
			gps.showSettingsAlert();
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
				MainFragment.MainFragmentAux.class, b);

		setContentView(R.layout.activity_main_menu);

		final Button begin = (Button) findViewById(R.id.button_begin);
		
		profile_button = (RelativeLayout) findViewById(R.id.button_profile_layout);
		db = new DatabaseHandler(this);


		//RETIRAR QUANDO FOR A SÉRIO!
		db.restartDB();
		//

		begin.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				Intent intent = new Intent(StartActivity.this, MainActivity.class);
				startActivity(intent);
			}
		});
	}	

	private void getUserInfo(String URL) {

		this.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		JoggingoAPI.requestURL(URL, new ResponseCommand() {

			public void onResultReceived(Object... results) {
				
				JSONObject personal_data = (JSONObject) results[0];

				Log.d("resultados", personal_data.toString());

				try {
					String name = personal_data.getString("username");
					user_id = personal_data.getInt("id");
					
					Toast.makeText(StartActivity.this, "Welcome, "+name,
							Toast.LENGTH_LONG).show();
					menu_login.setTitle(JoggingoAPI.Strings.LOGOUT);
					profile_button.setVisibility(View.VISIBLE);
					
				} catch (JSONException e) {
					
					user_id = 0;
					Toast.makeText(StartActivity.this, "Error loading data...",
							Toast.LENGTH_LONG).show();
					e.printStackTrace();
				}
			}

			@Override
			public void onError(ERROR_TYPE error) {
				
				user_id = 0;
				if(error.toString().equals(ERROR_TYPE.NETWORK))
					Toast.makeText(StartActivity.this, JoggingoAPI.Strings.SERVER_CONNECTION,
							Toast.LENGTH_LONG).show();
				else if(error.toString().equals(ERROR_TYPE.GENERAL))
					Toast.makeText(StartActivity.this, JoggingoAPI.Strings.CHECK_CONNECTION,
							Toast.LENGTH_LONG).show();
			}
		});
		// }
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

			Intent intent = new Intent(StartActivity.this,  OAuthAccessTokenActivity.class );
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
		super.onStop();
	}
	@Override
	protected void onDestroy() {
		setResult(0);
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode==3){
			finish();
		}
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
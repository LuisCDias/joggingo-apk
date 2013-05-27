package fe.up.pt.joggingo;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import oauth2.OAuthAccessTokenActivity;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Camera.Size;
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
import android.widget.Button;
import android.widget.EditText;
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

public class MainActivity extends SherlockFragmentActivity implements TabListener {

	ViewPager mViewPager;
	ActionBar mActionBar;
	TabsAdapter mTabsAdapter;
	String userToken;
	String useMode ;
	String userID;
	String userRating;
	String userName;
	String title;
	Bundle extras;
	GPSTracker gps = null;
	private Handler handler = new Handler();

	private long track_id;
	private DatabaseHandler db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);


		Bundle b = new Bundle();

		String tab_title = "Welcome to JogginGo!";

		gps = new GPSTracker(this);


		if(gps.canGetLocation()){

			double latitude = gps.getLatitude(); // returns latitude
			double longitude = gps.getLongitude(); // returns longitude
			/*Toast.makeText(getApplicationContext(), 
					"Localização - \nLat: " + latitude + "\nLong: " + longitude, 
					Toast.LENGTH_LONG).show();*/

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
		/*mTabsAdapter.addTab(mActionBar.newTab().setText("Hot Offers"),
				MainFragment_Results.MainFragment_Results_Aux.class, b);*/
		mTabsAdapter.addTab(mActionBar.newTab().setText(tab_title),
				MainFragment.MainFragmentAux.class, b);

		setContentView(R.layout.activity_main_menu);



		final Button begin = (Button) findViewById(R.id.button_begin);
		final Button end = (Button) findViewById(R.id.button_stop);
		final Button start_track = (Button) findViewById(R.id.button_start_tracking);
		final Button map = (Button) findViewById(R.id.button_map);
		final Button sync = (Button) findViewById(R.id.button_sincronize);

		final TextView coordinates_text = (TextView) findViewById(R.id.coordinates_text);
		final EditText track_name = (EditText) findViewById(R.id.track_name);
		final TextView main_text = (TextView) findViewById(R.id.joggingo_main_text);
		final View gradient_text = (View) findViewById(R.id.gradient_coordinates);

		db = new DatabaseHandler(this);

		//RETIRAR QUANDO FOR A SÉRIO!
		db.restartDB();

		begin.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				begin.setVisibility(View.GONE);
				coordinates_text.setVisibility(View.GONE);
				map.setVisibility(View.GONE);
				sync.setVisibility(View.GONE);
				main_text.setText("Enter track name!");

				Calendar c = Calendar.getInstance(); 
				Log.d("current",c.getTime()+""); 
				SimpleDateFormat df = new SimpleDateFormat("EEE, d/MMM/yyyy HH:mm");
				String formattedDate = df.format(c.getTime());

				track_name.setHint(formattedDate);
				track_name.setVisibility(View.VISIBLE);
				start_track.setVisibility(View.VISIBLE);

			}
		});



		end.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				begin.setVisibility(View.VISIBLE);
				begin.setText("Start again");
				end.setVisibility(View.GONE);
				coordinates_text.setVisibility(View.VISIBLE);
				map.setVisibility(View.VISIBLE);
				sync.setVisibility(View.VISIBLE);
				main_text.setText("Well done!");
				handler.removeCallbacks(runnable);
			}
		});

		start_track.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				// Perform action on click

				String name = track_name.getText().toString();

				end.setVisibility(View.VISIBLE);
				coordinates_text.setVisibility(View.VISIBLE);
				gradient_text.setVisibility(View.VISIBLE);
				map.setVisibility(View.GONE);
				main_text.setText("Go!");
				track_name.setVisibility(View.GONE);
				start_track.setVisibility(View.GONE);

				Log.d("Insert: ", "Inserting ..");
				if(!name.matches("")){

					Log.d("Track name: ", track_name.getText().toString());
					track_id = db.addTrack(new Track(track_name.getText().toString(),"Porto", "Portugal", 1, 1,0));
				}
				else{
					Log.d("Track name: ", track_name.getHint().toString());
					track_id = db.addTrack(new Track(track_name.getHint().toString(),"Porto", "Portugal", 1, 1,0));
				}

				handler.postDelayed(runnable, 1000);
			}
		});

		sync.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				//TODO POST /tracks/

			}
		});
	}	


	private Runnable runnable = new Runnable() {

		@Override
		public void run() {
			/* do what you need to do */
			gps = new GPSTracker(MainActivity.this);
			if(gps.canGetLocation()){
				double latitude = gps.getLatitude(); // returns latitude
				double longitude = gps.getLongitude(); // returns longitude

				TextView coordenadas_text = (TextView) findViewById(R.id.coordinates_text);
				coordenadas_text.setText(latitude + ", "+longitude);
				//Log.d("latitude", String.valueOf(latitude));
				//Log.d("longitude", String.valueOf(longitude));
				db.addPoint(new Point(Double.toString(latitude), Double.toString(longitude),1));
			}
			/* and here comes the "trick" */
			handler.postDelayed(this, 1000);
		}
	};

	public void goToMap(View v){

		gps = new GPSTracker(this);

		if(gps.canGetLocation()){

			double latitude = gps.getLatitude(); // returns latitude
			double longitude = gps.getLongitude(); // returns longitude

			Intent intent = new Intent(this, MainMapActivity.class);

			Log.d("TRACK_ID", track_id+"");
			intent.putExtra("track", track_id);
			intent.putExtra("latitude", latitude);
			intent.putExtra("longitude", longitude);

			startActivity(intent);
		}
		else{
			gps.showSettingsAlert();
		}	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_main, menu);
		MenuItem LogInItemMenu = menu.findItem(R.id.menu_LogIn);

		LogInItemMenu.setTitle(JoggingoAPI.Strings.LOGIN);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			gps = new GPSTracker(this);
			if(gps.canGetLocation()){

				double latitude = gps.getLatitude(); // returns latitude
				double longitude = gps.getLongitude(); // returns longitude
				Toast.makeText(getApplicationContext(), 
						"Localização - \nLat: " + latitude + "\nLong: " + longitude, 
						Toast.LENGTH_LONG).show();
				//				TextView coordenadas_text = (TextView) findViewById(R.id.mysixText);
				//				coordenadas_text.setText(latitude + ", "+longitude);
			}
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
package fe.up.pt.joggingo;

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

	private int track_id;
	private DatabaseHandler db;


	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);


		Bundle b = new Bundle();

		String tab_title = "JogginGo!";

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
		final Button map = (Button) findViewById(R.id.button_map);
		final TextView coordinates_text = (TextView) findViewById(R.id.coordinates_text);
		final TextView main_text = (TextView) findViewById(R.id.joggingo_main_text);
		final View gradient_text = (View) findViewById(R.id.gradient_coordinates);

		db = new DatabaseHandler(this);

		//RETIRAR QUANDO FOR A SÉRIO!
		db.restartDB();
		begin.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Perform action on click
				begin.setVisibility(View.GONE);
				end.setVisibility(View.VISIBLE);
				coordinates_text.setVisibility(View.VISIBLE);
				gradient_text.setVisibility(View.VISIBLE);
				map.setVisibility(View.GONE);
				main_text.setText("Go!");
				//map.setEnabled(false);

				Log.d("Insert: ", "Inserting ..");

				
				db.addTrack(new Track("Trilho lindo","Porto", "Portugal", 1, 1,0));
				
				/*Trocar pelo respectivo track_id*/
				track_id = 1;
				
				handler.postDelayed(runnable, 1000);

			}
		});

		end.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				begin.setVisibility(View.VISIBLE);
				begin.setText("New run");
				end.setVisibility(View.GONE);
				coordinates_text.setVisibility(View.VISIBLE);
				map.setVisibility(View.VISIBLE);
				main_text.setText("Well done!");
				handler.removeCallbacks(runnable);
				// Reading all tracks
//				Log.d("Reading: ", "Reading all tracks.."); 
//				List<Track> tracks = db.getAllTracks();      
//
//				for (Track cn : tracks) {
//					String log = "Id: "+cn.getId()+" ,Name: " + cn.getName() + " ,Country: " + cn.getCountry();
//					// Writing Contacts to log
//					Log.d("Name: ", log);
//
//
//				}
//
//				//Reading all points
//				Log.d("Reading: ", "Reading all points.."); 
//				List<Point> points= db.getAllPoint();      
//
//				for (Point p : points) {
//					String log2 = "Id: "+p.getId()+" ,Latitude: " + p.getLatitude() + " ,Longitude: " + p.getLongitude();
//					// Writing Contacts to log
//					Log.d("Name: ", log2);
//
//				}
				//db.deleteAllTracks();
				//db.deleteAllPoints();
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
				//					Toast.makeText(getApplicationContext(), 
				//									"Localização - \nLat: " + latitude + "\nLong: " + longitude, 
				//									Toast.LENGTH_LONG).show();
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
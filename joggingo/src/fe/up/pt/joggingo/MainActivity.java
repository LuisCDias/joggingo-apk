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

public class MainActivity extends SherlockFragmentActivity implements TabListener {

	ViewPager mViewPager;
	ActionBar mActionBar;
	TabsAdapter mTabsAdapter;
	Bundle extras;
	GPSTracker gps = null;
	private int elapsed_time = 0;
	private float distance_ran = 0;
	private Handler handler = new Handler();

	private boolean paused = false;

	private long track_id;
	private DatabaseHandler db;

	private double latitude_was = 0.0;
	private double longitude_was = 0.0;


	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		Bundle b = new Bundle();

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
		final Button pause = (Button) findViewById(R.id.button_pause);
		final Button end = (Button) findViewById(R.id.button_stop);
		final Button start_track = (Button) findViewById(R.id.button_start_tracking);
		final Button map = (Button) findViewById(R.id.button_map);
		final Button sync = (Button) findViewById(R.id.button_sincronize);

		final TextView coordinates_text = (TextView) findViewById(R.id.coordinates_text);
		final EditText track_name = (EditText) findViewById(R.id.track_name);
		final TextView main_text = (TextView) findViewById(R.id.joggingo_main_text);
		final View gradient_text = (View) findViewById(R.id.gradient_coordinates);

		final TextView elapsed_time_text = (TextView) findViewById(R.id.elapsed_time_text);
		final TextView distance_ran_text = (TextView) findViewById(R.id.distance_ran_text);
		final LinearLayout statistics_layout = (LinearLayout) findViewById(R.id.statistics_layout);
		final View gradient_statistics = (View) findViewById(R.id.gradient_statistics);

		final LinearLayout notifications_layout = (LinearLayout) findViewById(R.id.notifications_layout);
		final TextView notifications_text = (TextView) findViewById(R.id.notification_text);
		
		db = new DatabaseHandler(this);
		
		
		//RETIRAR QUANDO FOR A SÉRIO!
		db.restartDB();
		//
		
		
		List<Track> tracks = db.getAllTracks();
		
		if(tracks.size() == 0)
			notifications_layout.setVisibility(View.GONE);
		else{
			notifications_layout.setVisibility(View.VISIBLE);
			notifications_text.setText(notificationMessage(tracks.size()));
		}
				
		
		begin.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				elapsed_time = 0;
				distance_ran = 0;
				latitude_was = 0.0;
				longitude_was = 0.0;

				begin.setVisibility(View.GONE);
				coordinates_text.setVisibility(View.GONE);
				map.setVisibility(View.GONE);
				sync.setVisibility(View.GONE);
				main_text.setText("Enter track name!");
				statistics_layout.setVisibility(View.GONE);
				elapsed_time_text.setText("00:00:00");
				distance_ran_text.setText("0.0 km");
				gradient_statistics.setVisibility(View.GONE);

				Calendar c = Calendar.getInstance(); 
				Log.d("current",c.getTime()+""); 
				SimpleDateFormat df = new SimpleDateFormat("EEE, d/MMM/yyyy HH:mm");
				String formattedDate = df.format(c.getTime());

				track_name.setHint(formattedDate);
				track_name.setVisibility(View.VISIBLE);
				start_track.setVisibility(View.VISIBLE);

			}
		});

		pause.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				if(paused){
					paused = false;
					pause.setText("Pause");
					main_text.setText("Go!");
					handler.postDelayed(runnable, 1000);
				}
				else{
					paused = true;
					pause.setText("Go");
					main_text.setText("Paused");
				}
			}
		});


		end.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				List<Track> tracks = db.getAllTracks();
				
				if(tracks.size() == 0)
					notifications_layout.setVisibility(View.GONE);
				else{
					notifications_layout.setVisibility(View.VISIBLE);
					notifications_text.setText(notificationMessage(tracks.size()));
				}
				
				begin.setVisibility(View.VISIBLE);
				begin.setText("Start again");
				pause.setVisibility(View.GONE);
				end.setVisibility(View.GONE);

				coordinates_text.setVisibility(View.GONE);
				statistics_layout.setVisibility(View.VISIBLE);

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
				elapsed_time = 0;
				
				notifications_layout.setVisibility(View.GONE);
				pause.setVisibility(View.VISIBLE);
				end.setVisibility(View.VISIBLE);

				coordinates_text.setVisibility(View.VISIBLE);
				gradient_text.setVisibility(View.VISIBLE);

				statistics_layout.setVisibility(View.VISIBLE);
				gradient_statistics.setVisibility(View.VISIBLE);

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

				URL url;
				try {
					url = new URL("http://belele.herokuapp.com/mobile");
					new DownloadFilesTask().execute(url);
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}	

	/*TODO CRONOMETRO TEM DE SER LANÇADO NOUTRO RUNNABLE
	 * 	   PORQUE ESTE VAI SER DE 500*60 ou 1000*60
	 * 	   ENQUANTO QUE O DO CRONOMETRO É DE 1000
	 * */
	private Runnable runnable = new Runnable() {

		@Override
		public void run() {
			/* do what you need to do */
			if(!paused){
				gps = new GPSTracker(MainActivity.this);
				if(gps.canGetLocation()){
					double latitude = gps.getLatitude(); // returns latitude
					double longitude = gps.getLongitude(); // returns longitude

					TextView coordenadas_text = (TextView) findViewById(R.id.coordinates_text);
					TextView elapsed_time_text = (TextView) findViewById(R.id.elapsed_time_text);
					TextView distance_ran_text = (TextView) findViewById(R.id.distance_ran_text);


					if(latitude_was != 0.0 && longitude_was != 0.0){

						float[] results = new float[3];
						Location.distanceBetween(latitude, longitude, latitude_was, longitude_was,  results);
						Log.d("distancia entre A e B results", Arrays.toString(results));

						distance_ran += Math.abs(results[0]);
						distance_ran = (float)Math.round(distance_ran*100)/100;

						/*TODO 
						 * Para já está a ser guardado o valor em metros como se fossem KM
						 * porque senão não se viam alterações
						 * Depois mudar para distance_ran/1000
						 * */
						distance_ran_text.setText(String.valueOf(distance_ran)+" km");

					}

					elapsed_time +=1;
					String result = String.format("%02d:%02d:%02d", elapsed_time / 3600, elapsed_time / 60 % 60, elapsed_time % 60);

					coordenadas_text.setText(latitude + ", "+longitude);
					elapsed_time_text.setText(result);

					db.addPoint(new Point(Double.toString(latitude), Double.toString(longitude),track_id));

					if(latitude_was != latitude)
						latitude_was = latitude;
					if(longitude_was != longitude)
						longitude_was = longitude;

				}
				/* and here comes the "trick" */
				handler.postDelayed(this, 1000);
			}
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

	public void listTracksToSync(View v){
		
		Intent intent = new Intent(this, TracksActivity.class);
		
		startActivity(intent);
	}
	
	public String notificationMessage(int i){
			
		String str = "You have ";
		if(i==1)
			return str+ i + " track to sincronize!";
		else
			return str+ i + " tracks to sincronize!";
		
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
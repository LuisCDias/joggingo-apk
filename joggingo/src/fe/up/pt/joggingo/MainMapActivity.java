/***
  Copyright (c) 2012 CommonsWare, LLC
  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
  by applicable law or agreed to in writing, software distributed under the
  License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
  OF ANY KIND, either express or implied. See the License for the specific
  language governing permissions and limitations under the License.

  From _The Busy Coder's Guide to Android Development_
    http://commonsware.com/Android
 */

package fe.up.pt.joggingo;

import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import fe.up.pt.joggingo.R;

public class MainMapActivity extends AbstractMapActivity implements
OnNavigationListener, OnInfoWindowClickListener,
OnMyLocationChangeListener {
	private static final String STATE_NAV="nav";
	private static final int[] MAP_TYPE_NAMES= { R.string.normal,
		R.string.hybrid, R.string.satellite, R.string.terrain };
	private static final int[] MAP_TYPES= { GoogleMap.MAP_TYPE_NORMAL,
		GoogleMap.MAP_TYPE_HYBRID, GoogleMap.MAP_TYPE_SATELLITE,
		GoogleMap.MAP_TYPE_TERRAIN };
	private GoogleMap map=null;
	private DatabaseHandler db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		/*
		 * O bloco a seguir vai carregar da base de dados os points da track que foi feita
		 * 
		 * */


		db = new DatabaseHandler(this);

		Bundle extras = getIntent().getExtras();

		long track_id = extras.getLong("track");
		double latitude = extras.getDouble("latitude");
		double longitude = extras.getDouble("longitude");

		/****************************************/


		Log.d("pos_mapa", latitude+" , "+longitude);

		if (readyToGo()) {
			setContentView(R.layout.activity_main);

			SupportMapFragment mapFrag=
					(SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);

			//initListNav();

			map=mapFrag.getMap();

			if (savedInstanceState == null) {

				CameraUpdate center= CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude));
				
				CameraUpdate zoom=CameraUpdateFactory.zoomTo(15);

				map.moveCamera(center);
				map.animateCamera(zoom);
			}

			Log.d("Reading: ", "Reading all points.."); 
			
			Log.d("from track: ", track_id+"");
			List<Point> points= db.getAllPoint(track_id);      

			for (Point p : points) {
				String log2 = "Id: "+p.getId()+" ,Latitude: " + p.getLatitude() + " ,Longitude: " + p.getLongitude();
				// Writing Contacts to log
				Log.d("Name: ", log2);
				double lat = Double.parseDouble(p.getLatitude());
				double lon = Double.parseDouble(p.getLongitude());
				addMarker(map, lat, lon,
						R.string.un, R.string.united_nations);

				
			}

			map.setInfoWindowAdapter(new PopupAdapter(getLayoutInflater()));
			map.setOnInfoWindowClickListener(this);

			map.setMyLocationEnabled(true);
			map.setOnMyLocationChangeListener(this);
		}
	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		map.setMapType(MAP_TYPES[itemPosition]);

		return(true);
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);

		savedInstanceState.putInt(STATE_NAV,
				getSupportActionBar().getSelectedNavigationIndex());
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

		getSupportActionBar().setSelectedNavigationItem(savedInstanceState.getInt(STATE_NAV));
	}

	@Override
	public void onInfoWindowClick(Marker marker) {
		Toast.makeText(this, marker.getTitle(), Toast.LENGTH_LONG).show();
	}

	@Override
	public void onMyLocationChange(Location lastKnownLocation) {
		Log.d(getClass().getSimpleName(),
				String.format("%f:%f", lastKnownLocation.getLatitude(),
						lastKnownLocation.getLongitude()));
	}

	private void initListNav() {
		ArrayList<String> items=new ArrayList<String>();
		ArrayAdapter<String> nav=null;
		ActionBar bar=getSupportActionBar();

		for (int type : MAP_TYPE_NAMES) {
			items.add(getString(type));
		}

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			nav=
					new ArrayAdapter<String>(
							bar.getThemedContext(),
							android.R.layout.simple_spinner_item,
							items);
		}
		else {
			nav=
					new ArrayAdapter<String>(
							this,
							android.R.layout.simple_spinner_item,
							items);
		}

		nav.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		bar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		bar.setListNavigationCallbacks(nav, this);
	}

	private void addMarker(GoogleMap map, double lat, double lon,
			int title, int snippet) {
		map.addMarker(new MarkerOptions().position(new LatLng(lat, lon))
				.title(getString(title))
				.snippet(getString(snippet)));
	}

	@Override
	public void onStop() {
		super.onStop();

		db.close();

	}
}

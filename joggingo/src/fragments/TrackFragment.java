package fragments;

import java.util.ArrayList;
import java.util.List;

import ListAdapter.TracksAdapter;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import fe.up.pt.joggingo.DatabaseHandler;
import fe.up.pt.joggingo.Point;
import fe.up.pt.joggingo.R;
import fe.up.pt.joggingo.Track;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.SherlockListFragment;

/**
 * Demonstration of using ListFragment to show a list of items
 * from a canned array.
 */
public class TrackFragment extends SherlockFragmentActivity {
	
	
	public static Button sync_one;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);
	}

	//TODO: This ACtivity will run an AsyncTask to Load The message number and user Credentials to the view (please see asyncTask on LaunchActivity

	public static class TrackFragmentAux extends SherlockFragment {
		
		Bundle b;
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			Bundle b= super.getArguments();
			//get values from bundles
			String name = b.getString("name");
			String city = b.getString("city");
		

			View v = inflater.inflate(R.layout.track_view, container, false);

			TextView name_text = (TextView) v.findViewById(R.id.track_name);
			TextView city_text = (TextView) v.findViewById(R.id.track_city);
			sync_one = (Button) v.findViewById(R.id.button_sync);
			Button remove = (Button) v.findViewById(R.id.button_remove);
			Button goMap = (Button) v.findViewById(R.id.button_map);
			
			name_text.setText(name);
			city_text.setText(city);
			
			if(b.getString("type").equals("synched")){
				sync_one.setVisibility(View.GONE);
				remove.setVisibility(View.GONE);
				goMap.setVisibility(View.GONE);
				
			}
			
			if(b.containsKey("access_token")){
				sync_one.setText("Synchronize this track");
			}
			else{
				sync_one.setText("Login to synchronize");
			}

			return v;
		}
	}
}

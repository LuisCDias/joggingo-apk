package fragments;

import java.util.ArrayList;
import java.util.List;

import ListAdapter.TracksAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import fe.up.pt.joggingo.DatabaseHandler;
import fe.up.pt.joggingo.Point;
import fe.up.pt.joggingo.R;
import fe.up.pt.joggingo.Track;
import fe.up.pt.joggingo.TrackActivity;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.SherlockListFragment;

/**
 * Demonstration of using ListFragment to show a list of items
 * from a canned array.
 */
public class TracksFragment extends SherlockFragmentActivity {
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);
	}

	//TODO: This ACtivity will run an AsyncTask to Load The message number and user Credentials to the view (please see asyncTask on LaunchActivity

	public static class TracksFragmentAux extends SherlockListFragment {
		
		private ArrayList<String> ids;
		private ArrayList<String> names;
		private ArrayList<String> cities;
		Bundle b;
		private DatabaseHandler db;
		
		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			b= super.getArguments();
			
			db = new DatabaseHandler(this.getSherlockActivity());
			Log.d("Reading: ", "Reading all tracks.."); 
			
			List<Track> tracks = db.getAllTracks();      

			Log.d("tracks", tracks.size()+"");
			
			ids = new ArrayList<String>();
			names = new ArrayList<String>();
			cities = new ArrayList<String>();
			
			for (Track t : tracks) {
				
				String log = "Id: "+t.getId()+" ,Nome: " + t.getName();
				ids.add(String.valueOf(t.getId()));
				names.add(t.getName());
				cities.add(t.getCity());
				
				Log.d("Track: ", log);
	
			}
			// remove divider
			this.getListView().setDividerHeight(0);

			setListAdapter(new TracksAdapter(getActivity(), names, cities));
			

		}

		@Override
		public void onListItemClick(ListView l, View v, int position, long id) {
			
						
			Intent intent = new Intent(this.getSherlockActivity(), TrackActivity.class );

			intent.putExtra("name", names.get(position));
			intent.putExtra("city", cities.get(position));
			
			startActivity(intent);


		}
		
		@Override
		public void onStop() {
			super.onStop();

			db.close();

		}
	}
}

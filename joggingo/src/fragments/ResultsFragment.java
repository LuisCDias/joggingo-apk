package fragments;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import AsyncTasks.ResponseCommand;
import AsyncTasks.ResponseCommand.ERROR_TYPE;
import ListAdapter.ListAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.Toast;

import fe.up.pt.joggingo.JoggingoAPI;
import fe.up.pt.joggingo.OffersPanelActivity;
import fe.up.pt.joggingo.R;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.SherlockListFragment;


public class ResultsFragment extends SherlockFragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		// Create the list fragment and add it as our sole content.
		if (getSupportFragmentManager().findFragmentById(android.R.id.content) == null) {
			ResultsFragmentAux list = new ResultsFragmentAux();
			getSupportFragmentManager().beginTransaction()
			.add(android.R.id.content, list).commit();

		}

	}

	public static class ResultsFragmentAux extends SherlockListFragment {

		String useMode;
		String userToken;
		private ArrayList<String> titles;
		private ArrayList<String> texts;
		private ArrayList<String> images;
		private ArrayList<String> ids;
		private ArrayList<String> owners;



		private void searchIt(String URL) {
			getActivity().getWindow().setSoftInputMode(
					WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
			JoggingoAPI.requestURL(URL, new ResponseCommand() {

				public void onResultReceived(Object... results) {
					JSONArray offers = (JSONArray) results[0];
					titles = new ArrayList<String>();
					texts = new ArrayList<String>();
					images = new ArrayList<String>();
					ids = new ArrayList<String>();
					owners = new ArrayList<String>();

					int i = 0;
					while (!offers.isNull(i)) {
						try {

							JSONObject offer = offers.getJSONObject(i);
							ids.add(offer.getString("id").toString());
							titles.add(offer.getString("title").toString());
							texts.add(offer.getString("description").toString());
							images.add(offer.getString("media").toString());
							owners.add(offer.getString("owner").toString());
						} catch (JSONException e) {
							e.printStackTrace();
						}
						i++;
					}
					setListAdapter(new ListAdapter(getActivity(), titles, texts, images,owners,0));

				}

				@Override
				public void onError(ERROR_TYPE error) {
					if(error.toString().equals(ERROR_TYPE.NETWORK))
						Toast.makeText(getActivity(), JoggingoAPI.Strings.SERVER_CONNECTION,
								Toast.LENGTH_LONG).show();
					else if(error.toString().equals(ERROR_TYPE.GENERAL))
						Toast.makeText(getActivity(), JoggingoAPI.Strings.CHECK_CONNECTION,
								Toast.LENGTH_LONG).show();
				}
			});
	}

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {

			super.onActivityCreated(savedInstanceState);
			//get parent extras
			Bundle b= super.getArguments();
			String title =b.getString("title");
			useMode = b.getString("useMode");
			if(useMode.equals(JoggingoAPI.Strings.USER_MODE))
				userToken = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(JoggingoAPI.Strings.ACCESS_TOKEN, null);
			
			// remove divider
			this.getListView().setDividerHeight(0);

			//AsyncTasks to search something
			searchIt("categories/"+title+".json");


		}

		@Override
		public void onListItemClick(ListView l, View v, int position, long id) {
            Log.d("Item clicked: ",ids.get(position));
            
            String id_offer= ids.get(position);
            String title_offer = titles.get(position);
            String owner_offer = owners.get(position);
            
            Intent intent = new Intent(this.getSherlockActivity(), OffersPanelActivity.class);
            intent.putExtra("id", id_offer);
            intent.putExtra("title", title_offer);
            intent.putExtra("owner", owner_offer);
            intent.putExtra(JoggingoAPI.Strings.USE_MODE_BUNDLE, useMode);
            
            startActivity(intent);
            
    }
	}
}
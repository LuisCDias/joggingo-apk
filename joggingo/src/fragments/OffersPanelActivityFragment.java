package fragments;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import AsyncTasks.ResponseCommand;
import AsyncTasks.ResponseCommand.ERROR_TYPE;
import ListAdapter.ListAdapter;
import ListAdapter.OffersAdapter;
import android.app.Activity;
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


public class OffersPanelActivityFragment extends SherlockFragmentActivity {

	private static int adapterFlag=0;
	private static int wishFlag=0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {


		setTheme(R.style.Theme_seis); // Used for theme switching in samples
		super.onCreate(savedInstanceState);
		// Create the list fragment and add it as our sole content.
		if (getSupportFragmentManager().findFragmentById(android.R.id.content) == null) {
			OffersPanelActivityFragmentAux list = new OffersPanelActivityFragmentAux();
			getSupportFragmentManager().beginTransaction()
			.add(android.R.id.content, list).commit();
		}

	}

	public static class OffersPanelActivityFragmentAux extends SherlockListFragment {

		public String userToken;
		public String useMode ;
		
		private ArrayList<String> id_offer;
		
		private ArrayList<String> titles;
		private ArrayList<String> texts;
		private ArrayList<String> images;
		private ArrayList<String> ids;
		private ArrayList<String> owners;
		private ArrayList<String> datas;
		

		private void getWish(String URL, final String id_user) {
			
			Log.d("wishurl",URL);
			getActivity().getWindow().setSoftInputMode(
					WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
			
			JoggingoAPI.requestURL(URL, new ResponseCommand() {

				public void onResultReceived(Object... results) {
					JSONObject offers = (JSONObject) results[0];

					JSONArray wishs = new JSONArray();
					
					try {
						wishs = (JSONArray) offers.getJSONArray("wishlist");
					} catch (JSONException e) {
						e.printStackTrace();
					}

					id_offer = new ArrayList<String>();
					
					int i = 0;
					while (!wishs.isNull(i)) {
						try {

							JSONObject wish = wishs.getJSONObject(i);
							id_offer.add(wish.getString("id").toString()); //contem o id da oferta
				

						} catch (JSONException e) {
							e.printStackTrace();
						}
						i++;
					}
				
					
					searchIt("offers/"+id_user+".json");
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

		private void searchIt(String URL) {

			Log.d("search url", URL);
			getActivity().getWindow().setSoftInputMode(
					WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
			
			
			JoggingoAPI.requestURL(URL, new ResponseCommand() {
				
				
				public void onResultReceived(Object... results) {

					JSONObject offer = (JSONObject) results[0];

					titles = new ArrayList<String>();
					texts = new ArrayList<String>();
					images = new ArrayList<String>();
					ids = new ArrayList<String>();
					owners = new ArrayList<String>();
					datas = new ArrayList<String>();

					try {

						ids.add(offer.getString("id").toString());
						titles.add(offer.getString("title").toString());
						texts.add(offer.getString("description").toString());
						images.add(offer.getString("media").toString());
						owners.add(offer.getString("owner"));

						String temp_data = offer.getString("created_at");
						int i = temp_data.indexOf('T');
						String fdata = temp_data.substring(0, i);
						datas.add(fdata);
						//datas.add(offer.getString("created_data")); //VER


					} catch (JSONException e) {
						Toast.makeText(getActivity(), JoggingoAPI.Strings.SERVER_CONNECTION,
								Toast.LENGTH_LONG).show();
						getActivity().finish();
						
					}
					
					if(id_offer != null)
						for(int x=0; x<id_offer.size();x++)
							if(id_offer.get(x).equals(ids.get(0)))
								wishFlag = 1;
							else
								wishFlag=0;
							

					setListAdapter(new OffersAdapter(getActivity(), titles, texts, images, owners,datas, adapterFlag,wishFlag));

				}

				@Override
				public void onError(ERROR_TYPE error) {
					if(error.toString().equals(ERROR_TYPE.NETWORK))
						Toast.makeText(getActivity(), JoggingoAPI.Strings.SERVER_CONNECTION,
								Toast.LENGTH_LONG).show();
					else if(error.toString().equals(ERROR_TYPE.GENERAL))
						Toast.makeText(getActivity(), JoggingoAPI.Strings.CHECK_CONNECTION,
								Toast.LENGTH_LONG).show();
					getActivity().finish();
				}
			});

		}

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {

			super.onActivityCreated(savedInstanceState);
			//get parent extras
			Bundle b= super.getArguments();
			String id =b.getString("id");
			adapterFlag = b.getInt("adapterFlag");
			useMode = b.getString(JoggingoAPI.Strings.USE_MODE_BUNDLE);
			if(useMode.equals(JoggingoAPI.Strings.USER_MODE))
				userToken = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(JoggingoAPI.Strings.ACCESS_TOKEN, null);


			// remove divider
			this.getListView().setDividerHeight(0);
			
			
			//AsyncTasks to search something
			
			getWish("profile.json?access_token="+userToken,id);

		}

		@Override
		public void onListItemClick(ListView l, View v, int position, long id) {

			Intent intent = new Intent(this.getSherlockActivity(), OffersPanelActivity.class );
			intent.putExtra(JoggingoAPI.Strings.USE_MODE_BUNDLE, useMode);
			startActivity(intent);


		}
	}
}
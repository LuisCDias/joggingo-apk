package fragments;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import AsyncTasks.ResponseCommand;
import AsyncTasks.ResponseCommand.ERROR_TYPE;
import ListAdapter.ListAdapter;
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
import fe.up.pt.joggingo.R;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.SherlockListFragment;


public class SearchableActivityFragment extends SherlockFragmentActivity {

	private static String searchType;
	private static int adapterFlag  = 0; 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_browse);

		// Create the list fragment and add it as our sole content.
		if (getSupportFragmentManager().findFragmentById(android.R.id.content) == null) {
			SearchableActivityFragmentAux list = new SearchableActivityFragmentAux();
			getSupportFragmentManager().beginTransaction()
			.add(android.R.id.content, list).commit();
		}
	}

	public static class SearchableActivityFragmentAux extends SherlockListFragment {


		public String useMode;
		public String userToken;
		private ArrayList<String> names;
		private ArrayList<String> images;
		private ArrayList<String> ids_offers;
		private ArrayList<String> ids_users;
		private ArrayList<String> owners;
		private ArrayList<String> titles;
		private ArrayList<String> texts;
		// private Bundle extras ;



		private void searchForOffer(String URL) {


			getActivity().getWindow().setSoftInputMode(
					WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

			JoggingoAPI.requestURL(URL, new ResponseCommand() {

				public void onResultReceived(Object... results) {

					JSONArray offers = (JSONArray) results[0];

					titles = new ArrayList<String>();
					texts = new ArrayList<String>();
					images = new ArrayList<String>();
					ids_offers = new ArrayList<String>();
					owners = new ArrayList<String>();


					int i = 0;
					while (!offers.isNull(i)) {
						try {

							JSONObject offer = offers.getJSONObject(i);
							ids_offers.add(offer.getString("id").toString());
							titles.add(offer.getString("title").toString());
							texts.add(offer.getString("description").toString());
							images.add(offer.getString("media").toString());
							owners.add(offer.getString("owner").toString());

						} catch (JSONException e) {
							
							e.printStackTrace();
						}
						i++;
					}

					setListAdapter(new ListAdapter(getActivity(), titles, texts, images, owners, adapterFlag));

				}

				@Override
				public void onError(ERROR_TYPE error) {
					Toast.makeText(getActivity(), error.toString(),
							Toast.LENGTH_LONG).show();
				}
			});
			// }
		}

		private void multiSearch(String URL) {

			Log.d("ASDASDASDASDAs", URL);
			getActivity().getWindow().setSoftInputMode(
					WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

			JoggingoAPI.requestURL(URL, new ResponseCommand() {

				public void onResultReceived(Object... results) {

					JSONArray offers = (JSONArray) results[0];

					titles = new ArrayList<String>();
					texts = new ArrayList<String>();
					images = new ArrayList<String>();
					ids_offers = new ArrayList<String>();
					owners = new ArrayList<String>();


					int i = 0;
					while (!offers.isNull(i)) {
						try {

							JSONObject offer = offers.getJSONObject(i);
							ids_offers.add(offer.getString("id").toString());
							titles.add(offer.getString("title").toString());
							texts.add(offer.getString("description").toString());
							images.add(offer.getString("media").toString());
							owners.add(offer.getString("owner").toString());

						} catch (JSONException e) {
							
							e.printStackTrace();
						}
						i++;
					}

					setListAdapter(new ListAdapter(getActivity(), titles, texts, images, owners,adapterFlag));

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
			// }
		}
		@Override
		public void onActivityCreated(Bundle savedInstanceState) {

			super.onActivityCreated(savedInstanceState);
			// remove divider
			this.getListView().setDividerHeight(0);

			//get parent extras
			Bundle b= super.getArguments();
			useMode = b.getString(JoggingoAPI.Strings.USE_MODE_BUNDLE);
			if(useMode.equals(JoggingoAPI.Strings.USER_MODE))
				userToken = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(JoggingoAPI.Strings.ACCESS_TOKEN, null);
			
			
			adapterFlag = b.getInt("adapterFlag");
			searchType = b.getString("type");



			if(searchType.equals("offer")){
				String query =b.getString("searchQuery");
				String title = b.getString("title");
				query = query.trim();
				// if(checkIfMoreThanOneKeyword(query))
				query= query.replace(' ', '+');
				//AsyncTasks to search something
				if(title == null)//search without categorie filter
					searchForOffer("offers/search.json?name="+ query);
				else//search with categorie filter 
				{
					title=title.trim();
					searchForOffer("offers/search.json?name=" +query + "&cat="+ title);

				}
			}
			else
				if(searchType.equals("multi_search")){
					//multiSearchs
					String URL_to_search = "offers/search.json?";
					String query_from_bundle =b.getString("query_from_Bundle").trim();
					String title =b.getString("title");
					String user =b.getString("user").trim();					
					String rating =b.getString("rating");

					Log.d("query_from_Bundle", query_from_bundle);
					Log.d("title", title);
					Log.d("user", user);
					Log.d("rating", rating);


					if(!(query_from_bundle.equals(""))){
						URL_to_search += "name=";
						URL_to_search += query_from_bundle;
						URL_to_search+="&";
					}
					if(!(title.equals("all categories"))){
						URL_to_search += "cat=";
						URL_to_search += title;
						URL_to_search+="&";
					}
					if(!(user.equals(""))){
						URL_to_search += "username=";
						URL_to_search += user;
						URL_to_search+="&";
					}
					if(rating.equals("0")){
						URL_to_search += "rating=";
						URL_to_search += rating;
						URL_to_search+="&";
					}

					if(URL_to_search.endsWith("&")){
						URL_to_search= URL_to_search.substring(0, URL_to_search.length()-1);
					}

					multiSearch(URL_to_search);
				}

		}

		@Override
		public void onListItemClick(ListView l, View v, int position, long id) {

			String id_offer= ids_offers.get(position);
			String title_offer = titles.get(position);
			String owner_offer = owners.get(position);
			
			

			/*Intent intent = new Intent(this.getSherlockActivity(), OffersPanelActivity.class );
			intent.putExtra("id", id_offer);
			intent.putExtra("title", title_offer);
			intent.putExtra("owner", owner_offer);
			intent.putExtra("adapterFlag", adapterFlag);
			intent.putExtra(JoggingoAPI.Strings.USE_MODE_BUNDLE, useMode);
			startActivity(intent);*/

		}


	}
}
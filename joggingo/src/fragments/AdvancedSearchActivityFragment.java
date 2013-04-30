package fragments;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import AsyncTasks.ResponseCommand;
import AsyncTasks.ResponseCommand.ERROR_TYPE;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.SherlockListFragment;

import fe.up.pt.joggingo.JoggingoAPI;
import fe.up.pt.joggingo.MultiSearchableActivity;
import fe.up.pt.joggingo.R;



public class AdvancedSearchActivityFragment extends SherlockFragmentActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		
		if (getSupportFragmentManager().findFragmentById(android.R.id.content) == null) {
			AdvancedSearchActivityFragmentAux list = new AdvancedSearchActivityFragmentAux();
			getSupportFragmentManager().beginTransaction()
			.add(android.R.id.content, list).commit();
		}
	}


	public static class AdvancedSearchActivityFragmentAux extends SherlockListFragment   {
		public String useMode;
		public String userToken;
		private ArrayList<String> names;
		private ArrayList<String> names_to_populate;
		private ArrayList<String> descs;
		private ArrayList<String> slugs;
		private ArrayList<String> ids;


		private void searchIt(String URL, final Activity a) {

			final LayoutInflater inflater = (LayoutInflater)a.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


			getActivity().getWindow().setSoftInputMode(
					WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

			JoggingoAPI.requestURL(URL, new ResponseCommand() {

				public void onResultReceived(Object... results) {

					JSONArray offers = (JSONArray) results[0];

					names = new ArrayList<String>();
					descs = new ArrayList<String>();
					slugs = new ArrayList<String>();
					ids = new ArrayList<String>();

					int i = 0;
					while (!offers.isNull(i)) {
						try {

							JSONObject offer = offers.getJSONObject(i);
							ids.add(offer.getString("id").toString());
							names.add(offer.getString("name").toString());
							descs.add(offer.getString("description").toString());
							slugs.add(offer.getString("slug").toString());

						} catch (JSONException e) {
							e.printStackTrace();
						}
						i++;
					}

					names_to_populate=slugs;
					names_to_populate.add(0,"all categories");

					View v =  inflater.inflate(R.layout.advanced_search, null);

					final EditText userNameText = (EditText) v.findViewById(R.id.advancedSearchUserText);

					final EditText offerNameText = (EditText) v.findViewById(R.id.advancedSearchOfferText);

					final Spinner s = (Spinner) v.findViewById(R.id.spinner1);
					ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(a.getBaseContext(), android.R.layout.simple_spinner_item, names_to_populate);
					spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					s.setAdapter(spinnerArrayAdapter);

					final Spinner s2 = (Spinner) v.findViewById(R.id.spinner2);
					ArrayAdapter<String> spinnerArrayAdapter2 = new ArrayAdapter<String>(a.getBaseContext(), android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.ratings_array));
					spinnerArrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					s2.setAdapter(spinnerArrayAdapter2);

					Button bt1 = (Button) v.findViewById(R.id.advancedSearchBtn);
					bt1.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							String userToSearch;
							if(userNameText.getText().toString().equals("")){
								userToSearch = "";
							}
							else
								userToSearch = userNameText.getText().toString();

							String queryToSearch;
							if(offerNameText.getText().toString().equals("")){
								queryToSearch = "";
							}
							else
								queryToSearch = offerNameText.getText().toString();

							String ratingToSearch = s2.getSelectedItem().toString();
							String categoryToSearch = s.getSelectedItem().toString();
							fireSearch(userToSearch, ratingToSearch, categoryToSearch, queryToSearch);

						}
					});

					a.setContentView(v);
					//setListAdapter(new AdvancedSearchAdapter(getActivity(), names));


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

		public void fireSearch(String u, String r, String c, String q){

			Intent intent = new Intent(this.getSherlockActivity(), MultiSearchableActivity.class );
			intent.putExtra("type", "multi_search");
			intent.putExtra("title", c);
			intent.putExtra("query_from_Bundle", q);
			intent.putExtra("user", u);
			intent.putExtra("rating", r);
			intent.putExtra("useMode", useMode);  
			startActivity(intent);

		}





		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			//get parent extras
			Bundle b= super.getArguments();
			
			useMode = b.getString(JoggingoAPI.Strings.USE_MODE_BUNDLE);
			if(useMode.equals(JoggingoAPI.Strings.USER_MODE))
				userToken = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(JoggingoAPI.Strings.ACCESS_TOKEN, null);
			// remove divider
			this.getListView().setDividerHeight(0);
			//AsyncTasks to search something
			Activity a= super.getActivity();
			searchIt("categories.json", a);



		}




	}
}



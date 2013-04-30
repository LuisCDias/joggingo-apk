package fe.up.pt.joggingo;

import android.app.SearchManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;

import fe.up.pt.joggingo.R;
import fragments.SearchableActivityFragment;

public class SearchableActivity extends SherlockFragmentActivity {

	ViewPager mViewPager;
	ActionBar mActionBar;
	TabsAdapter mTabsAdapter;
	public String userToken;
	public String useMode ;


	@Override
	public void onCreate(Bundle savedInstanceState) {  
		super.onCreate(savedInstanceState);

		mViewPager = new ViewPager(this);
		mViewPager.setId(R.id.pager);
		mViewPager.setBackgroundColor(Color.WHITE);
		setContentView(mViewPager);

		mActionBar = getSupportActionBar();
		mActionBar.setDisplayShowTitleEnabled(false);
		mActionBar.setHomeButtonEnabled(true);
		mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);



		String searchQuery = null;
		final Intent queryIntent = getIntent();
		final String queryAction = queryIntent.getAction();

		Bundle b = queryIntent.getBundleExtra(SearchManager.APP_DATA);
		String type = b.getString("type");	//this is the search Type
		useMode = b.getString(JoggingoAPI.Strings.USE_MODE_BUNDLE);
		if(useMode.equals(JoggingoAPI.Strings.USER_MODE))
			userToken = PreferenceManager.getDefaultSharedPreferences(this).getString(JoggingoAPI.Strings.ACCESS_TOKEN, null);

		if(!(type.equals("multi_search"))){
			if (Intent.ACTION_SEARCH.equals(queryAction)) {
				searchQuery=  doSearchWithIntent(queryIntent);
			}
		}




		String title = b.getString("title");  // this is the category name
		String query_from_Bundle = b.getString("query_from_Bundle");
		String userName = b.getString("user"); //this is the user name to search
		String rating = b.getString("rating"); //this is the minimum rating to look for
		Bundle b_query = new Bundle();
		b_query.putString("searchQuery", searchQuery); 

		b_query.putString("type", type);
		b_query.putString("title", title);   
		b_query.putString(JoggingoAPI.Strings.USE_MODE_BUNDLE, useMode); 
		b_query.putString(JoggingoAPI.Strings.USER_TOKEN_BUNDLE, userToken); 




		String actionBarTitle = "Results For ";
		actionBarTitle += searchQuery;
		if(title!= null){
			actionBarTitle += " In Category " + title;

		}


		mTabsAdapter = new TabsAdapter(this, mViewPager);
		mTabsAdapter.addTab(mActionBar.newTab().setText(actionBarTitle),
				SearchableActivityFragment.SearchableActivityFragmentAux.class, b_query);

	}

	/*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
            getMenuInflater().inflate(R.menu.activity_main_menu, menu);
            return true;
    }*/

	private String doSearchWithIntent(final Intent queryIntent) {
		final String queryString = queryIntent
				.getStringExtra(SearchManager.QUERY);
		return queryString;
	}


}
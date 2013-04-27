package fe.up.pt.partner;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;

import fe.up.pt.partner.R;
import fragments.SearchableActivityFragment;

public class MultiSearchableActivity extends SherlockFragmentActivity {


	ViewPager mViewPager;
	ActionBar mActionBar;
	TabsAdapter mTabsAdapter;
	String useMode;
	String userToken;
	static Activity act;


	@Override
	public void onCreate(Bundle savedInstanceState) {

		
		setTheme(R.style.Theme_seis);   
		super.onCreate(savedInstanceState);
		act = this;

		Bundle b= getIntent().getExtras();

		mViewPager = new ViewPager(this);
		mViewPager.setId(R.id.pager);
		mViewPager.setBackgroundColor(Color.WHITE);
		setContentView(mViewPager);

		mActionBar = getSupportActionBar();
		mActionBar.setDisplayShowTitleEnabled(false);
		mActionBar.setHomeButtonEnabled(true);
		mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);



		String type = b.getString("type");	//this is the search Type

		String title = b.getString("title");  // this is the category name
		String query_from_Bundle = b.getString("query_from_Bundle"); //query passed by bundle           
		String userName = b.getString("user"); //this is the user name to search
		String rating = b.getString("rating"); //this is the minimum rating to look for
		String token = b.getString("token"); //this is to identify that we want the porfolio
		useMode = b.getString(JoggingoAPI.Strings.USE_MODE_BUNDLE);
		if(useMode.equals(JoggingoAPI.Strings.USER_MODE))
			userToken = PreferenceManager.getDefaultSharedPreferences(this).getString(JoggingoAPI.Strings.ACCESS_TOKEN, null);
		
		int adapterFlag = b.getInt("adapterFlag");
		Bundle b_query = new Bundle();
		b_query.putString("searchQuery", query_from_Bundle); 
		b_query.putString("query_from_Bundle", query_from_Bundle); 
		b_query.putString("type", type);
		b_query.putString("title", title);
		b_query.putString("user", userName);
		b_query.putString("rating", rating);
		b_query.putString("token", token);
		b_query.putString(JoggingoAPI.Strings.USE_MODE_BUNDLE, useMode);
		b_query.putString(JoggingoAPI.Strings.USER_TOKEN_BUNDLE, userToken);
		b_query.putInt("adapterFlag", adapterFlag);



		String actionBarTitle =null;

		/*parse ao tipo de utiliza��o do search*/
		if(token != null)
			actionBarTitle="Portfolio of "+userName;
		else
		{
			actionBarTitle = "Results For ";
			actionBarTitle += query_from_Bundle;
			if(title!= null)
				actionBarTitle += " In Category " + title;
		}


		mTabsAdapter = new TabsAdapter(this, mViewPager);
		mTabsAdapter.addTab(mActionBar.newTab().setText(actionBarTitle),
				SearchableActivityFragment.SearchableActivityFragmentAux.class, b_query);

	}




}
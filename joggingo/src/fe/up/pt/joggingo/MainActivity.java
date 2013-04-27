package fe.up.pt.joggingo;

import oauth2.OAuthAccessTokenActivity;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Camera.Size;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
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
	String userToken;
	String useMode ;
	String userID;
	String userRating;
	String userName;
	String title;
	Bundle extras;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		setTheme(R.style.Theme_seis);
		super.onCreate(savedInstanceState);
		
		Bundle b = new Bundle();
		
		b.putString("book", "Book");
		b.putString("partner", "Partner");
		

		mViewPager = new ViewPager(this);
		mViewPager.setId(R.id.pager);
		mViewPager.setBackgroundColor(Color.WHITE);
		setContentView(mViewPager);

		mActionBar = getSupportActionBar();
		mActionBar.setDisplayShowTitleEnabled(false);
		mActionBar.setHomeButtonEnabled(true);
		mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);


		mTabsAdapter = new TabsAdapter(this, mViewPager);
		/*mTabsAdapter.addTab(mActionBar.newTab().setText("Hot Offers"),
				MainFragment_Results.MainFragment_Results_Aux.class, b);*/
		mTabsAdapter.addTab(mActionBar.newTab().setText("Home"),
				MainFragment.MainFragmentAux.class, b);


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
			// TODO handle clicking the app icon/logo
			return false;
		case R.id.menu_search:
			//startSearch(title, false,null,false);
			onSearchRequested();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}


	@Override
	public boolean onSearchRequested() {
		SearchManager searchManager = (SearchManager)getSystemService(Context.SEARCH_SERVICE);

		if(searchManager!=null)
		{
			// start the search with the appropriate searchable activity
			// so we get the correct search hint in the search dialog
			Bundle b = new Bundle();
			b.putString("type", "offer" );
			b.putString("title", title);
			b.putString(JoggingoAPI.Strings.USE_MODE_BUNDLE, useMode);
			searchManager.startSearch(null, false,new ComponentName(this, SearchableActivity.class), b, false);
			return true;
		}
		return false;
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


	public void goWeb(View v){
		//opens six webpage
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(JoggingoAPI.getURL()));
		startActivity(browserIntent);
	}

	public void openOfferPage(View v){
		Intent intent = new Intent(MainActivity.this, OffersPanelActivity.class);
		startActivity(intent);
	}

	
	//--------------------------------------------------------------------------------------
	public void getAdvancedSearch(View v){

		Intent intent = new Intent(MainActivity.this, AdvancedSearchActivity.class);
		intent.putExtra(JoggingoAPI.Strings.USE_MODE_BUNDLE, useMode);
		startActivity(intent);  
	}


	public void onTabSelected(Tab tab, FragmentTransaction ft) {

	}

	public void onTabUnselected(Tab tab, FragmentTransaction ft) {


	}

	public void onTabReselected(Tab tab, FragmentTransaction ft) {


	}
}
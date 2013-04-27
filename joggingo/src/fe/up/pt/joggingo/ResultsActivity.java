package fe.up.pt.joggingo;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import fe.up.pt.joggingo.R;
import fragments.ResultsFragment;

public class ResultsActivity extends SherlockFragmentActivity {

	String type;
	String useMode;
	ViewPager mViewPager;
	ActionBar mActionBar;
	TabsAdapter mTabsAdapter;
	String title;
	Bundle extras; //bundle that comes when this activity is lanched

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		extras= getIntent().getExtras();
		if(extras!=null){
			title = extras.getString("title");
		}
		useMode = extras.getString(JoggingoAPI.Strings.USE_MODE_BUNDLE);
		setTheme(R.style.Theme_seis);
		super.onCreate(savedInstanceState);

		mViewPager = new ViewPager(this);
		mViewPager.setId(R.id.pager);
		mViewPager.setBackgroundColor(Color.WHITE);
		setContentView(mViewPager);

		mActionBar = getSupportActionBar();
		mActionBar.setDisplayShowTitleEnabled(false);
		mActionBar.setHomeButtonEnabled(true);
		mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		mTabsAdapter = new TabsAdapter(this, mViewPager);
	
		mTabsAdapter.addTab(mActionBar.newTab().setText(title),
				ResultsFragment.ResultsFragmentAux.class, extras);
		
		
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		//TODO: Create new menu only with search
            getSupportMenuInflater().inflate(R.menu.activity_main, menu);
            return super.onCreateOptionsMenu(menu);
    }
	
	 @Override
     public boolean onOptionsItemSelected(MenuItem item) {
             switch (item.getItemId()) {
             case android.R.id.home:
                    
                     return false;
             case R.id.menu_search:
            	 
            	 onSearchRequested();
                     
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
	             type = "offer";
	             b.putString("type", type);
	             b.putString("title", title);
	             b.putString(JoggingoAPI.Strings.USE_MODE_BUNDLE, useMode); 
	             searchManager.startSearch(null, false,new ComponentName(this, SearchableActivity.class), b, false);
	     return true;
	 }
	 return false;
	 }
	  
	/*public void openOfferPage(View v){
		Intent intent = new Intent(ResultsActivity.this, OffersPanelActivity.class);
		startActivity(intent);
	}*/
	

	
}
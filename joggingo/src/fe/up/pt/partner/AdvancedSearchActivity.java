package fe.up.pt.partner;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;

import fe.up.pt.partner.R;
import fragments.AdvancedSearchActivityFragment;

public class AdvancedSearchActivity extends SherlockFragmentActivity {

	
	ViewPager mViewPager;
	ActionBar mActionBar;
	TabsAdapter mTabsAdapter;
	Bundle extras;
 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		extras= getIntent().getExtras();
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
		mTabsAdapter.addTab(mActionBar.newTab().setText("Advanced Search"),
				AdvancedSearchActivityFragment.AdvancedSearchActivityFragmentAux.class, extras);
		
		
	}	
	

	
}
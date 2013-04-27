package fe.up.pt.joggingo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.ActionBar.TabListener;
import com.actionbarsherlock.app.SherlockFragmentActivity;

import fe.up.pt.joggingo.R;
import fragments.OffersPanelActivityFragment;

public class OffersPanelActivity extends SherlockFragmentActivity implements TabListener {

	ViewPager mViewPager;
	ActionBar mActionBar;
	TabsAdapter mTabsAdapter;
	Bundle extras ;
	public String userToken;
	public String useMode ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		String title = null;
		extras= getIntent().getExtras();
		if(extras!=null){
			title = extras.getString("title");

			useMode = extras.getString(JoggingoAPI.Strings.USE_MODE_BUNDLE);
			if(useMode.equals(JoggingoAPI.Strings.USER_MODE))
				userToken = PreferenceManager.getDefaultSharedPreferences(this).getString(JoggingoAPI.Strings.ACCESS_TOKEN, null);
		}
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
				OffersPanelActivityFragment.OffersPanelActivityFragmentAux.class,extras);

	}	



	public void onTabSelected(Tab tab, FragmentTransaction ft) {


	}

	public void onTabUnselected(Tab tab, FragmentTransaction ft) {


	}

	public void onTabReselected(Tab tab, FragmentTransaction ft) {


	}
}
package fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import fe.up.pt.partner.JoggingoAPI;
import fe.up.pt.partner.R;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;

/**
 * Demonstration of using ListFragment to show a list of items
 * from a canned array.
 */
public class MainFragment extends SherlockFragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_seis); //Used for theme switching in samples
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);

	}

	//TODO: This ACtivity will run an AsyncTask to Load The message number and user Credentials to the view (please see asyncTask on LaunchActivity

	public static class MainFragmentAux extends SherlockFragment {


		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			Bundle b= super.getArguments();
			//get values from bundles

			String book= b.getString("book");

			String partner = b.getString("partner");

			View v = inflater.inflate(R.layout.activity_main_menu, container, false);

			TextView userNameTextView = (TextView) v.findViewById(R.id.userNameOnMainMenu);
			userNameTextView.setText(book+"-"+partner);

			return v;
		}
	}
}

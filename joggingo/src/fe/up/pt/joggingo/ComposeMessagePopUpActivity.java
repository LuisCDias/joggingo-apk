package fe.up.pt.joggingo;

import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import fe.up.pt.partner.R;
import fe.up.pt.partner.PartnerAPI;



public class ComposeMessagePopUpActivity extends Activity {

	public String useMode;
	public String userToken;

	public String type;
	public String title;
	public String author;
	public String rating;
	
	public String popUpFor; //string that controls action after yes
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//setTheme(android.R.style.Theme_Dialog); //Used for theme switching in samples
		super.onCreate(savedInstanceState);
		Bundle b= getIntent().getExtras();
		
		if(b != null){
			
			title = b.getString("title");
			author = b.getString("author");
			rating = b.getString("rating");
		}
		
		//useMode = b.getString(PartnerAPI.Strings.USE_MODE_BUNDLE);
		//if(useMode.equals(PartnerAPI.Strings.USER_MODE))
		//	userToken = PreferenceManager.getDefaultSharedPreferences(this).getString(PartnerAPI.Strings.ACCESS_TOKEN, null);
		//popUpFor= b.getString(PartnerAPI.Strings.CONFIRMATION_POPUP_BUNDLE);
		//user_from_id = b.getString(SixAPI.Strings.USER_ID_BUNDLE);

		buildPane(this);

	}

	public void buildPane(final Activity a){
		
		final LayoutInflater inflater = (LayoutInflater)a.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View vi =  inflater.inflate(R.layout.activity_compose_message_pop_up, null);

		EditText tweet_text = (EditText) vi.findViewById(R.id.tweet_text);
		
		final String tweet_message = "Great book: "+title+" by "+author+"! "+
							   "Average rating of "+rating+"* on BookPartner!";
		tweet_text.setHint(tweet_message);
		
		Button okBtn = (Button) vi.findViewById(R.id.composePopUpYesBtn);
		Button cancelBtn = (Button) vi.findViewById(R.id.composePopUpNoBtn);
		
		okBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				Intent intent = new Intent(v.getContext(), TwitterActivity.class );

				//v =  inflater.inflate(R.layout.activity_compose_message_pop_up, null);
				EditText tweet_text = (EditText) vi.findViewById(R.id.tweet_text);
				String texto = tweet_text.getText().toString();
				
				if(texto.matches("")){
					intent.putExtra("texto", tweet_message);
				}
				else{
					String info = "["+ title + " by "+author+" ("+rating+")]";
					String novo_texto = texto+"\n"+ info;
					intent.putExtra("texto", novo_texto);
				}
				
				v.getContext().startActivity(intent);
				a.finish();
			}
		});

		cancelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				a.finish();				
				
			}
		});
		
		
		a.setContentView(vi);

	}
	
	



}



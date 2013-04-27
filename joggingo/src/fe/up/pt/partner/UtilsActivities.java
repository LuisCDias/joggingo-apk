package fe.up.pt.partner;

import java.util.Calendar;
import java.util.Date;

import oauth2.OAuthAccessTokenActivity;
import android.app.Activity;
import android.preference.PreferenceManager;

public class UtilsActivities {

	public static void checkToken(Activity a){
		// Handles the token according to dates
					if(PreferenceManager.getDefaultSharedPreferences(a).getString("access_token", null) != null){
						Date d = new Date (PreferenceManager.getDefaultSharedPreferences(a).getLong(JoggingoAPI.Strings.EXPIRES_IN,0));
						Date d2 = Calendar.getInstance().getTime();

						if (d2.after(d)){
							//REFRESH THE USER TOKEN
							OAuthAccessTokenActivity oAuth = new OAuthAccessTokenActivity();
							oAuth.refreshToken(PreferenceManager.getDefaultSharedPreferences(a));
							//TOKEN REFRESHED
						}
					}
					//handling done
	}
}

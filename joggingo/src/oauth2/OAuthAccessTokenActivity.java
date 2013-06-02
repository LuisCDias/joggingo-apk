package oauth2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import AsyncTasks.ResponseCommand;
import AsyncTasks.ResponseCommand.ERROR_TYPE;
import ListAdapter.ListAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;
import fe.up.pt.joggingo.JoggingoAPI;
import fe.up.pt.joggingo.MainActivity;
import fe.up.pt.joggingo.R;
/**
 * Execute the OAuthRequestTokenTask to retrieve the request, and authorize the
 * request. After the request is authorized by the user, the callback URL will
 * be intercepted here.
 * 
 */
@SuppressLint("SetJavaScriptEnabled")
public class OAuthAccessTokenActivity extends Activity {

	private SharedPreferences prefs;
	private boolean logout = false;
	Activity a = this;

	@SuppressLint("NewApi")
	@TargetApi(9)
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
		//not the best practices but hey, short on time!
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy); 
		Bundle b = getIntent().getExtras();
		
		/*if(b != null && b.containsKey("logout")){
			logout = true;
			clearCredentials();
		}*/
	}

	public SharedPreferences getPrefs() {
		return prefs;
	}

	public void setPrefs(SharedPreferences prefs) {
		this.prefs = prefs;
	}

	@Override
	protected void onStop() {
		setResult(3);
		super.onStop();
	}
	@Override
	protected void onPause() {
		setResult(3);
		super.onPause();
	}
	@Override
	protected void onDestroy() {
		setResult(3);
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		
		super.onResume();
		WebView webview = new WebView(this);
		webview.getSettings().setJavaScriptEnabled(true);
		webview.getSettings().setSavePassword(false);
		webview.setVisibility(View.VISIBLE);		


		setContentView(webview);

		/* WebViewClient must be set BEFORE calling loadUrl! */
		webview.setWebViewClient(new WebViewClient() {

			@Override
			public void onPageFinished(WebView view, String url) {
				Log.d("url", url);
				Log.d("redirect_url", OAuth2ClientCredentials.REDIRECT_URI);
				
				
				if (url.startsWith(OAuth2ClientCredentials.REDIRECT_URI)) {

					if (url.indexOf("code=") != -1) {

						String code = url.substring(OAuth2ClientCredentials.SCOPE.length() + 6, url.indexOf('&'));
						Log.d("url", url);
						Log.d("code", code);
						try {						
							// Create Post Header
							HttpPost httppost = new HttpPost(OAuth2ClientCredentials.SCOPE+"oauth/token");
							
							// Add your data
							List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

							nameValuePairs.add(new BasicNameValuePair("client_id", OAuth2ClientCredentials.CLIENT_ID));
							nameValuePairs.add(new BasicNameValuePair("client_secret", OAuth2ClientCredentials.CLIENT_SECRET));
							nameValuePairs.add(new BasicNameValuePair("code", code));
							//nameValuePairs.add(new BasicNameValuePair("grant_type", "authorization_code"));
							httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
							
							Log.d("Passou", "aqui");
							Log.d("POST", httppost.getURI()+"");
							// Execute HTTP Post Request
							ResponseHandler<String> responseHandler=new BasicResponseHandler();
							JSONObject response = new JSONObject(new DefaultHttpClient().execute(httppost, responseHandler));
							
							// Store the information in the shared preferences

							Editor editor = prefs.edit();
							editor.putString("access_token",response.getString("access_token"));

							Log.d("access_token_caralho",response.getString("access_token") );
							//Date d = Calendar.getInstance().getTime();
							//d.setSeconds(response.getInt("expires_in")); //remove a minute for flapping

							//editor.putLong("expires_in", d.getTime());
							//editor.putString("refresh_token",response.getString("refresh_token"));
							editor.commit();
							Log.d("fez","commit");
						} catch (ClientProtocolException e) {
							e.printStackTrace();
							Log.i("ClientProtocolException: ", e.toString());
						} catch (IOException e) {
							e.printStackTrace();
							Log.i("IOException: ", e.toString());
						} catch (JSONException e) {
							e.printStackTrace();
							Log.i("JSONException: ", e.toString());
						}
						view.setVisibility(View.INVISIBLE);
						
						MainActivity.sync.setText("Synchronize account");
						
						Toast.makeText(OAuthAccessTokenActivity.this, "Logging in...",
								Toast.LENGTH_SHORT).show();
						getUserInfo(OAuth2ClientCredentials.SCOPE+"profile.json?access_token="+prefs.getString("access_token", null));
						
						finish();
					}

				}
			}

		});
		String authorizationUrl = 
				new String(OAuth2ClientCredentials.SCOPE+"oauth/new?client_id="+OAuth2ClientCredentials.CLIENT_ID+
						"&client_secret="+OAuth2ClientCredentials.CLIENT_SECRET+"&redirect_uri="+OAuth2ClientCredentials.REDIRECT_URI);
		// authorizationUrl
		Log.i("authorizationUrl: ", authorizationUrl);
		if (this.prefs.getString("access_token", null) == null)
			webview.loadUrl(authorizationUrl);
		else {
			Log.i("access_token: ", this.prefs.getString("access_token", null));
			//Log.i("expires_in: ", new Date(this.prefs.getLong("expires_in", 0)).toString() );
			//Log.i("refresh_token: ", this.prefs.getString("refresh_token", null));
			/*getUserDetails(OAuth2ClientCredentials.SCOPE+"profile.json?access_token="+this.prefs.getString("access_token", null));
			finish();*/
			clearCredentials();
			Toast.makeText(OAuthAccessTokenActivity.this, "Successfully logged out.",
					Toast.LENGTH_LONG).show();
		}
	}

	private void getUserInfo(String URL) {

		this.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		JoggingoAPI.requestURL(URL, new ResponseCommand() {

			public void onResultReceived(Object... results) {
				
				JSONObject personal_data = (JSONObject) results[0];

				Log.d("resultados", personal_data.toString());

				try {
					String name = personal_data.getString("username");
					MainActivity.user_id = personal_data.getInt("id");

					Toast.makeText(OAuthAccessTokenActivity.this, "Welcome, "+name,
							Toast.LENGTH_LONG).show();
					MainActivity.menu_login.setTitle(JoggingoAPI.Strings.LOGOUT);
					MainActivity.sync.setEnabled(true);
					MainActivity.profile_button.setVisibility(View.VISIBLE);
					
				} catch (JSONException e) {
					
					MainActivity.user_id = 0;
					Toast.makeText(OAuthAccessTokenActivity.this, "Error logging in...",
							Toast.LENGTH_LONG).show();
					e.printStackTrace();
				}
			}

			@Override
			public void onError(ERROR_TYPE error) {
				
				MainActivity.user_id = 0;
				
			}
		});
		// }
	}
	
	public void clearCredentials() {
		
		Editor editor = prefs.edit();
		MainActivity.user_id = 0;
		MainActivity.sync.setText("Login to synchronize");
		
		editor.remove("access_token");
		editor.remove("");
		editor.commit();
		MainActivity.menu_login.setTitle(JoggingoAPI.Strings.LOGIN);
		MainActivity.profile_button.setVisibility(View.GONE);
		
		//android.webkit.CookieManager.getInstance().removeAllCookie(); //this is used to clear the cookies of the webview
		finish();
	}



	// example call
	//new OAuthAccessTokenActivity().refreshToken(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
	@SuppressLint({ "NewApi", "NewApi" })
	@TargetApi(9)
	public void refreshToken(SharedPreferences sharedPreferences) {

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy); 
		try {						
			Editor editor = sharedPreferences.edit();
			editor.remove("access_token");
			editor.remove("expires_in");

			// Create Post Header
			HttpPost httppost = new HttpPost(OAuth2ClientCredentials.SCOPE+"oauth2/token");

			// Add your data
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("client_id", OAuth2ClientCredentials.CLIENT_ID));
			nameValuePairs.add(new BasicNameValuePair("client_secret", OAuth2ClientCredentials.CLIENT_SECRET));
			nameValuePairs.add(new BasicNameValuePair("refresh_token", sharedPreferences.getString("refresh_token", null)));
			nameValuePairs.add(new BasicNameValuePair("grant_type", "refresh_token"));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			// Execute HTTP Post Request
			ResponseHandler<String> responseHandler=new BasicResponseHandler();	        
			JSONObject response = new JSONObject(new DefaultHttpClient().execute(httppost, responseHandler));

			// Store the information in the shared preferences
			Date d = Calendar.getInstance().getTime();
			editor.putString("access_token",response.getString("access_token"));
			d.setSeconds(response.getInt("expires_in")-60);
			editor.putLong("expires_in", d.getTime());
			editor.commit();


		} catch (ClientProtocolException e) {
			e.printStackTrace();
			Log.i("ClientProtocolException: ", e.toString());
		} catch (IOException e) {
			e.printStackTrace();
			Log.i("IOException: ", e.toString());
		} catch (JSONException e) {
			e.printStackTrace();
			Log.i("JSONException: ", e.toString());
		}
	}


}

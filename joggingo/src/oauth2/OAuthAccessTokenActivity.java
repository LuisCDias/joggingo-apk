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
		//clearCredentials();
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
						//Intent i = new Intent( OAuthAccessTokenActivity.this,  MainActivity.class );
						//i.putExtra(JoggingoAPI.Strings.LAUNCH_FLAG_BUNDLE, JoggingoAPI.Ints.LAUNCH_FLAG_MAIN_LOAD);
						//i.putExtra("access_token",prefs.getString("access_token",null));
						
						//MainActivity.main_text = (TextView) v.findViewById(R.id.joggingo_main_text);
						
						MainActivity.sync.setText("Synchronize account");
						getUserDetails(OAuth2ClientCredentials.SCOPE+"profile.json?access_token="+prefs.getString("access_token", null));
						
						finish();
					}
					/*
					Log.i("access_token: ", prefs.getString("access_token", null));
					Log.i("expires_in: ", new Date(prefs.getLong("expires_in", 0)).toString() );
					Log.i("refresh_token: ", prefs.getString("refresh_token", null));
					view.setVisibility(View.INVISIBLE);
					 

					*/

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
			getUserDetails(OAuth2ClientCredentials.SCOPE+"profile.json?access_token="+this.prefs.getString("access_token", null));
			finish();
		}
	}

	public void getUserDetails(String URL){
		Log.d("USER_URL",URL);
		JoggingoAPI.requestURL(URL, new ResponseCommand() {
			
			
			public void onResultReceived(Object... results) {

				Log.d("results",results[0].toString());

				

			}

			@Override
			public void onError(ERROR_TYPE error) {
				// TODO Auto-generated method stub
				Log.d("ERRO",error.toString());
			}});

	}
	
	public void clearCredentials() {
		Editor editor = prefs.edit();		
		editor.remove("access_token");
		editor.remove("expires_in");
		editor.remove("refresh_token");
		editor.remove("");
		editor.commit();
		android.webkit.CookieManager.getInstance().removeAllCookie(); //this is used to clear the cookies of the webview
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

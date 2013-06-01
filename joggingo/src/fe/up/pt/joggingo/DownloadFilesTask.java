package fe.up.pt.joggingo;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import AsyncTasks.ResponseCommand.ERROR_TYPE;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

class DownloadFilesTask extends AsyncTask<DatabaseHandler, Integer, Long> {

	private DatabaseHandler db;
	private String URL = "http://belele.herokuapp.com/mobile";
	private long track_id;
	private Context ctx;
	private HttpResponse response;
	
	public DownloadFilesTask(long id, Context c) {
		super();
		track_id = id;
		ctx = c;
	}

	// Do the long-running work in here
	protected Long doInBackground(DatabaseHandler... dbs) {
		db = dbs[0];
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(URL);
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

		Log.d("async", db.getAllTracks().size()+"");
		JSONObject json = new JSONObject();

		List<Track> tracks = new ArrayList<Track>();

		if(track_id != -1){
			Log.d("track_id", track_id+"");
			tracks.add(db.getTrack((int)track_id));

		}
		else{
			Log.d("all tracks", "no track id");
			tracks = db.getAllTracks();
		}


		for(Track t : tracks){

			Log.d("Posting track: ", t.getName());
			try {
				json.put("user_id", t.getUserId());
				json.put("approved", (t.isApproved() == 1? true:false));
				json.put("name", t.getName());
				json.put("city", t.getCity());
				json.put("country", t.getCountry());
				json.put("private", (t.isPrivat()== 1? true:false));

				json.put("initial_time", t.getInitial_time());
				json.put("final_time", t.getFinal_time());
				
				JSONArray pontos = new JSONArray();
				
				for(Point p : db.getAllPoint(t.getId())){
					JSONObject ponto = new JSONObject();
					ponto.put("latitude", p.getLatitude());
					ponto.put("longitude", p.getLongitude());
					pontos.put(ponto);
				}
				json.put("points", pontos);
				
				Log.d("pontos",pontos+"");
					
				nameValuePairs = new ArrayList<NameValuePair>(2);
		        nameValuePairs.add(new BasicNameValuePair("user_id", "1"));
		        nameValuePairs.add(new BasicNameValuePair("approved", "false"));
		        nameValuePairs.add(new BasicNameValuePair("name", "asd"));
		        nameValuePairs.add(new BasicNameValuePair("city", "Porto"));
		        nameValuePairs.add(new BasicNameValuePair("country", "Portugal"));
		        nameValuePairs.add(new BasicNameValuePair("initial_time", "2013:1:1:20:10:1:987"));
		        nameValuePairs.add(new BasicNameValuePair("final_time", "2013:1:1:20:15:1:0"));
		        
				
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		    Log.d("SYNC", json.toString());
		    try {
		    	StringEntity se = new StringEntity(json.toString(), HTTP.UTF_8);  
	            se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
	            
				httppost.setEntity(se);
		    	//httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	            
				//httppost.setHeader( "Content-Type", "application/json" );
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}   
	        try {
				response = httpclient.execute(httppost);
				
				String jsonString = EntityUtils.toString(response.getEntity());
				Log.d("response", jsonString);
				Log.d("responseStatus", response.getStatusLine().toString());
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return (long) 1;
	}

	// This is called each time you call publishProgress()
	protected void onProgressUpdate(Integer... progress) {

	}

	// This is called when doInBackground() is finished
	protected void onPostExecute(Long result) {
		
		if(response.getStatusLine().getStatusCode() == 200)
			Toast.makeText(ctx, "Successfully synchronized",
					Toast.LENGTH_LONG).show();
		else
			Toast.makeText(ctx, "Synchronize failed! Please try again",
					Toast.LENGTH_LONG).show();
		
	}
	
	public void onError(ERROR_TYPE error) {
		Toast.makeText(ctx, "Synchronize failed! Please try again",
				Toast.LENGTH_LONG).show();
	}
}
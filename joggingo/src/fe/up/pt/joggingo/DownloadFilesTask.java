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

import android.os.AsyncTask;
import android.util.Log;

class DownloadFilesTask extends AsyncTask<DatabaseHandler, Integer, Long> {
	
	private DatabaseHandler db;
	private String URL = "http://belele.herokuapp.com/mobile";
	
    // Do the long-running work in here
    protected Long doInBackground(DatabaseHandler... dbs) {
    	db = dbs[0];
    	HttpClient httpclient = new DefaultHttpClient();
	    HttpPost httppost = new HttpPost(URL);
	    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	    
	    Log.d("async", db.getAllTracks().size()+"");
	    JSONObject json = new JSONObject();
	    
	    Track t = db.getAllTracks().get(0);
	    try {
			json.put("user_id", t.getUserId());
			json.put("approved", t.isApproved());
			json.put("name", t.getName());
			json.put("city", t.getCity());
			json.put("country", t.getCountry());
			json.put("private", t.isPrivat());
			json.put("initial_time", "2013:1:1:20:10:1:987");
			json.put("final_time", "2013:1:1:20:15:1:0");
			
			JSONArray pontos = new JSONArray();
			JSONObject ponto = new JSONObject();
			ponto.put("latitude", "41.157671");
			ponto.put("longitude", "-8.627787");
			pontos.put(ponto);
			JSONObject ponto1 = new JSONObject();
			ponto1.put("latitude", "41.158818");
			ponto1.put("longitude", "-8.628495");
			pontos.put(ponto1);
			JSONObject ponto2 = new JSONObject();
			ponto2.put("latitude", "41.158725");
			ponto2.put("longitude", "-8.62982");
			pontos.put(ponto2);
			json.put("points", pontos);
			
			
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
	    	StringEntity se = new StringEntity(json.toString());  
            se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
			httppost.setEntity(se);
	    	//httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            
			//httppost.setHeader( "Content-Type", "application/json" );
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}   
        try {
			HttpResponse response = httpclient.execute(httppost);

			String jsonString = EntityUtils.toString(response.getEntity());
			Log.d("response", jsonString);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    /*
	     * {
			\"user_id\":1,
			\"approved\":false,
			\"name\": \"Trilho da Boavista\",
			\"city\": \"Porto\",
			\"private\":true,
			\"initial_time\": \"2013:1:1:20:10:1:987\",
			\"final_time\": \"2013:1:1:20:15:1:0\",
			\"points\":[{\"latitude\": \"41.157671\",
				\"longitude\": \"-8.627787\"},
				{\"latitude\": \"41.158818\",
				\"longitude\": \"-8.628495\"},
				{\"latitude\": \"41.158725\",
				\"longitude\": \"-8.62982\"},
				{\"latitude\": \"41.157898\",
				\"longitude\": \"-8.63034\"},
				{\"latitude\": \"41.157009\",
				\"longitude\": \"-8.629589\"},
				{\"latitude\": \"41.15734\",
				\"longitude\": \"-8.625716\"},
				{\"latitude\": \"41.15709\",
				\"longitude\": \"-8.624107\"},
				{\"latitude\": \"41.156896\",
				\"longitude\": \"-8.623055\"},
				{\"latitude\": \"41.156702\",
				\"longitude\": \"-8.6218\"},
				{\"latitude\": \"41.156492\",
				\"longitude\": \"-8.62077\"}
				]
			}*/
        return (long) 1;
    }

    // This is called each time you call publishProgress()
    protected void onProgressUpdate(Integer... progress) {
    }

    // This is called when doInBackground() is finished
    protected void onPostExecute(Long result) {
    }
}
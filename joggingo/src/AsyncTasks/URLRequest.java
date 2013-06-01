package AsyncTasks;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import AsyncTasks.ResponseCommand.ERROR_TYPE;

public class URLRequest extends AsyncRequest {

    public URLRequest(ResponseCommand command, String loggerTag) {
            super(command, loggerTag);
    }

    @Override
    protected ERROR_TYPE doInBackground(Object... urls) {

            HttpClient httpclient = new DefaultHttpClient();

            HttpGet request = new HttpGet((String)urls[0]);
            ResponseHandler<String> handler = new BasicResponseHandler();
            
            try {
	
	            if (urls.length < 1)
	                    return ERROR_TYPE.GENERAL;
	            if (isCancelled())
	                    return null;
	
	            this.result = new JSONObject(httpclient.execute(request, handler));

            } catch (JSONException e) {
                //Log.wtf(TAG, e.getCause());
        		try {
					this.result = new JSONArray(httpclient.execute(request, handler));
				} catch (ClientProtocolException e1) {
					e1.printStackTrace();
				} catch (JSONException e1) {
					httpclient.getConnectionManager().shutdown();
	                return ERROR_TYPE.JSON;
					//e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
                httpclient.getConnectionManager().shutdown();
                //return ERROR_TYPE.JSON;
            } catch (ClientProtocolException e) {
                //Log.wtf(TAG, e.getCause());
                
                httpclient.getConnectionManager().shutdown();
                return ERROR_TYPE.NETWORK;
            } catch (IOException e) {
                //Log.wtf(TAG, e.getCause());
                httpclient.getConnectionManager().shutdown();
                return ERROR_TYPE.GENERAL;
            }
            
            httpclient.getConnectionManager().shutdown();
            
            return null;
    }

}

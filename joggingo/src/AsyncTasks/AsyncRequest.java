package AsyncTasks;

import AsyncTasks.ResponseCommand.ERROR_TYPE;
import android.os.AsyncTask;

public abstract class AsyncRequest extends
        AsyncTask<Object, Void, ResponseCommand.ERROR_TYPE> {

    // Tag for the Logger
    protected final String TAG;

    // Response object
    protected final ResponseCommand command;

    // Result object
    protected Object result;

    public AsyncRequest(ResponseCommand command, String loggerTag) {
        this.command = command;
        this.TAG = loggerTag;
    }

    @Override
    protected abstract ERROR_TYPE doInBackground(Object... params);

    protected void onPostExecute(ERROR_TYPE error) {
    	
        if (error == null) {
            command.onResultReceived(this.result);
            return;
        }
    	//Log.d(TAG, this.result.toString());
        
        command.onError(error);
    }

}
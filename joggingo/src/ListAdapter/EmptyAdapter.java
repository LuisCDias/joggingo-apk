package ListAdapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import fe.up.pt.joggingo.R;

public class EmptyAdapter extends BaseAdapter {
    
    private Activity activity;
    private int option;
    
    private static LayoutInflater inflater=null;

    public EmptyAdapter(Activity a, int op) {
    	
        activity = a;
        option = op;
        
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return 1; 
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }
    
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.no_results, null);
     
        TextView message = (TextView) vi.findViewById(R.id.no_results);
        switch(option){
        	case 1: message.setText("No tracks to synchronize!");
        			break;
        	case 2: message.setText("No tracks available!");
					break;
        }

        return vi;
    }
}
	
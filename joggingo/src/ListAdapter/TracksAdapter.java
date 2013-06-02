package ListAdapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import fe.up.pt.joggingo.JoggingoAPI;
import fe.up.pt.joggingo.R;

public class TracksAdapter extends BaseAdapter {
    
    private Activity activity;
    private ArrayList<String> names;
    private ArrayList<String> cities;
    private ArrayList<String> countries;

    private static LayoutInflater inflater=null;

    
    public TracksAdapter(Activity a, ArrayList<String> nam, ArrayList<String> cit, ArrayList<String> count) {
        activity = a;
        names = nam;
        cities = cit;
        countries = count;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return names.size();
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
            vi = inflater.inflate(R.layout.user_tracks, null);
     
        TextView name = (TextView) vi.findViewById(R.id.track_name);
        TextView city = (TextView) vi.findViewById(R.id.track_city);
        
        name.setText(names.get(position));
        city.setText(cities.get(position));
        
        return vi;
    }
}
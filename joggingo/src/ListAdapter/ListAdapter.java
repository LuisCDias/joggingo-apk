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

public class ListAdapter extends BaseAdapter {
    
    private Activity activity;
    private ArrayList<String> titles;
    private ArrayList<String> texts;
    private ArrayList<String> images;
    private ArrayList<String> owners;
    private static LayoutInflater inflater=null;
    private int flag ;
    public ImageLoader imageLoader; 
    
    public ListAdapter(Activity a, ArrayList<String> tlt, ArrayList<String> txt, ArrayList<String> i, ArrayList<String> o, int f) {
        activity = a;
        titles=tlt;
        texts=txt;
        images=i;
        owners = o;
        flag = f;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader=new ImageLoader(activity.getApplicationContext());
    }

    public int getCount() {
        return images.size();
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
            vi = inflater.inflate(R.layout.user_offers, null);
     
       // TextView text=(TextView)vi.findViewById(R.id.user_offerText);
        ImageView image=(ImageView)vi.findViewById(R.id.user_offerImg);
        TextView title = (TextView)vi.findViewById(R.id.user_offerTitle);
        TextView owner = (TextView)vi.findViewById(R.id.user_offerOwner);
        if(flag != 1)
        	 owner.setText(owners.get(position));
        
        //text.setText(texts.get(position));
        title.setText(titles.get(position));
        //title.setText(titles.get(position));
        imageLoader.DisplayImage(JoggingoAPI.Strings.SERVER_URL + images.get(position), image,"offer");
        Log.d("PIRATE", JoggingoAPI.Strings.SERVER_URL + images.get(position));
        return vi;
    }
}
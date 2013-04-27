package ListAdapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import fe.up.pt.joggingo.R;

public class OffersAdapter extends BaseAdapter {
    
    private Activity activity;
    private ArrayList<String> titles;
    private ArrayList<String> texts;
    private ArrayList<String> images;
    private ArrayList<String> owners;
    private ArrayList<String> datas;
    private static LayoutInflater inflater=null;
    int flag;
    int wishflag;
    public ImageLoader imageLoader; 
    
    public OffersAdapter(Activity a, ArrayList<String> tlt, ArrayList<String> txt, ArrayList<String> i, 
    		ArrayList<String> o, ArrayList<String> d, int f, int w) {
        activity = a;
        titles=tlt;
        texts=txt;
        images=i;
        owners=o;
        datas=d;
        flag = f;
        wishflag=w;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader=new ImageLoader(activity.getApplicationContext());
    }

    public int getCount() {
        return titles.size(); //pelo menos um titulo h� sempre
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
            vi = inflater.inflate(R.layout.offer_view, null);
     
        TextView text=(TextView)vi.findViewById(R.id.offerText);
        //TextView title=(TextView)vi.findViewById(R.id.offerTitle);
        TextView seller=(TextView)vi.findViewById(R.id.offerSeller);
        TextView data = (TextView)vi.findViewById(R.id.offerDate);
        ImageView image=(ImageView)vi.findViewById(R.id.offerImage);
        Button b1 = (Button) vi.findViewById(R.id.contactSeller_btn);
        Button b2 = (Button) vi.findViewById(R.id.add_wish_btn);
        
        if(flag==1){
        	b1.setVisibility(View.GONE);
        }
        if(wishflag==1)
        	b2.setText("Remove from wishlist");
        	
        text.setText(texts.get(position));
        //title.setText(titles.get(position));
        seller.setText("From: "+owners.get(position));
        data.setText(datas.get(position));
        
        imageLoader.DisplayImage(images.get(position), image, "offer");

        return vi;
    }
}

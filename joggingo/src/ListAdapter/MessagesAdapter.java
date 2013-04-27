package ListAdapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import fe.up.pt.joggingo.R;

public class MessagesAdapter extends BaseAdapter {

	private Activity activity;
	private ArrayList<String> senders;
	private ArrayList<String> ids;
	private ArrayList<String> receivers;
	private ArrayList<String> messages;
	private ArrayList<String> dates;
	private ArrayList<String> readedFlags;
	private ArrayList<String> messagesTitles;
	private static LayoutInflater inflater=null;
	private int flag ;//0 to inbox, 1 to sentBox & archiveBox
	public ImageLoader imageLoader; 

	public MessagesAdapter(Activity a, ArrayList<String> ids, ArrayList<String> snd, ArrayList<String> rcv,  ArrayList<String> msg, ArrayList<String> date, ArrayList<String> rFlag, ArrayList<String> msgT,   int flag) {
		activity = a;
		
		this.ids = ids;
		senders = snd;
		receivers = rcv;
		messages=msg;
		messagesTitles = msgT;
		dates = date;
		readedFlags = rFlag;
		this.flag = flag;
		inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageLoader=new ImageLoader(activity.getApplicationContext());
		
		Log.d("sendersSize", Integer.toString(senders.size()));
	}

	public int getCount() {
		return senders.size();
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
			vi = inflater.inflate(R.layout.activity_messages, null);

		
		TextView msgSender=(TextView)vi.findViewById(R.id.sender);
		msgSender.setText(senders.get(position));

		
		TextView msgDate = (TextView) vi.findViewById(R.id.date);
		String croppedDate = dates.get(position).substring(0, dates.get(position).length()-15);
		msgDate.setText(croppedDate);

		TextView msgTitle = (TextView) vi.findViewById(R.id.messageTitle);
		msgTitle.setText(messagesTitles.get(position));
		return vi;
	}
}
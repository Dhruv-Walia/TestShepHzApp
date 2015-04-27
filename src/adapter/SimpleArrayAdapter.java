package adapter;

import java.util.ArrayList;

import com.example.testshephzapp.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class SimpleArrayAdapter extends ArrayAdapter<String>{

	LayoutInflater inflater;
	private Context context;
	private ArrayList<String> data,data1;
	ViewHolder holder;

	public SimpleArrayAdapter(Context context, ArrayList<String> data,ArrayList<String> data1) {
		super(context, R.layout.msg_list_row ,data);
		this.context = context;
		this.data = data;
		this.data1 = data1;
	}
	
	public static class ViewHolder
	{
		TextView usr,msg;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		if(inflater==null)
		{
			inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		
		if(convertView == null )
		{
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.msg_list_row,parent,false);
			holder.msg = (TextView)convertView.findViewById(R.id.msg);
			holder.usr = (TextView)convertView.findViewById(R.id.usr);
		}
		
		holder.msg.setText(data.get(position).toString());
		holder.usr.setText(data1.get(position).toString());
		
		return convertView;
	}

}

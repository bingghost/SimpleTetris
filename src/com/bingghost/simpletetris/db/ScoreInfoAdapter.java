package com.bingghost.simpletetris.db;

import java.util.List;

import com.bingghost.simpletetris.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ScoreInfoAdapter extends ArrayAdapter<ScoreInfo> {
	private int resourceID;
	
	public ScoreInfoAdapter(Context context, int textViewResourceId, List<ScoreInfo> objects) {
		super(context, textViewResourceId, objects);
		
		resourceID = textViewResourceId;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ScoreInfo info = getItem(position);
		
		View view;
		ViewHolder viewHolder;
		
		// ‘§º”‘ÿView
		if (convertView == null) {
			view = LayoutInflater.from(getContext()).inflate(resourceID, null);
			
			viewHolder = new ViewHolder();
			viewHolder.imageViewHeadPic = (ImageView) view.findViewById(R.id.nameImage);
			viewHolder.textViewName = (TextView) view.findViewById(R.id.itemName); 
			viewHolder.textViewScore = (TextView) view.findViewById(R.id.itemScore);
			
			view.setTag(viewHolder);
		} else {
			view = convertView;
			viewHolder = (ViewHolder)view.getTag();
		}

		viewHolder.imageViewHeadPic.setImageResource(((ScoreInfo) info).getResID());
		viewHolder.textViewName.setText(info.getName());
		viewHolder.textViewScore.setText(new String("" + info.getScore()));
		
		return view;
	}
	
	class ViewHolder {
		ImageView imageViewHeadPic;
		TextView textViewName;
		TextView textViewScore;
	}
}

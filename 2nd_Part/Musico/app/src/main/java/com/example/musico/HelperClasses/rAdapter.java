package com.example.musico.HelperClasses;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.musico.R;

import java.util.ArrayList;

public class rAdapter extends RecyclerView.Adapter<rAdapter.ViewHolder> {

	private ArrayList<recItem> list;
	public static class ViewHolder extends RecyclerView.ViewHolder{

		private ImageView image;
		private TextView song;
		private TextView artist;

		public ViewHolder(View itemView) {
			super(itemView);
			image = itemView.findViewById(R.id.cImage);
			song = itemView.findViewById(R.id.cSong);
			artist = itemView.findViewById(R.id.cArtist);
		}
	}

	public rAdapter(ArrayList<recItem> list){
		this.list = list;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rec_item, parent, false);
		ViewHolder vHolder = new ViewHolder(v);
		return vHolder;
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		recItem currentItem = list.get(position);
		holder.image.setImageResource(currentItem.getImgResource());
		holder.song.setText(currentItem.getSong());
		holder.artist.setText(currentItem.getArtist());
	}

	@Override
	public int getItemCount() {
		return list.size();
	}
}

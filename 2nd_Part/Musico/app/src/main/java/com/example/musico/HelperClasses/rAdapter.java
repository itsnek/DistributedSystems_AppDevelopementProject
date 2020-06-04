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
	private OnItemClickListener mListener;

	public interface OnItemClickListener{
		void onItemClick(int position);
	}

	public void setOnItemClickListener(OnItemClickListener listener){
			mListener = listener;
	}

	public static class ViewHolder extends RecyclerView.ViewHolder{

		private ImageView image, delImage;
		private TextView artist, song;


		public ViewHolder(View itemView, final OnItemClickListener listener) {
			super(itemView);
			image = itemView.findViewById(R.id.cImage);
			artist = itemView.findViewById(R.id.cArtist);

			itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (listener != null){
						int position = getAdapterPosition();
						if (position != RecyclerView.NO_POSITION){
							listener.onItemClick(position);
						}
					}
				}
			});
		}
	}

	public rAdapter(ArrayList<recItem> list){
		this.list = list;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rec_item_artist, parent, false);
		ViewHolder vHolder = new ViewHolder(v, mListener);
		return vHolder;
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		recItem currentItem = list.get(position);
		holder.image.setImageResource(currentItem.getImgResource());
		holder.artist.setText(currentItem.getArtist());
	}

	@Override
	public int getItemCount() {
		return list.size();
	}
}

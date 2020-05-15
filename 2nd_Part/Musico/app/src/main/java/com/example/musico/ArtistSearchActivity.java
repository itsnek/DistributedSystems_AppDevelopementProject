package com.example.musico;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.example.musico.HelperClasses.rAdapter;
import com.example.musico.HelperClasses.recItem;

import java.util.ArrayList;

public class ArtistSearchActivity extends AppCompatActivity {

	private String artist;
	private RecyclerView recView;
	private rAdapter adapter;
	private RecyclerView.LayoutManager rLayoutManager;

	public static final String EXTRA_MESSAGE = "com.example.musico.MESSAGE";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_artist_search);

		final ArrayList<recItem> List = new ArrayList<>();
		List.add(new recItem(R.drawable.ic_headset_black_24dp, "Sin Boy"));
		List.add(new recItem(R.drawable.ic_headset_black_24dp, "Jme"));
		List.add(new recItem(R.drawable.ic_headset_black_24dp, "Skepta"));
		//TODO: Dynamically fill the list with the songs provided by the broker
		//TODO: Check if song is downloaded and load the correct resource (cross or check)

		recView = findViewById(R.id.recyclerView);
		recView.setHasFixedSize(true);
		rLayoutManager = new LinearLayoutManager(this);
		adapter = new rAdapter(List);

		recView.setLayoutManager(rLayoutManager);
		recView.setAdapter(adapter);

		adapter.setOnItemClickListener(new rAdapter.OnItemClickListener() {
			@Override
			public void onItemClick(int position) {
				artist = List.get(position).getArtist();
				Intent intent = new Intent(ArtistSearchActivity.this, AsyncTaskActivity.class);
				intent.putExtra(EXTRA_MESSAGE, artist);
				startActivity(intent);
			}
		});
	}
}
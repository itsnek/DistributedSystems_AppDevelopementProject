package com.example.musico;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musico.HelperClasses.rAdapter;
import com.example.musico.HelperClasses.recItem;

import java.util.ArrayList;
import java.util.List;

import MyPackage.*;

public class ArtistSearchActivity extends AppCompatActivity {

	private String artist;
	private RecyclerView recView;
	private rAdapter adapter;
	private RecyclerView.LayoutManager rLayoutManager;
	static final String EXTRA_MESSAGE = "MyPackage.MESSAGE";
	Consumer cons = new Consumer();

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		final ArrayList<recItem> List = new ArrayList<>();
		Communicator communicator = new Communicator();

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_artist_search);

		communicator.start();
		cons.setArtistList(communicator.getArrayList());

		if(cons.getArtistList()!=null){
			System.out.println(cons.getArtistList().size());

			for (int i=0; i<cons.getArtistList().size(); i++) {
				System.out.println("mesa eimai");
				List.add(new recItem(R.drawable.ic_headset_black_24dp, cons.getArtistList().get(i)));
			}
			System.out.println(List.size());
		}

//		List.add(new recItem(R.drawable.ic_headset_black_24dp, "Sin Boy"));
//		List.add(new recItem(R.drawable.ic_headset_black_24dp, "Jme"));
//		List.add(new recItem(R.drawable.ic_headset_black_24dp, "Skepta"));
		//TODO: Dynamically fill the list with the songs provided by the broker

		recyclerSetup(List, cons);
	}

	private void recyclerSetup(final ArrayList<recItem> List, final Consumer cons){
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
				cons.handshake(new ArtistName(artist));
				Intent intent = new Intent(ArtistSearchActivity.this, AsyncTaskActivity.class);
				intent.putExtra (EXTRA_MESSAGE, artist);
				intent.putExtra ("Consumer", cons);
				startActivity(intent);
			}
		});
	}

}

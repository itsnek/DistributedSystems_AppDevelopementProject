package com.example.musico;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

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

		Communicator communicator = new Communicator(cons,1);
		final ArrayList<recItem> List = new ArrayList<>();

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_artist_search);

		communicator.start();
		//cons.setIn(communicator.getInputStream());
		while(!Communicator.getEnd()){}//System.out.println("loading");}
		cons.setArtistList(communicator.getArrayList());

		if(cons.getArtistList()!=null){
			for (int i=0; i<cons.getArtistList().size(); i++) {
				List.add(new recItem(R.drawable.ic_headset_black_24dp, cons.getArtistList().get(i)));
			}
		}

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
				Communicator communicator2 = new Communicator(cons,2,artist);
				communicator2.start();
				//cons.setIn(communicator2.getInputStream());
				Intent intent = new Intent(ArtistSearchActivity.this, AsyncTaskActivity.class);
				intent.putExtra (EXTRA_MESSAGE, artist);
				//intent.putExtra ("Consumer", cons);
				//intent.putExtra ("Communicator", (Parcelable) communicator2);
				startActivity(intent);
			}
		});
	}

}

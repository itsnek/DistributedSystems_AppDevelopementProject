package com.example.musico;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.musico.HelperClasses.rAdapterLib;
import com.example.musico.HelperClasses.recItem;

import java.util.ArrayList;

public class LibraryActivity extends AppCompatActivity {

	private RecyclerView recView;
	private rAdapterLib adapter;
	private RecyclerView.LayoutManager rLayoutManager;
	private ArrayList<recItem> List = new ArrayList<>();
	recItem item;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_library);

		Intent inte = getIntent();
		item = inte.getParcelableExtra("item");
		if(!List.contains(item)){ List.add(item); }
		recyclerSetup(List);
	}

	private void recyclerSetup(final ArrayList<recItem> List){
		recView = findViewById(R.id.recyclerViewLib);
		recView.setHasFixedSize(true);
		rLayoutManager = new LinearLayoutManager(this);
		adapter = new rAdapterLib(List);

		recView.setLayoutManager(rLayoutManager);
		recView.setAdapter(adapter);

		adapter.setOnItemClickListener(new rAdapterLib.OnItemClickListener() {
			@Override
			public void onItemClick(int position) {
				Intent intent = new Intent(LibraryActivity.this, MusicPlayerActivity.class);
				intent.putExtra("Song", List.get(position).getSong());
				startActivity(intent);
			}

			@Override
			public void onDeleteClick(int position) {
				List.remove(position);
				//Erase from memory.
				adapter.notifyDataSetChanged();
			}
		});
	}
}

package com.example.musico;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_library);

		recyclerSetup(List);
	}

	private void recyclerSetup(final ArrayList<recItem> List){
		recView = findViewById(R.id.recyclerView);
		recView.setHasFixedSize(true);
		rLayoutManager = new LinearLayoutManager(this);
		adapter = new rAdapterLib(List);

		recView.setLayoutManager(rLayoutManager);
		recView.setAdapter(adapter);

		adapter.setOnItemClickListener(new rAdapterLib.OnItemClickListener() {
			@Override
			public void onItemClick(int position) {
				Intent intent = new Intent(LibraryActivity.this, MusicPlayerActivity.class);
				startActivity(intent);
			}

			@Override
			public void onDeleteClick(int position) {
				List.remove(position);
				adapter.notifyDataSetChanged();
			}
		});
	}
}

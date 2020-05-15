package com.example.musico;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AsyncTaskActivity extends AppCompatActivity {

	private Button btn;
	private EditText editTxt;
	private String artist, song;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_async_task);

		// Get the Intent that started this activity and extract the string
		Intent intent = getIntent();
		artist = intent.getStringExtra(ArtistSearchActivity.EXTRA_MESSAGE);
		TextView textView = findViewById(R.id.artist);
		textView.setText(artist);

		editTxt = findViewById(R.id.plain_text_input);
		btn = findViewById(R.id.searchBtn);
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				song = editTxt.getText().toString();
				if(song.isEmpty()){
					Toast.makeText(AsyncTaskActivity.this, "Please Enter a Song", Toast.LENGTH_SHORT).show();
				}else {

					//EDO STELNEIS SONG KAI ARTIST

				}
			}
		});
	}
}

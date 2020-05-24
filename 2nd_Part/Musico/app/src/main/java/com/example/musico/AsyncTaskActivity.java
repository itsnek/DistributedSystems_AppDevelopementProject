package com.example.musico;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class AsyncTaskActivity extends AppCompatActivity {

	private Button btn;
	private EditText editTxt;
	private String artist, song;
	private Switch swtch;
	boolean online;

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
		swtch = findViewById(R.id.swtch);
		swtch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					online = true;
				} else {
					online = false;
				}
			}
		});
		btn = findViewById(R.id.searchBtn);
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				song = editTxt.getText().toString();
				if(song.isEmpty()){
					Toast.makeText(AsyncTaskActivity.this, "Please Enter a Song", Toast.LENGTH_SHORT).show();
				}else {
					if(online){
						//ONLINE MODE
					}else{
						//OFFLINE MODE
					}
				}
			}
		});
	}
}

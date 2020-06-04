package com.example.musico;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

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
			@RequiresApi(api = Build.VERSION_CODES.O)
			@Override
			public void onClick(View v) {
				song = editTxt.getText().toString();
				Intent intent = getIntent();
                Consumer cons = (Consumer) intent.getSerializableExtra("Consumer");
				if(song.isEmpty()){
					Toast.makeText(AsyncTaskActivity.this, "Please Enter a Song", Toast.LENGTH_SHORT).show();
				}else {
                    cons.requestSong (new ArtistName(artist), song);
					if(online){
						//ONLINE MODE
                        try {
                            playSongOnline(cons);
                        } catch (ClassNotFoundException cnf) {
                            cnf.printStackTrace();
                        } catch (IOException io) {
                            io.printStackTrace();
                        }

					}else{
						//OFFLINE MODE
                        cons.playData(song);
					}
				}
			}
		});
	}

	@RequiresApi(api = Build.VERSION_CODES.O)
	private void playSongOnline (Consumer cons) throws IOException, ClassNotFoundException {
        /* This list contains chunks that came earlier than than their order. For example chunk with number 3 if it arrived earlier
        than chunk with number 2. I delete a chunk after i use it.
         */
        ArrayList<MusicChunk> earlyChunks = new ArrayList<>();
        File mp3File = new File("D:\\Nikos\\Documents\\GitHub\\distributed\\1st_Part\\song.mp3");
        try {
            while (true) {
                if (cons.getIn().readObject() != null) break;
            }

            Message message = (Message) cons.getIn().readObject();
            MusicChunk mChunk = message.getChunk();
            Intent player;
            int partLookingFor = 0;
            if (mChunk.getPartitionNumber() == 0) {
                mp3File.createNewFile();
                Files.write(Paths.get("song.mp3"), mChunk.getPartition());
                player = new Intent(AsyncTaskActivity.this, MusicPlayerActivity.class);
                player.putExtra("file's name", "song.mp3");
                player.putExtra("online", true); // Put true to use online mode in MusicPlayerActivity.
                startActivity(player);
                partLookingFor++; //Now it's equal to one.
            } else {
                earlyChunks.add(mChunk);
            }
            int totalChunks = mChunk.getTotalPartitions();

            while (partLookingFor < totalChunks - 1) {
            	message = (Message) cons.getIn().readObject();
            	mChunk = message.getChunk();
            	if (partLookingFor == mChunk.getPartitionNumber()) {
            		mp3File.createNewFile();
            		Files.write(Paths.get("song.mp3"), mChunk.getPartition());
					player = new Intent(AsyncTaskActivity.this, MusicPlayerActivity.class);
					player.putExtra("file's name" , "song.mp3");
					player.putExtra("online", true);
					startActivity(player);
					partLookingFor++;
				} else {
            		earlyChunks.add(mChunk);
            		for (int i=0; i < earlyChunks.size(); i++) {
            			if (earlyChunks.get(i).getPartitionNumber() == partLookingFor) {
            				mp3File.createNewFile();
            				Files.write(Paths.get("song.mp3"), earlyChunks.get(i).getPartition());
							player = new Intent(AsyncTaskActivity.this, MusicPlayerActivity.class);
							player.putExtra ("file's name", "song.mp3");
							player.putExtra ("online", true);
							startActivity (player);
                            earlyChunks.remove(i); // Remove from list part that we were looking for (remove from RAM) because we wrote it in disk.
            				partLookingFor++;
						}
					}
				}
            }

        } catch (ClassNotFoundException cnf) {
            cnf.printStackTrace();
        } catch (IOException io) {
            io.printStackTrace();
        }
    }
}

package com.example.musico;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.musico.HelperClasses.rAdapter;

import java.io.File;

public class MusicPlayerActivity extends AppCompatActivity {

	private static final int PERMISSION_READ = ;
	private Button btn;
	private SeekBar positionBar;
	private SeekBar volumeBar;
	private TextView elapsedTimeLabel;
	private TextView remainingTimeLabel;
	private MediaPlayer mp;
	private int totalTime;
	private File file;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_music_player);

		btn = findViewById(R.id.playButton);
		elapsedTimeLabel = findViewById(R.id.elapsedTime);
		remainingTimeLabel = findViewById(R.id.remainingTime);

		//Media Player
		mp = MediaPlayer.create(this, Uri.fromFile(file));
		mp.setLooping(false);
		mp.seekTo(0);
		mp.setVolume(0.5f, 0.5f);
		totalTime = mp.getDuration();

		//Position Bar
		positionBar = findViewById(R.id.seekSong);
		positionBar.setMax(totalTime);
		positionBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if(fromUser){
					mp.seekTo(progress);
					positionBar.setProgress(progress);
				}
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}
		});

		//Volume Bar
		volumeBar = findViewById(R.id.volumeBar);
		volumeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				float volumnNum = progress / 100f;
				mp.setVolume(volumnNum, volumnNum);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}
		});

		new Thread(new Runnable() {
			@Override
			public void run() {
				while(mp != null){
					try{
						Message msg = new Message();
						msg.what = mp.getCurrentPosition();
						handler.sendMessage(msg);
						Thread.sleep(1000);
					}catch (InterruptedException e){}
				}
			}
		}).start();
	}

	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg){
			int currentPosition = msg.what;
			positionBar.setProgress(currentPosition);

			String elapsedTime = createTimeLabel(currentPosition);
			elapsedTimeLabel.setText(elapsedTime);

			String remainingTime = createTimeLabel(totalTime - currentPosition);
			remainingTimeLabel.setText("- " + remainingTime);
		}
	};

	public String createTimeLabel(int time){
		String timeLabel = "";
		int min = time / 1000 / 60;
		int sec = time / 1000 % 60;

		timeLabel = min + ":";
		if (sec < 10) timeLabel += "0";
		timeLabel += sec;

		return timeLabel;
	}

	public void playBtnClick(View view){
		if(!mp.isPlaying()){
			mp.start();
			btn.setBackgroundResource(R.drawable.ic_pause);
		}else{
			mp.pause();
			btn.setBackgroundResource(R.drawable.ic_play);
		}
	}

	public boolean checkPermission() {
		int READ_EXTERNAL_PERMISSION = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
		if((READ_EXTERNAL_PERMISSION != PackageManager.PERMISSION_GRANTED)) {
			ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_READ);
			return false;
		}
		return true;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mp !=null){
			mp.release();
		}
	}
}
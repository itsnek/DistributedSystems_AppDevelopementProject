package com.example.musico;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    ImageButton DiskImageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e("MainActivity", "at start");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.mipmap.ic_music_disk_lime);
    }

    public void onStart() {
        super.onStart();

        DiskImageButton = (ImageButton) findViewById(R.id.Disk_Image_Button);

        DiskImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent s = new Intent(view.getContext(), ArtistSearchActivity.class);
                startActivityForResult(s, 0);
            }
        });
    }
}

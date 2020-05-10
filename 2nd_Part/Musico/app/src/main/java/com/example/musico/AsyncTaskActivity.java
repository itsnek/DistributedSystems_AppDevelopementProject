package com.example.musico;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.musico.HelperClasses.rAdapter;
import com.example.musico.HelperClasses.recItem;

import java.util.ArrayList;

public class AsyncTaskActivity extends AppCompatActivity {

    private RecyclerView recView;
    private RecyclerView.Adapter rAdapter;
    private RecyclerView.LayoutManager rLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_async_task);

        ArrayList<recItem> List = new ArrayList<>();
        List.add(new recItem(R.drawable.ic_headset_black_24dp, "Mama", "Sin Boy"));
        List.add(new recItem(R.drawable.ic_headset_black_24dp, "Here", "Jme"));
        List.add(new recItem(R.drawable.ic_headset_black_24dp, "Shutdown", "Skepta"));
        //TODO: Dynamically fill the list with the songs provided by the broker

        recView = findViewById(R.id.recyclerView);
        recView.setHasFixedSize(true);
        rLayoutManager = new LinearLayoutManager(this);
        rAdapter = new rAdapter(List);

        recView.setLayoutManager(rLayoutManager);
        recView.setAdapter(rAdapter);
    }
}

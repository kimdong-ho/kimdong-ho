package com.example.controle;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {
    private ListView listView;
    private PhotoAdapter adapter;
    private ArrayList<PhotoItem> photoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        listView = findViewById(R.id.list_view);
        photoList = new ArrayList<>();

        // 이전 상태 복원
        if (savedInstanceState != null) {
            photoList = savedInstanceState.getParcelableArrayList("photoList");
        }

        // PhotoItem 객체 받아오기
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            PhotoItem photoItem = extras.getParcelable("photoItem");
            if (photoItem != null) {
                photoList.add(photoItem);
            }
        }

        adapter = new PhotoAdapter(this, photoList);
        listView.setAdapter(adapter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("photoList", photoList);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        photoList = savedInstanceState.getParcelableArrayList("photoList");
        adapter = new PhotoAdapter(this, photoList);
        listView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ListActivity.this, MainActivity.class);
        startActivity(intent);
        finish(); // ListActivity 종료
    }
}





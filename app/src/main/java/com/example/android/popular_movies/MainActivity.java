package com.example.android.popular_movies;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toast.makeText(this, "This product uses the TMDb API but is not endorsed or certified by TMDb.", Toast.LENGTH_LONG).show();
    }
}

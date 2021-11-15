package com.programacion.fragments;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class UpdateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        Bundle extras = getIntent().getExtras();
        TextView nombre = findViewById(R.id.txxt);
        nombre.setText("Nombre: " + extras.get("hola"));
    }
}
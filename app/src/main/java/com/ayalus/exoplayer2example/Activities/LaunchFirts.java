package com.ayalus.exoplayer2example.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ayalus.exoplayer2example.R;

public class LaunchFirts extends AppCompatActivity {

    EditText id;
    Button actividad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_firts);

        id = findViewById(R.id.editText);
        actividad = findViewById(R.id.Enviar);


        actividad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String texto = id.getText().toString().trim();

                if (TextUtils.isEmpty(texto)) {

                    id.setError("Ingrese un ID de canal");

                }
                else {

                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(LaunchFirts.this);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("id", texto);
                    editor.apply();

                    Intent intent = new Intent(LaunchFirts.this, MainActivity.class);
                    startActivity(intent);
                    finish();

                }
            }
        });
    }

    private void checkFirstOpen() {
        Boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getBoolean("isFirstRun", true);

        if (!isFirstRun) {
            Intent intent = new Intent(LaunchFirts.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit().putBoolean("isFirstRun",
                false).apply();
    }


}

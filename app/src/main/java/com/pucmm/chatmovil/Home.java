package com.pucmm.chatmovil;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Home extends AppCompatActivity {

    @SuppressLint("MissingInflatedId")
    @Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    EdgeToEdge.enable(this);
    setContentView(R.layout.activity_home);
    ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
        Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
        v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
        return insets;
    });

    // Obtener el nombre de usuario del Intent
    String name = getIntent().getStringExtra("name");

    // Mostrar el nombre de usuario en un TextView
    TextView nameTextView = findViewById(R.id.name_text_view);
    nameTextView.setText("Bienvenido, " + name);

    findViewById(R.id.logout_button).setOnClickListener(view -> {
        getSharedPreferences("prefs", MODE_PRIVATE).edit().putBoolean("isLoggedIn", false).apply();
        startActivity(new Intent(Home.this, Login.class));
        finish();
    });
}
}
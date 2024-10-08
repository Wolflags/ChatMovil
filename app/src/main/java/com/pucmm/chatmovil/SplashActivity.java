package com.pucmm.chatmovil;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SplashActivity extends AppCompatActivity {

    @Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    EdgeToEdge.enable(this);
    setContentView(R.layout.activity_splash);
    ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
        Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
        v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
        return insets;
    });

    new Handler().postDelayed(() -> {
        boolean isLoggedIn = getSharedPreferences("prefs", MODE_PRIVATE).getBoolean("isLoggedIn", false);
        if (isLoggedIn) {
            startActivity(new Intent(SplashActivity.this, Home2.class));
        } else {
            startActivity(new Intent(SplashActivity.this, Login.class));
        }
        finish();
    }, 2000);

        }
    }

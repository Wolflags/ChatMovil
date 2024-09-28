package com.pucmm.chatmovil;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    Button loginButton;
    Button registerButton;
    EditText loginEmailField;
    EditText loginPasswordField;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        loginButton = findViewById(R.id.login_ingresar_button);
        registerButton = findViewById(R.id.login_registrar_button);
        progressBar = findViewById(R.id.login_loading_progress);
        loginEmailField = findViewById(R.id.login_email_field);
        loginPasswordField = findViewById(R.id.login_password_field);

        progressBar.setVisibility(View.GONE);

        setup();

    }

    private void setup() {
        loginButton.setOnClickListener(view -> {
            if (loginEmailField.getText().toString().isEmpty() || loginPasswordField.getText().toString().isEmpty()) {
                return;
            }
            progressBar.setVisibility(View.VISIBLE);
            loginButton.setEnabled(false);
            new Handler().postDelayed(() -> {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(loginEmailField.getText().toString(), loginPasswordField.getText().toString()).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        startActivity(new Intent(Login.this, Home.class));
                        finish();
                    } else {
                        showError(task.getException().getMessage());
                        progressBar.setVisibility(View.GONE);
                        loginButton.setEnabled(true);
                    }
                });


            }, 1000);
        });

        registerButton.setOnClickListener(view -> {
            startActivity(new Intent(Login.this, Register.class));
        });
    }

    private void showError(String error){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error");
        builder.setMessage(error);
        builder.setPositiveButton("Aceptar", null);
        builder.create().show();

    }

}
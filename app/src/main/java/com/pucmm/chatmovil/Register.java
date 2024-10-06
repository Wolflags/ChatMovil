package com.pucmm.chatmovil;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.pucmm.chatmovil.models.UserModel;
import com.pucmm.chatmovil.utils.FirebaseUtil;

public class Register extends AppCompatActivity {

    Button registerButton;
    ProgressBar progressBar;
    EditText registerEmailField;
    EditText registerPasswordField;
    EditText registerNameField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



        registerButton = findViewById(R.id.register_button);
        progressBar = findViewById(R.id.register_loading_progress);
        registerEmailField = findViewById(R.id.register_email_field);
        registerPasswordField = findViewById(R.id.register_password_field);
        registerNameField = findViewById(R.id.nombre_field);

        progressBar.setVisibility(View.GONE);

        setup();
    }


private void setup() {
    registerButton.setOnClickListener(view -> {
        if (registerEmailField.getText().toString().isEmpty() || registerPasswordField.getText().toString().isEmpty() || registerNameField.getText().toString().isEmpty()) {
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        registerButton.setEnabled(false);

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(registerEmailField.getText().toString(), registerPasswordField.getText().toString()).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(registerNameField.getText().toString())
                        .build();
                    user.updateProfile(profileUpdates).addOnCompleteListener(profileTask -> {
                        if (profileTask.isSuccessful()) {
                            // Guardar el estado de inicio de sesión
                            getSharedPreferences("prefs", MODE_PRIVATE).edit().putBoolean("isLoggedIn", true).apply();
                            // Guardar el nombre de usuario en Firestore
                            saveUserToFirestore(user.getEmail(), registerNameField.getText().toString());
                            showHome(registerEmailField.getText().toString(), registerNameField.getText().toString());
                        } else {
                            showError(profileTask.getException().getMessage());
                        }
                    });
                }
            } else {
                showError(task.getException().getMessage());
            }
            progressBar.setVisibility(View.GONE);
            registerButton.setEnabled(true);
        });
    });
}

    private void saveUserToFirestore(String email, String name) {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    UserModel user = new UserModel(name, email, null, FirebaseUtil.currentUserId()); // Asumiendo que el URL de la foto de perfil es null por ahora

    db.collection("users2").document(email)
            .set(user)
            .addOnSuccessListener(aVoid -> {
                // Usuario guardado exitosamente
            })
            .addOnFailureListener(e -> {
                // Error al guardar el usuario
                showError(e.getMessage());
            });
}

    private void showError(String error){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error");
        builder.setMessage(error);
        builder.setPositiveButton("Aceptar", null);
        builder.create().show();

    }

    private void showHome(String email, String name) {
    getSharedPreferences("prefs", MODE_PRIVATE).edit().putString("email", email).apply();
    getSharedPreferences("prefs", MODE_PRIVATE).edit().putString("name", name).apply();
    Intent intent = new Intent(this, Home2.class);
    intent.putExtra("email", email);
    intent.putExtra("name", name);
    startActivity(intent);
}
}
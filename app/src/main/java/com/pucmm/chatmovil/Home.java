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

import com.google.firebase.firestore.FirebaseFirestore;

public class Home extends AppCompatActivity {

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

    //OBTENER EL EMAIL DEL USUARIO DE LAS PREFERENCIAS LOCALES
    String email = getSharedPreferences("prefs", MODE_PRIVATE).getString("email", null);

    //OBTENER EL NOMBRE DEL USUARIO DE LAS PREFERENCIAS LOCALES
    String name = getSharedPreferences("prefs", MODE_PRIVATE).getString("name", null);


    TextView nameTextView = findViewById(R.id.name_text_view);
    nameTextView.setText("Bienvenido, " + name);

    //OBTENER EL NOMBRE DEL USUARIO DE LA BASE DE DATOS POR EL EMAIL
//    if (email != null) {
//        FirebaseFirestore.getInstance().collection("users").document(email).get()
//            .addOnSuccessListener(documentSnapshot -> {
//                if (documentSnapshot.exists()) {
//                    String name = documentSnapshot.getString("name");
//                    TextView nameTextView = findViewById(R.id.name_text_view);
//                    nameTextView.setText("Bienvenido, " + name);
//                }
//            })
//            .addOnFailureListener(e -> {
//                // Manejar el error
//            });
//    }



    findViewById(R.id.logout_button).setOnClickListener(view -> {
        getSharedPreferences("prefs", MODE_PRIVATE).edit().putBoolean("isLoggedIn", false).apply();
        startActivity(new Intent(Home.this, Login.class));
        finish();
    });
}
}
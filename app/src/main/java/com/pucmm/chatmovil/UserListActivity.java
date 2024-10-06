package com.pucmm.chatmovil;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class UserListActivity extends AppCompatActivity {
    private static final String TAG = "UserListActivity";
    private RecyclerView userRecyclerView;
    private UserAdapter userAdapter;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        userRecyclerView = findViewById(R.id.userRecyclerView);
        userAdapter = new UserAdapter(user -> {
            if (!user.getId().equals(currentUserId)) {
                Intent intent = new Intent(UserListActivity.this, ChatActivity.class);
                intent.putExtra("otherUserId", user.getId());
                startActivity(intent);
            } else {
                Toast.makeText(this, "No puedes iniciar un chat contigo mismo", Toast.LENGTH_SHORT).show();
            }
        });

        userRecyclerView.setAdapter(userAdapter);
        userRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadUsers();
    }

    private void loadUsers() {
        FirebaseFirestore.getInstance().collection("users")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<User> users = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        User user = document.toObject(User.class);
                        if (user != null && user.getId() != null) {
                            if (user.getId().equals(currentUserId)) {
                                user.setName(user.getName() + " (Yo)");
                                users.add(0, user); // Añadir el usuario actual al principio de la lista
                            } else {
                                users.add(user);
                            }
                        }
                    }
                    userAdapter.setUsers(users);
                    Log.d(TAG, "Usuarios cargados: " + users.size());
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al cargar usuarios", e);
                    Toast.makeText(this, "Error al cargar usuarios: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
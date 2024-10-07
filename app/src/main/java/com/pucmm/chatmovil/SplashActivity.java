package com.pucmm.chatmovil;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.pucmm.chatmovil.models.UserModel;
import com.pucmm.chatmovil.utils.AndroidUtil;
import com.pucmm.chatmovil.utils.FirebaseUtil;

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
    if(FirebaseUtil.isLoggedIn() && getIntent().getExtras() != null){
        String userId = getIntent().getExtras().getString("userId");
        FirebaseUtil.allChatroomCollectionReference().document(userId).get()
                .addOnCompleteListener(task -> {
                   if(task.isSuccessful()){
                       UserModel model = task.getResult().toObject(UserModel.class);

                       Intent homeIntent = new Intent(this, Home2.class);
                       homeIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                       startActivity(homeIntent);

                       Intent intent = new Intent(this, ChatActivity.class);
                       AndroidUtil.passUserModelAsIntent(intent, model);
                       intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                       startActivity(intent);
                   }
        });
    }else{
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
}

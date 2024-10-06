package com.pucmm.chatmovil.utils;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.pucmm.chatmovil.models.UserModel;

public class AndroidUtil {

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void passUserModelAsIntent(Intent intent, UserModel userModel){
        intent.putExtra("name", userModel.getName());
        intent.putExtra("email", userModel.getEmail());
        intent.putExtra("userId", userModel.getUserId());
    }

    public static UserModel getUserModelFromIntent(Intent intent) {
        UserModel userModel = new UserModel();
        userModel.setName(intent.getStringExtra("name"));
        userModel.setEmail(intent.getStringExtra("email"));
        userModel.setUserId(intent.getStringExtra("userId"));
        return userModel;
    }

}

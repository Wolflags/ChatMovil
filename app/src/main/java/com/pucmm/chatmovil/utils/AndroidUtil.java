package com.pucmm.chatmovil.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.pucmm.chatmovil.models.UserModel;

public class AndroidUtil {

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void passUserModelAsIntent(Intent intent, UserModel userModel){
        intent.putExtra("name", userModel.getName());
        intent.putExtra("email", userModel.getEmail());
        intent.putExtra("userId", userModel.getUserId());
        intent.putExtra("fcmToken", userModel.getFcmToken());
    }

    public static UserModel getUserModelFromIntent(Intent intent) {
        UserModel userModel = new UserModel();
        userModel.setName(intent.getStringExtra("name"));
        userModel.setEmail(intent.getStringExtra("email"));
        userModel.setUserId(intent.getStringExtra("userId"));
        userModel.setFcmToken(intent.getStringExtra("fcmToken"));
        return userModel;
    }

    public static void setProfilePic(Context context, Uri imageUri, ImageView imageView) {
        Glide.with(context)
                .load(imageUri).apply(RequestOptions.circleCropTransform()).into(imageView);
    }

}

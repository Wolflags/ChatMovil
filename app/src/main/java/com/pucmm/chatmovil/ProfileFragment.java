package com.pucmm.chatmovil;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.pucmm.chatmovil.models.UserModel;
import com.pucmm.chatmovil.utils.AndroidUtil;
import com.pucmm.chatmovil.utils.FirebaseUtil;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class ProfileFragment extends Fragment {

    Button cerrar_sesion_btn;
    EditText username_text;
    ImageView profile_image;


    UserModel currentUserModel;
    ActivityResultLauncher<Intent> imagePickLauncher;
    Uri selectedImageUri;


    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    imagePickLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == getActivity().RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null && data.getData() != null) {
                        selectedImageUri = data.getData();
                        AndroidUtil.setProfilePic(getContext(), selectedImageUri, profile_image);
                        updateProfilePic(selectedImageUri);
                    }
                }
            }
    );
}

    @Override
public View onCreateView(LayoutInflater inflater, ViewGroup container,
                         Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_profile, container, false);

    cerrar_sesion_btn = view.findViewById(R.id.cerrar_sesion_btn);
    profile_image = view.findViewById(R.id.profile_image);

    getUserData();

    cerrar_sesion_btn.setOnClickListener(view1 -> {
        getActivity().getSharedPreferences("prefs", getContext().MODE_PRIVATE).edit().putBoolean("isLoggedIn", false).apply();
        FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    FirebaseUtil.logout();
                    Intent intent = new Intent(getContext(),SplashActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        });
    });

    profile_image.setOnClickListener(view1 -> {
        ImagePicker.with(this).cropSquare().compress(512)
                .maxResultSize(512, 512)
                .createIntent(new Function1<Intent, Unit>() {
                    @Override
                    public Unit invoke(Intent intent) {
                        imagePickLauncher.launch(intent);
                        return null;
                    }
                });
    });

    username_text = view.findViewById(R.id.username_text);
    //Obtener nombre de usuario localmente
    String username = getActivity().getSharedPreferences("prefs", getContext().MODE_PRIVATE).getString("name", "");
    username_text.setText(username);

    // Initialize currentUserModel
    FirebaseUtil.currentUserDetails().get().addOnSuccessListener(documentSnapshot -> {
        currentUserModel = documentSnapshot.toObject(UserModel.class);
    });

    return view;
}

void updateProfilePic(Uri selectedImageUri){
    if(selectedImageUri != null){
        FirebaseUtil.getCurrentProfilePicReference().putFile(selectedImageUri).addOnCompleteListener(task -> {

                updateToFirestore();

        });
    }
}

void updateToFirestore(){
    FirebaseUtil.currentUserDetails().set(currentUserModel);
}

    void getUserData(){

        FirebaseUtil.getCurrentProfilePicReference().getDownloadUrl()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Uri uri  = task.getResult();
                        AndroidUtil.setProfilePic(getContext(),uri,profile_image);
                    }
                });

        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {

            currentUserModel = task.getResult().toObject(UserModel.class);

        });
    }

}
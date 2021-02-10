package com.example.students4students.registration;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.example.students4students.R;
import com.example.students4students.model.UserViewModel;

import static android.app.Activity.RESULT_OK;

public class ProfilePictureFragment extends Fragment {
    // Shared ViewModel
    private UserViewModel userModel;

    // Inputs
    private ImageView profilePic;
    private final int choosePictureRequestCode = 1;

    // Buttons
    private Button addProfilePicBtn;
    private Button continueBtn;
    private Button backBtn;

    public ProfilePictureFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile_picture, container, false);

        profilePic = v.findViewById(R.id.profilePicPicker);

        addProfilePicBtn = v.findViewById(R.id.addPicBtn);
        addProfilePicBtn.setOnClickListener(v1 -> {
            chooseProfilePicture();
        });

        continueBtn = v.findViewById(R.id.continueButton_profile);
        continueBtn.setOnClickListener(v12 ->
                Navigation.findNavController(v12).navigate(R.id.goToConfirmDetails));

        backBtn = v.findViewById(R.id.backBtn_profile);
        backBtn.setOnClickListener(v13 ->
                Navigation.findNavController(v13).navigate(R.id.backToAccountDetails));


        return v;
    }

    private void chooseProfilePicture() {
        Intent choosePictureIntent = new Intent();
        choosePictureIntent.setType("image/*");
        choosePictureIntent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(choosePictureIntent, choosePictureRequestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == choosePictureRequestCode && resultCode == RESULT_OK) {
            if(data != null && data.getData() != null) {
                Uri imageUri = data.getData();
                profilePic.setImageURI(imageUri);
                // Set userModel value here
                userModel.setProfilePicUri(imageUri);
            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Instantiate viewModel here
        userModel = new ViewModelProvider(getActivity()).get(UserViewModel.class);


        // Set observer
        // 1. Profile picture observer
        String defaultPicPath =
                "android.resource://" + getActivity().getPackageName() + "/drawable/" +
                R.drawable.default_profile_picture;

        Uri defaultPicUri = Uri.parse(defaultPicPath);

        userModel.getProfilePicUri(defaultPicUri)
                .observe(getViewLifecycleOwner(), uri -> profilePic.setImageURI(uri));
    }
}
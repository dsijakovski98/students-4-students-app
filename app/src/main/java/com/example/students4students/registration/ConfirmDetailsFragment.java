package com.example.students4students.registration;

import android.content.ContentResolver;
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
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.students4students.collections.UserCourses;
import com.example.students4students.user_homepage.DashboardActivity;
import com.example.students4students.Firebase;
import com.example.students4students.R;
import com.example.students4students.collections.User;
import com.example.students4students.model.UserViewModel;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class ConfirmDetailsFragment extends Fragment {
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    
    
    // Shared ViewModel
    private UserViewModel userViewModelModel;

    // Inputs
    private TextView firstNameConfirm;
    private TextView lastNameConfirm;
    private TextView nicknameConfirm;
    private TextView emailConfirm;
    private TextView dateOfBirthConfirm;
    private TextView genderConfirm;
    private TextView majorConfirm;
    private String password;
    private Uri profilePicUri;

    // Progress bar
    private ProgressBar progressBar;

    // Buttons
    private Button registerBtn;
    private Button backBtn;

    public ConfirmDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_confirm_details, container, false);

        firstNameConfirm = v.findViewById(R.id.firstNameConfirm);
        lastNameConfirm = v.findViewById(R.id.lastNameConfirm);
        nicknameConfirm = v.findViewById(R.id.nicknameConfirm);
        emailConfirm = v.findViewById(R.id.emailConfirm);
        dateOfBirthConfirm = v.findViewById(R.id.dobConfirm);
        genderConfirm = v.findViewById(R.id.genderConfirm);
        majorConfirm = v.findViewById(R.id.majorConfirm);

        progressBar = v.findViewById(R.id.registerProgressBar);

        registerBtn = v.findViewById(R.id.registerButton_confirm);
        registerBtn.setOnClickListener(v12 -> {
            // 1. Register user with user auth
            registerUser();
        });

        backBtn = v.findViewById(R.id.backBtn_confirm);
        backBtn.setOnClickListener(v1 -> Navigation.findNavController(v1).navigate(R.id.backToProfilePicDetails));

        return v;
    }

    private void registerUser() {
        progressBar.setVisibility(View.VISIBLE);
        String email = emailConfirm.getText().toString();
        
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        // 2. Upload profile picture to storage
                        uploadProfilePicture(task);
                    }
                    else {
                        // TODO: Registration error handling
                        Toast.makeText(getActivity(),
                                "User registration error!", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    private void uploadProfilePicture(Task<AuthResult> authTask) {
        StorageReference storageRef = storage.getReference();

        String nickname = nicknameConfirm.getText().toString();

        String fileExtension = getFileExtension(profilePicUri);
        if(fileExtension == null) {
            updateUserInfo(authTask, null);
            return;
        }
        String profilePicName = nickname + "_pp." + getFileExtension(profilePicUri);
        StorageReference profilePicRef = storageRef.child(profilePicName);

        profilePicRef.putFile(profilePicUri).addOnCompleteListener(task1 -> {
            if(task1.isSuccessful()) {
                    // 3. Update user displayName and profileUrl
                    profilePicRef.getDownloadUrl().addOnSuccessListener(uri -> updateUserInfo(authTask, uri));
            }
            else {
                // TODO: Profile picture upload error handling
                Toast.makeText(getActivity(),
                        "Profile picture upload error!", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });


    }

    private void updateUserInfo(Task<AuthResult> authTask, Uri profileUri) {
        String nickname = nicknameConfirm.getText().toString();

        FirebaseUser currentUser = authTask.getResult().getUser();
        if(currentUser != null) {
            // Create profile update request
            UserProfileChangeRequest.Builder request = new UserProfileChangeRequest.Builder();
            request.setPhotoUri(profileUri);
            request.setDisplayName(nickname);
            
            currentUser.updateProfile(request.build())
                    .addOnCompleteListener(task1 -> {
                        if(task1.isSuccessful()) {
                            String url = "";
                            if(profileUri != null) {
                                url = profileUri.toString();
                            }
                            // 4. Add user to "users" collection
                            addUserToDatabase(authTask, url);
                        }
                        else {
                            // TODO: Update user info error handling
                            Toast.makeText(getActivity(),
                                    "User info update error!", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    });
        }
    }

    private void addUserToDatabase(Task<AuthResult> task, String profileUrl) {
        CollectionReference usersRef = firestore.collection(Firebase.USER_COLLECTION);

        String uid = task.getResult().getUser().getUid();
        String firstName = firstNameConfirm.getText().toString();
        String lastName = lastNameConfirm.getText().toString();
        String dob = dateOfBirthConfirm.getText().toString();
        String gender = genderConfirm.getText().toString();
        String major = majorConfirm.getText().toString();
        String email = emailConfirm.getText().toString();
        String nickname = nicknameConfirm.getText().toString();

        User user = new User(uid, firstName, lastName, dob, gender, major, email, nickname, profileUrl);


        usersRef.document(uid).set(user).addOnCompleteListener(task1 -> {
            if(task1.isSuccessful()) {
                // 5. Create user_courses collection
                createUserCoursesCollection(uid);
            }
            else {
                // TODO: User add to database error handling
                Toast.makeText(getActivity(),
                        "User add to database error!", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);

            }
        });
    }

    private void createUserCoursesCollection(String uid) {
        CollectionReference userCoursesRef = firestore.collection(Firebase.USER_COURSES_COLLECTION);

        List<String> emptyList = new ArrayList<>();
        UserCourses userCourses = new UserCourses(emptyList);
        
        userCoursesRef.document(uid).set(userCourses).addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                // User is added to firebase!
                progressBar.setVisibility(View.GONE);

                Intent goToDashboard = new Intent(getActivity(), DashboardActivity.class);
                startActivity(goToDashboard);
                getActivity().finish();
            }
            else {
                // TODO: UserCourses add to database error handling
                Toast.makeText(getActivity(),
                        "UserCourses add to database error!", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cr = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Instantiate viewModel here
        userViewModelModel = new ViewModelProvider(getActivity()).get(UserViewModel.class);

        // Set observers
        // 1. First name observer
        userViewModelModel.getFirstName().observe(getViewLifecycleOwner(), fname -> firstNameConfirm.setText(fname));
        // 2. Last name observer
        userViewModelModel.getLastName().observe(getViewLifecycleOwner(), lname -> lastNameConfirm.setText(lname));
        // 3. Email observer
        userViewModelModel.getEmail().observe(getViewLifecycleOwner(), email -> emailConfirm.setText(email));
        // 4. Password observer
        userViewModelModel.getPassword().observe(getViewLifecycleOwner(), pwd -> password = pwd);
        // 5. Nickname observer
        userViewModelModel.getNickname().observe(getViewLifecycleOwner(), nick -> nicknameConfirm.setText(nick));
        // 6. Date of birth observer
        userViewModelModel.getDateOfBirth().observe(getViewLifecycleOwner(), dob -> dateOfBirthConfirm.setText(dob));
        // 7. Gender observer
        String[] genderArray = getActivity().getResources().getStringArray(R.array.gender);
        userViewModelModel.getGender().observe(getViewLifecycleOwner(),
                genderIndex -> genderConfirm.setText(genderArray[genderIndex]));
        // 8. Major observer
        String[] majorsArray = getActivity().getResources().getStringArray(R.array.majors);
        userViewModelModel.getMajor().observe(getViewLifecycleOwner(),
                majorIndex -> majorConfirm.setText(majorsArray[majorIndex]));
        // 9. Profile picture observer
        userViewModelModel.getProfilePicUri(null).observe(getViewLifecycleOwner(),
                uri -> profilePicUri = uri);
    }
}
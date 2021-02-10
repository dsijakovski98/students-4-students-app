package com.example.students4students.registration;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.students4students.Firebase;
import com.example.students4students.R;
import com.example.students4students.model.UserViewModel;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class AccountDetailsFragment extends Fragment {

    FirebaseFirestore firestore;

    // Shared ViewModel
    private UserViewModel userModel;

    // Inputs
    private Spinner majorSpinner;
    private EditText emailInput;
    private EditText nicknameInput;
    private EditText passwordInput;
    private EditText confirmPasswordInput;

    // Buttons
    private Button continueBtn;
    private Button backBtn;

    // Progress bar
    private ProgressBar progressBar;

    public AccountDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_account_details, container, false);

        firestore = FirebaseFirestore.getInstance();

        majorSpinner = v.findViewById(R.id.majorInput);
        emailInput = v.findViewById(R.id.emailInput);
        nicknameInput = v.findViewById(R.id.nicknameInput);
        passwordInput = v.findViewById(R.id.passwordInput);
        confirmPasswordInput = v.findViewById(R.id.confirmPasswordInput);

        progressBar = v.findViewById(R.id.accountDetailsProgressBar);

        continueBtn = v.findViewById(R.id.continueButton_account);
        continueBtn.setOnClickListener(v1 -> {
            if(validAccountDetailsInput()) {
//              checkUniqueNickname(v1);
                checkUniqueEmail(v1);
            }
        });

        backBtn = v.findViewById(R.id.backBtn_account);
        backBtn.setOnClickListener(v12 -> Navigation.findNavController(v12).navigate(R.id.backToGeneralDetails));

        return v;
    }

    private boolean validAccountDetailsInput() {
        String email = emailInput.getText().toString();
        String nickname = nicknameInput.getText().toString();
        String password = passwordInput.getText().toString();
        String confirmPassword = confirmPasswordInput.getText().toString();

        // Email validation
        // 1. Empty field
        if(email.trim().isEmpty())
            return setErrorMessage(emailInput, "Enter your email!");
        // 2. Valid format
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
            return setErrorMessage(emailInput, "Invalid email format!");
        // 3. No capital letters
        if(emailContainsCapitalLetters(email))
            return setErrorMessage(emailInput, "Email can't contain uppercase letters!");

        // Nickname validation
        // 1. Empty field
        if(nickname.trim().isEmpty())
            return setErrorMessage(nicknameInput, "Enter your nickname! Must be unique.");

        // Password validation
        // 1. Empty field
        if(password.trim().isEmpty())
            return setErrorMessage(passwordInput, "Enter your password!");
        // 2. Password length > 6
        if(password.length() < 6)
            return setErrorMessage(passwordInput, "Password must be at least 6 characters!");

        // Confirm password validation
        // 1. Matching passwords
        if(!confirmPassword.equals(password))
            return setErrorMessage(confirmPasswordInput, "Passwords don't match!");


        // Valid inputs at this point
        userModel.setEmail(email);
        userModel.setPassword(password);
        userModel.setConfirmPassword(confirmPassword);
        userModel.setNickname(nickname);
        userModel.setMajor(majorSpinner.getSelectedItemPosition());

        return true;
    }

    private void checkUniqueEmail(View view) {
        progressBar.setVisibility(View.VISIBLE);

        String email = emailInput.getText().toString();

        CollectionReference usersRef = firestore.collection(Firebase.USER_COLLECTION);
        usersRef.whereEqualTo(Firebase.USER_COLLECTION_EMAIL, email)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        if(task.getResult() != null && task.getResult().getDocuments().isEmpty()) {
                            // Unique email
                            checkUniqueNickname(view);
                        }
                        else {
                            setErrorMessage(emailInput, "This email is already registered!");
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                    else {
                        // TODO: Email find error handling
                        Toast.makeText(getActivity(),
                                "Error in finding emails", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    private void checkUniqueNickname(View view) {
        String nickname = nicknameInput.getText().toString();

        CollectionReference usersRef = firestore.collection(Firebase.USER_COLLECTION);

        usersRef.whereEqualTo(Firebase.USER_COLLECTION_NICKNAME, nickname)
                .get()
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);

                    if(task.isSuccessful()) {
                        if(task.getResult() != null && task.getResult().getDocuments().isEmpty()) {
                            // Unique username
                            Navigation.findNavController(view).navigate(R.id.goToProfilePicDetails);
                        }
                        else {
                            setErrorMessage(nicknameInput, "Nickname already exists!");
                        }
                    }
                    else {
                        // TODO: Nickname find error handling
                        Toast.makeText(getActivity(),
                                "Error in finding nicknames", Toast.LENGTH_SHORT).show();
                    }
            });

    }

    private boolean emailContainsCapitalLetters(String email) {
        boolean containsCapitalLetters = false;

        for(char letter : email.toCharArray()) {
            if(letter == '@') break;

            if(Character.isUpperCase(letter)) {
                containsCapitalLetters = true;
                break;
            }
        }

        return containsCapitalLetters;
    }

    private boolean setErrorMessage(EditText input, String errorMessage) {
        input.setError(errorMessage);
        input.requestFocus();
        return false;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Spinner layout item
        ArrayAdapter adapter = ArrayAdapter.createFromResource(getActivity(), R.array.majors, R.layout.spinner_item);
        majorSpinner.setAdapter(adapter);

        // Instantiate viewModel here
        userModel = new ViewModelProvider(getActivity()).get(UserViewModel.class);

        // Set observers
        // 1. Email observer
        userModel.getEmail().observe(getViewLifecycleOwner(), email -> emailInput.setText(email));
        // 2. Password observer
        userModel.getPassword().observe(getViewLifecycleOwner(), pwd -> passwordInput.setText(pwd));
        // 3. Confirm password observer
        userModel.getConfirmPassword().observe(getViewLifecycleOwner(), confirmPwd -> confirmPasswordInput.setText(confirmPwd));
        // 4. Nickname observer
        userModel.getNickname().observe(getViewLifecycleOwner(), nickname -> nicknameInput.setText(nickname));
        // 5. Major observer
        userModel.getMajor().observe(getViewLifecycleOwner(), majorIndex -> majorSpinner.setSelection(majorIndex));
    }
}
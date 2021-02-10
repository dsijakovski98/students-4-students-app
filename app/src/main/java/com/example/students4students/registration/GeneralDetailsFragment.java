package com.example.students4students.registration;

import android.app.DatePickerDialog;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.students4students.R;
import com.example.students4students.model.UserViewModel;

import java.util.Calendar;

public class GeneralDetailsFragment extends Fragment {
    // Shared ViewModel
    private UserViewModel userModel;

    // Inputs
    private EditText firstNameInput;
    private EditText lastNameInput;
    private TextView dateOfBirthTextView;
    private Spinner genderSpinner;

    // Buttons
    private ImageView pickDateButton;
    private Button continueBtn;
    private Button cancelBtn;

    private String unsetDateOfBirth;

    public GeneralDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_general_details, container, false);

        firstNameInput = v.findViewById(R.id.firstNameInput);
        lastNameInput = v.findViewById(R.id.lastNameInput);
        dateOfBirthTextView = v.findViewById(R.id.dateOfBirthText);
        genderSpinner = v.findViewById(R.id.genderInput);

        unsetDateOfBirth = getActivity().getString(R.string.dob);

        // DATE PICKER CODE
        Calendar current = Calendar.getInstance();
        int year = current.get(Calendar.YEAR);
        int month = current.get(Calendar.MONTH);
        int day = current.get(Calendar.DAY_OF_MONTH);
        pickDateButton = v.findViewById(R.id.calendarInput);
        pickDateButton.setOnClickListener(v1 -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), (view, year1, month1, dayOfMonth) -> {
                // Set date here
                String dateChosen = String.format("%d/ %d/ %d", dayOfMonth, (month1+1), year1);

                // Unset error messages
                dateOfBirthTextView.setError(null);
                pickDateButton.setColorFilter(null);
                dateOfBirthTextView.setText(dateChosen);

            }, year, month+1, day);
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);
            datePickerDialog.show();
        });

        // CONTINUE BUTTON EVENT LISTENER
        continueBtn = v.findViewById(R.id.continueButton_general);
        continueBtn.setOnClickListener(v12 -> {
            if(validGeneralDetailsInput()) {
                Navigation.findNavController(v12).navigate(R.id.goToAccountDetails);
            }
        });

        // CANCEL BUTTON EVENT LISTENER
        cancelBtn = v.findViewById(R.id.cancelBtn_general);
        cancelBtn.setOnClickListener(v13 -> getActivity().finish());

        return v;
    }

    private boolean validGeneralDetailsInput() {
        String firstName = firstNameInput.getText().toString();
        String lastName = lastNameInput.getText().toString();
        String date = dateOfBirthTextView.getText().toString();

        // Empty fields
        // Empty first name field
        if(firstName.trim().isEmpty()) return setErrorMessage(firstNameInput, "Enter your first name!");
        // Empty last name field
        if(lastName.trim().isEmpty()) return setErrorMessage(lastNameInput, "Enter your last name!");
        // Empty date field
        if(date.equals(unsetDateOfBirth)) {
            dateOfBirthTextView.setError("Set date of birth!");
            pickDateButton.setColorFilter(ContextCompat.getColor(getActivity(), R.color.design_default_color_error),
                    PorterDuff.Mode.MULTIPLY);
            return false;
        }

        // At this point, its a valid input
        userModel.setFirstName(firstName);
        userModel.setLastName(lastName);
        userModel.setDateOfBirth(date);
        userModel.setGender(genderSpinner.getSelectedItemPosition());

        return true;
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
        ArrayAdapter adapter = ArrayAdapter.createFromResource(getActivity(), R.array.gender, R.layout.spinner_item);
        genderSpinner.setAdapter(adapter);

        // Instantiate viewModel here
        userModel = new ViewModelProvider(getActivity()).get(UserViewModel.class);

        // Set observers
        // 1. First name observer
        userModel.getFirstName().observe(getViewLifecycleOwner(), firstName -> firstNameInput.setText(firstName));
        // 2. Last name observer
        userModel.getLastName().observe(getViewLifecycleOwner(), lastName -> lastNameInput.setText(lastName));
        // 3. Date of birth observer
        userModel.getDateOfBirth().observe(getViewLifecycleOwner(), dob -> dateOfBirthTextView.setText(dob));
        // 4. Gender observer
        userModel.getGender().observe(getViewLifecycleOwner(), genderIndex -> genderSpinner.setSelection(genderIndex) );
    }

}
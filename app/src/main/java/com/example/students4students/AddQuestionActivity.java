package com.example.students4students;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.students4students.collections.Course;
import com.example.students4students.collections.Question;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AddQuestionActivity extends AppCompatActivity {

    private EditText titleInput;
    private EditText contentInput;

    private Spinner topicSpinner;

    private Button askQuestionBtn;
    private Button cancelBtn;

    private ProgressBar progressBar;

    List<String> courseIdsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_question);

        titleInput = findViewById(R.id.questionTitleInput);
        contentInput = findViewById(R.id.questionContentInput);

        courseIdsList = new ArrayList<>();

        progressBar = findViewById(R.id.addQuestionProgressBar);

        topicSpinner = findViewById(R.id.addQuestion_topicSpinner);
        addSpinnerData();


        askQuestionBtn = findViewById(R.id.addQuestion_btn);
        askQuestionBtn.setOnClickListener(v -> {
            if(validateQuestionInputs()) {
                askQuestion();
            }
        });

        cancelBtn = findViewById(R.id.addQuestion_back_btn);
        cancelBtn.setOnClickListener(v -> finish());
    }

    private void addSpinnerData() {
        CollectionReference coursesRef = FirebaseFirestore.getInstance().collection(Firebase.COURSE_COLLECTION);

        coursesRef.orderBy(Firebase.COURSE_COLLECTION_NAME, Query.Direction.DESCENDING)
                .get().addOnCompleteListener(task -> {
                   if(task.isSuccessful()) {
                       if(task.getResult() != null && !task.getResult().getDocuments().isEmpty()) {
                           List<String> courseNamesList = new ArrayList<>();

                           for(DocumentSnapshot snapshot : task.getResult().getDocuments()) {
                                Course course = snapshot.toObject(Course.class);
                                if(course != null) {
                                    courseNamesList.add(course.getName());
                                    courseIdsList.add(course.getId());
                                }
                           }

                           String[] courseNames = courseNamesList.stream().toArray(String[]::new);
                           ArrayAdapter<String> spinnerArrayAdapter =
                                   new ArrayAdapter<>(this, R.layout.spinner_item, courseNames);
                            topicSpinner.setAdapter(spinnerArrayAdapter);
                       }
                   }
                   else {
                       // TODO: Get courses error handling
                       Toast.makeText(this, "Error on courses fetch", Toast.LENGTH_SHORT).show();
                   }
                });

    }

    private boolean validateQuestionInputs() {
        String title = titleInput.getText().toString();
        String content = contentInput.getText().toString();

        // Empty fields
        if(title.trim().isEmpty())
           return setErrorMessage(titleInput, "Enter question title!");
        if(content.trim().isEmpty())
            return setErrorMessage(contentInput, "Enter question content!");


        // Pretty much valid inputs here
        return true;
    }

    private void askQuestion() {
        progressBar.setVisibility(View.VISIBLE);

        String id = UUID.randomUUID().toString();
        String title = titleInput.getText().toString();
        String content = contentInput.getText().toString();

        Timestamp timestamp = Timestamp.now();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String creatorId = currentUser.getUid();
        String creatorNickname = currentUser.getDisplayName();

        Uri creatorUri = currentUser.getPhotoUrl();
        String creatorProfileUrl = "";

        if(creatorUri != null) {
            creatorProfileUrl = creatorUri.toString();
        }

        String topicName = topicSpinner.getSelectedItem().toString();
        String topicId = courseIdsList.get(topicSpinner.getSelectedItemPosition());

        Question question = new Question(id, title, content, timestamp,
                creatorId, creatorNickname, creatorProfileUrl,
                topicId, topicName);

        CollectionReference questionsRef = FirebaseFirestore.getInstance().collection(Firebase.QUESTION_COLLECTION);
        questionsRef.document(id).set(question)
                .addOnCompleteListener(task -> {
                   if(task.isSuccessful()) {
                       progressBar.setVisibility(View.VISIBLE);
                       Toast.makeText(this, "Question asked successfully!", Toast.LENGTH_SHORT).show();
                       finish();
                   }
                   else {
                       // TODO: Question add to database error handling
                       Toast.makeText(this, "Error on question add", Toast.LENGTH_SHORT).show();
                       progressBar.setVisibility(View.GONE);
                   }
                });
    }



    private boolean setErrorMessage(EditText input, String errorMessage) {
        input.setError(errorMessage);
        input.requestFocus();
        return false;
    }
}
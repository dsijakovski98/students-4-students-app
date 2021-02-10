package com.example.students4students;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.students4students.adapters.CommentAdapter;
import com.example.students4students.collections.Comment;
import com.example.students4students.collections.Question;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class QuestionThreadActivity extends AppCompatActivity {


    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    CollectionReference questionsRef = firestore.collection(Firebase.QUESTION_COLLECTION);
    CollectionReference commentsRef = firestore.collection(Firebase.COMMENT_COLLECTION);

    private Question question;

    private String questionId;

    private TextView questionTitle;
    private TextView questionTimestamp;
    private TextView questionCreatorNickname;
    private TextView questionTopic;
    private TextView questionContent;

    private CircleImageView questionCreatorPicture;

    private ProgressBar progressBar;

    private CommentAdapter commentAdapter;
    private RecyclerView recyclerView;
    
    private Button addCommentButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_thread);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.questionThreadToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_exit_light);

        Intent intent = getIntent();
        questionId = intent.getStringExtra(Firebase.QUESTION_COLLECTION_ID);
        
        addCommentButton = findViewById(R.id.addCommentButton);
        addCommentButton.setOnClickListener(v -> {
            CommentDialogFragment commentDialogFragment =
                    new CommentDialogFragment(question);
            commentDialogFragment.show(this.getSupportFragmentManager(), "Comment Dialog Fragment");
        });
        addCommentButton.setVisibility(View.INVISIBLE);

        questionTitle = findViewById(R.id.questionThreadQuestionTitle);
        questionTimestamp = findViewById(R.id.questionThreadQuestionTimestamp);
        questionCreatorNickname = findViewById(R.id.questionThreadUserNickname);
        questionTopic = findViewById(R.id.questionThreadQuestionTopic);
        questionContent = findViewById(R.id.questionThreadQuestionContent);

        questionCreatorPicture = findViewById(R.id.questionThreadUserPicture);

        progressBar = findViewById(R.id.questionThreadProgressBar);

        recyclerView = findViewById(R.id.questionThreadCommentsRecyclerView);

        // Clear text
        questionTitle.setText("");
        questionTimestamp.setText("");
        questionCreatorNickname.setText("");
        questionTopic.setText("");
        questionContent.setText("");
        // Set picture invisible and progress bar visible
        questionCreatorPicture.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        
        getQuestionData();
        setUpCommentsRecyclerView();
    }

    private void setUpCommentsRecyclerView() {
        Query query = commentsRef.orderBy(Firebase.COMMENT_COLLECTION_TIMESTAMP, Query.Direction.DESCENDING)
                .whereEqualTo(Firebase.COMMENT_COLLECTION_QUESTION_ID, questionId);

        FirestoreRecyclerOptions<Comment> options = new FirestoreRecyclerOptions.Builder<Comment>()
                .setQuery(query, Comment.class).build();

        commentAdapter = new CommentAdapter(options, this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(commentAdapter);

        addCommentButton.setVisibility(View.VISIBLE);
    }

    private void getQuestionData() {
        questionsRef.document(questionId).get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        if(task.getResult() != null) {
                            question = task.getResult().toObject(Question.class);
                            if(question != null) {
                                questionTitle.setText(question.getTitle());
                                Timestamp timestamp = question.getTimestamp();
                                Date date = timestamp.toDate();

                                SimpleDateFormat formatter =
                                        new SimpleDateFormat("dd MMM, yyyy hh:mm", Locale.getDefault());
                                String dateString = formatter.format(date);
                                questionTimestamp.setText(dateString);

                                questionCreatorNickname.setText(question.getCreatorNickname());

                                questionTopic.setText(question.getTopicName());

                                questionContent.setText(question.getContent());

                                String creatorProfileUrl = question.getCreatorProfileUrl();
                                if(!creatorProfileUrl.isEmpty()) {
                                    Uri picUrl = Uri.parse(creatorProfileUrl);
                                    Picasso.with(this).load(picUrl).noFade().into(questionCreatorPicture);
                                }

                                questionCreatorPicture.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                        else {
                            // TODO: No question found error handling
                            Toast.makeText(this, "No question found error", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        // TODO: Get question data error handling
                        Toast.makeText(this, "Getting question data error!", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void logoutUser() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signOut();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    // Toolbar functions
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.question_thread_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // Toolbar back arrow click listener
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.settings_item:
                // TODO: Open settings here... android 2
                Toast.makeText(this, "Opening settings... Next semester", Toast.LENGTH_SHORT).show();
                break;
            case R.id.logout_item:
                logoutUser();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        if(commentAdapter != null)
            commentAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(commentAdapter != null)
            commentAdapter.stopListening();
    }
}
package com.example.students4students.user_homepage;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.students4students.AddQuestionActivity;
import com.example.students4students.Firebase;
import com.example.students4students.QuestionThreadActivity;
import com.example.students4students.R;
import com.example.students4students.adapters.FindQuestionsAdapter;
import com.example.students4students.collections.Question;
import com.example.students4students.collections.UserCourses;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FindQuestionsFragment extends Fragment {

    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    CollectionReference questionRef = firestore.collection(Firebase.QUESTION_COLLECTION);

    private FindQuestionsAdapter questionsAdapter;
    private RecyclerView recyclerView;

    private FloatingActionButton openAddQuestionsBtn;

    private ConstraintLayout noQuestionsLayout;

    public FindQuestionsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_find_questions, container, false);

        recyclerView = view.findViewById(R.id.findQuestions_recyclerView);
        openAddQuestionsBtn = view.findViewById(R.id.findQuestionsFab);
        noQuestionsLayout = view.findViewById(R.id.findQuestions_noQuestionsContainer);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        openAddQuestionsBtn.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), AddQuestionActivity.class));
        });

        setUpRecyclerView();
    }



    private void setUpRecyclerView() {

        Query query = questionRef.orderBy(Firebase.QUESTION_COLLECTION_TIMESTAMP, Query.Direction.DESCENDING)
                .orderBy(Firebase.QUESTION_COLLECTION_TOPIC_NAME, Query.Direction.DESCENDING);


        FirestoreRecyclerOptions<Question> options = new FirestoreRecyclerOptions.Builder<Question>()
                .setQuery(query, Question.class).build();

        questionsAdapter = new FindQuestionsAdapter(options, getActivity());

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(questionsAdapter);

        // TODO: Find a way to check for number of questions
//        if(recyclerView.getChildCount() > 0) {
//            recyclerView.setVisibility(View.VISIBLE);
//            noQuestionsLayout.setVisibility(View.GONE);
//        }
//        else {
//            recyclerView.setVisibility(View.GONE);
//            noQuestionsLayout.setVisibility(View.VISIBLE);
//        }

        // Open question thread click listener
        questionsAdapter.setOnItemClickListener((snapshot, position) -> {
            String questionId = snapshot.getId();
            Intent openQuestionThread = new Intent(getActivity(), QuestionThreadActivity.class);
            openQuestionThread.putExtra(Firebase.QUESTION_COLLECTION_ID, questionId);

            startActivity(openQuestionThread);
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        if(questionsAdapter != null)
            questionsAdapter.startListening();
    }


    @Override
    public void onStop() {
        super.onStop();
        if(questionsAdapter != null)
            questionsAdapter.stopListening();
    }
}
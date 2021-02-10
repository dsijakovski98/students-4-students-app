package com.example.students4students.user_homepage;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.students4students.AddQuestionActivity;
import com.example.students4students.Firebase;
import com.example.students4students.QuestionThreadActivity;
import com.example.students4students.R;
import com.example.students4students.adapters.MyQuestionsAdapter;
import com.example.students4students.collections.Question;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class MyQuestionsFragment extends Fragment {

    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    CollectionReference questionsRef = firestore.collection(Firebase.QUESTION_COLLECTION);

    private MyQuestionsAdapter questionsAdapter;

    public MyQuestionsFragment() {
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
        return inflater.inflate(R.layout.fragment_my_questions, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setUpMyQuestionsRecyclerView();
    }

    private void setUpMyQuestionsRecyclerView() {
        Query query = questionsRef.
                orderBy(Firebase.QUESTION_COLLECTION_TIMESTAMP, Query.Direction.DESCENDING)
                .whereEqualTo(Firebase.QUESTION_COLLECTION_CREATOR_ID, currentUser.getUid());

        FirestoreRecyclerOptions<Question> options = new FirestoreRecyclerOptions.Builder<Question>()
                .setQuery(query, Question.class).build();


        questionsAdapter = new MyQuestionsAdapter(options, getActivity());

        RecyclerView recyclerView = getActivity().findViewById(R.id.myQuestionsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(questionsAdapter);

        // On swipe delete listener
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                if(direction == ItemTouchHelper.LEFT) {
                    questionsAdapter.deleteItem(viewHolder.getAdapterPosition());
                }
            }
        }).attachToRecyclerView(recyclerView);


        // Open question thread click listener
        questionsAdapter.setOnItemClickListener((snapshot, position) -> {
            String questionId = snapshot.getId();
            Intent openQuestionThread = new Intent(getActivity(), QuestionThreadActivity.class);
            openQuestionThread.putExtra(Firebase.QUESTION_COLLECTION_ID, questionId);

            startActivity(openQuestionThread);
        });

        // Open AddQuestion activity click listener
        FloatingActionButton openAddQuestion = getActivity().findViewById(R.id.myQuestionsAddQuestionButton);
        openAddQuestion.setOnClickListener(v -> startActivity(new Intent(getActivity(), AddQuestionActivity.class)));

    }

    @Override
    public void onStart() {
        super.onStart();
        questionsAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        questionsAdapter.stopListening();
    }
}
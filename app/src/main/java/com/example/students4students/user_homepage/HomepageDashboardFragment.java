package com.example.students4students.user_homepage;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.students4students.AddQuestionActivity;
import com.example.students4students.CourseDialogFragment;
import com.example.students4students.Firebase;
import com.example.students4students.QuestionThreadActivity;
import com.example.students4students.R;
import com.example.students4students.adapters.HomepageCourseAdapter;
import com.example.students4students.collections.Course;
import com.example.students4students.collections.Question;
import com.example.students4students.collections.UserCourses;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.List;

public class HomepageDashboardFragment extends Fragment {

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser currentUser;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    CollectionReference coursesRef = firestore.collection(Firebase.COURSE_COLLECTION);
    CollectionReference userCoursesRef = firestore.collection(Firebase.USER_COURSES_COLLECTION);
    CollectionReference questionsRef = firestore.collection(Firebase.QUESTION_COLLECTION);


    private HomepageCourseAdapter courseAdapter;

    private ConstraintLayout latestQuestionLayout;
    private ConstraintLayout myCoursesLayout;
    private ConstraintLayout noQuestionsNoCoursesLayout;

    private TextView latestQuestionTitle;
    private TextView latestQuestionContent;
    private TextView latestQuestionTopic;

    private Button openQuestionThreadButton;
    private FloatingActionButton openAddQuestionButton;

    private RecyclerView recyclerView;

    private String latestQuestionId;

    private List<String> coursesList;

    public HomepageDashboardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_homepage_dashboard, container, false);

        currentUser = auth.getCurrentUser();
        latestQuestionLayout = view.findViewById(R.id.myQuestionContainer);
        myCoursesLayout = view.findViewById(R.id.myCoursesContainer);
        noQuestionsNoCoursesLayout = view.findViewById(R.id.noQuestionsNoCoursesContainer);

        latestQuestionTitle = view.findViewById(R.id.myLatestQuestionTitle);
        latestQuestionContent = view.findViewById(R.id.myLatestQuestionContent);
        latestQuestionTopic = view.findViewById(R.id.myLatestQuestionTopic);

        openQuestionThreadButton = view.findViewById(R.id.latestQuestionViewThreadButton);
        openAddQuestionButton = view.findViewById(R.id.homepageFabAddQuestion);

        recyclerView = view.findViewById(R.id.courseListHomepageRecyclerView);

        latestQuestionLayout.setVisibility(View.GONE);
        myCoursesLayout.setVisibility(View.GONE);
        noQuestionsNoCoursesLayout.setVisibility(View.GONE);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        openQuestionThreadButton.setOnClickListener(v -> {
            Intent openQuestionThread = new Intent(getActivity(), QuestionThreadActivity.class);
            openQuestionThread.putExtra(Firebase.QUESTION_COLLECTION_ID, latestQuestionId);

            startActivity(openQuestionThread);
        });

        openAddQuestionButton.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), AddQuestionActivity.class));
        });

        // 1. Get user courses
        getUserCourses();
    }

    private void getUserCourses() {
        String uid = currentUser.getUid();

        userCoursesRef.document(uid).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                if(task.getResult() != null) {
                    UserCourses userCourses = task.getResult().toObject(UserCourses.class);
                    if(userCourses != null) {
                        coursesList = userCourses.getCourses();

                        // 2.1 Set up courses recycler view
                        if(!coursesList.isEmpty()) {

                            // 3. Set up courses recycler view
                            myCoursesLayout.setVisibility(View.VISIBLE);
                            noQuestionsNoCoursesLayout.setVisibility(View.GONE);
                            setUpCoursesRecyclerView();
                            courseAdapter.startListening();
                        }
                        else {
                            myCoursesLayout.setVisibility(View.GONE);
                        }

                        // 2.2 Get user latest question
                        getLatestQuestion(uid);
                    }
                }
            }
            else {
                // TODO: Get user_courses error handling
                Toast.makeText(getActivity(), "Error on getting user courses!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getLatestQuestion(String uid) {

        questionsRef
            .orderBy(Firebase.QUESTION_COLLECTION_TIMESTAMP, Query.Direction.DESCENDING)
            .whereEqualTo(Firebase.QUESTION_COLLECTION_CREATOR_ID, uid)
            .addSnapshotListener((value, error) -> {
                if(error != null) {
                    Toast.makeText(getActivity(),
                "Error on getting user questions " + error.toString(), Toast.LENGTH_SHORT).show();
                    return;
                }

                if(value != null) {
                    List<DocumentSnapshot> snapshots = value.getDocuments();
                    if(snapshots.isEmpty()) {
                        // User has no questions asked
                        // If there are no questions nor courses subscriptions
                        if (coursesList.isEmpty()) {
                            latestQuestionLayout.setVisibility(View.GONE);
                            myCoursesLayout.setVisibility(View.GONE);
                            noQuestionsNoCoursesLayout.setVisibility(View.VISIBLE);
                        }
                    }
                    else {
                        Question latestQuestion = snapshots.get(0).toObject(Question.class);

                        latestQuestionTitle.setText(latestQuestion.getTitle());
                        latestQuestionContent.setText(latestQuestion.getContent());
                        latestQuestionTopic.setText(latestQuestion.getTopicName());

                        latestQuestionId = latestQuestion.getId();

                        latestQuestionLayout.setVisibility(View.VISIBLE);
                        noQuestionsNoCoursesLayout.setVisibility(View.GONE);
                    }

                }
                else {
                    // User has no questions asked
                    Toast.makeText(getActivity(),
                            "Null value on getting user questions", Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void setUpCoursesRecyclerView() {
        Query query = coursesRef.orderBy(Firebase.COURSE_COLLECTION_NAME, Query.Direction.DESCENDING)
                .whereIn(Firebase.COURSE_COLLECTION_ID, coursesList);


        FirestoreRecyclerOptions<Course> options = new FirestoreRecyclerOptions.Builder<Course>()
                .setQuery(query, Course.class).build();

        courseAdapter = new HomepageCourseAdapter(options, getActivity());

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(courseAdapter);

        // Open course dialog
        courseAdapter.setOnItemClickListener((snapshot, position) -> {
            CourseDialogFragment courseDialogFragment =
                    new CourseDialogFragment(snapshot, CourseDialogFragment.ACTION_NO_BUTTON);
            courseDialogFragment.show(getActivity().getSupportFragmentManager(), "Course Dialog Fragment");
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if(courseAdapter != null)
            courseAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(courseAdapter != null)
            courseAdapter.stopListening();
    }
}
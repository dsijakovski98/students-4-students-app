package com.example.students4students.user_homepage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.students4students.CourseDialogFragment;
import com.example.students4students.Firebase;
import com.example.students4students.R;
import com.example.students4students.adapters.FindCoursesAdapter;
import com.example.students4students.collections.Course;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class FindCoursesFragment extends Fragment {

    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    CollectionReference coursesRef = firestore.collection(Firebase.COURSE_COLLECTION);

    private FindCoursesAdapter courseAdapter;

    public FindCoursesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_find_courses, container, false);
    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        Query query = coursesRef.orderBy(Firebase.COURSE_COLLECTION_NAME, Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Course> options = new FirestoreRecyclerOptions.Builder<Course>()
                .setQuery(query, Course.class).build();

        courseAdapter = new FindCoursesAdapter(options, getActivity());

        RecyclerView recyclerView = getActivity().findViewById(R.id.course_list_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(courseAdapter);

        // Open course dialog
        courseAdapter.setOnItemClickListener((snapshot, position) -> {
            CourseDialogFragment courseDialogFragment =
                    new CourseDialogFragment(snapshot, CourseDialogFragment.ACTION_NONE);
            courseDialogFragment.show(getActivity().getSupportFragmentManager(), "Course Dialog Fragment");
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        courseAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        courseAdapter.stopListening();
    }
}
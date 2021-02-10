package com.example.students4students;

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.students4students.collections.Course;
import com.example.students4students.collections.UserCourses;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CourseDialogFragment extends DialogFragment {
    public static final int ACTION_SUBSCRIBE = 1;
    public static final int ACTION_UNSUBSCRIBE = -1;
    public static final int ACTION_NONE = 0;
    public static final int ACTION_NO_BUTTON = 2;

    private Course course;
    private ProgressBar buttonProgressBar;
    private ProgressBar actionProgressBar;

    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    DocumentSnapshot snapshot;
    int action;

    public CourseDialogFragment(DocumentSnapshot snapshot, int action) {
        this.snapshot = snapshot;
        this.action = action;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_course_dialog, container, false);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        course = snapshot.toObject(Course.class);
        buttonProgressBar = getDialog().findViewById(R.id.progressBarCourse);
        actionProgressBar = getDialog().findViewById(R.id.actionProgressBar);

        TextView courseName = getDialog().findViewById(R.id.courseDialog_courseName);
        TextView courseMajor = getDialog().findViewById(R.id.courseDialog_courseMajor);
        TextView courseDescription = getDialog().findViewById(R.id.courseDialog_courseDescription);

        ImageView coursePicture = getDialog().findViewById(R.id.courseDialog_coursePicture);

        ImageView closeDialogImageBtn = getDialog().findViewById(R.id.courseDialog_closeBtn);

        courseName.setText(course.getName());
        courseMajor.setText(course.getMajor());
        courseDescription.setText(course.getDescription());

        String picUrlString = course.getPictureUrl();
        if(!picUrlString.isEmpty()) {
            Picasso.with(getActivity()).load(Uri.parse(picUrlString)).into(coursePicture);
        }

        closeDialogImageBtn.setOnClickListener(v -> getDialog().dismiss());

        if(action == ACTION_NO_BUTTON) {
            Button actionButton = getDialog().findViewById(R.id.courseDialog_actionButton);
            actionButton.setVisibility(View.GONE);
        }

        if(action == ACTION_NONE) {
            // Check if user has subscribed to this course
            setSubscribeAction();
        }
        else if(action != ACTION_NO_BUTTON) {
            getSetActionButton();
        }


    }

    private void setSubscribeAction() {
        buttonProgressBar.setVisibility(View.VISIBLE);
        String courseId = course.getId();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        CollectionReference userCoursesRef =
                FirebaseFirestore.getInstance().collection(Firebase.USER_COURSES_COLLECTION);

        userCoursesRef.document(userId).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                if(task.getResult() != null) {
                    UserCourses userCourses = task.getResult().toObject(UserCourses.class);
                    if(userCourses != null) {
                        List<String> courses = userCourses.getCourses();
                        if(courses.contains(courseId)) {
                            this.action = ACTION_UNSUBSCRIBE;
                        }
                        else {
                            this.action = ACTION_SUBSCRIBE;
                        }

                        getSetActionButton();
                    }
                }
            }
            else {
                // TODO: Get user_courses database error handling
                Toast.makeText(getActivity(), "Error in getting user courses!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getSetActionButton() {
        buttonProgressBar.setVisibility(View.GONE);
        Button actionButton = getDialog().findViewById(R.id.courseDialog_actionButton);

        switch (action) {
            case ACTION_SUBSCRIBE:
                actionButton.setText(R.string.subscribe_button);
                break;
            case ACTION_UNSUBSCRIBE:
                actionButton.setText(R.string.unsubscribe_button);
                break;
        }

        actionButton.setOnClickListener(v -> {
            switch (action) {
                case ACTION_SUBSCRIBE:
                    subscribeToCourse();
                    break;
                case ACTION_UNSUBSCRIBE:
                    unsubscribeFromCourse();
                    break;
            }
        });
    }

    private void unsubscribeFromCourse() {
        actionProgressBar.setVisibility(View.VISIBLE);

        String uid = currentUser.getUid();
        String courseId = course.getId();
        String courseName = course.getName();

        CollectionReference userCoursesRef =
                FirebaseFirestore.getInstance().collection(Firebase.USER_COURSES_COLLECTION);

        userCoursesRef.document(uid).update(Firebase.COURSE_COLLECTION, FieldValue.arrayRemove(courseId))
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        actionProgressBar.setVisibility(View.GONE);
                        Toast.makeText(getActivity(),
                                "Unsubscribed from " + courseName, Toast.LENGTH_SHORT).show();
                        getDialog().dismiss();
                    }
                    else {
                        // TODO: Course subscription error handling
                        Toast.makeText(getActivity(), "Error on unsubscribe!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void subscribeToCourse() {
        actionProgressBar.setVisibility(View.VISIBLE);

        String uid = currentUser.getUid();
        String courseId = course.getId();
        String courseName = course.getName();

        CollectionReference userCoursesRef =
                FirebaseFirestore.getInstance().collection(Firebase.USER_COURSES_COLLECTION);

        userCoursesRef.document(uid).update(Firebase.COURSE_COLLECTION, FieldValue.arrayUnion(courseId))
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        actionProgressBar.setVisibility(View.GONE);
                        Toast.makeText(getActivity(),
                                "Subscribed to " + courseName, Toast.LENGTH_SHORT).show();
                        getDialog().dismiss();
                    }
                    else {
                        // TODO: Course subscription error handling
                        Toast.makeText(getActivity(), "Error on subscribe!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
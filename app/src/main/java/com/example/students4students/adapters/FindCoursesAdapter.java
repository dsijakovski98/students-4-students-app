package com.example.students4students.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.students4students.R;
import com.example.students4students.collections.Course;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

public class FindCoursesAdapter extends FirestoreRecyclerAdapter<Course, FindCoursesAdapter.FindCoursesHolder> {

    private final Context context;
    private ItemClickListener listener;

    public FindCoursesAdapter(@NonNull FirestoreRecyclerOptions<Course> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull FindCoursesHolder holder, int position, @NonNull Course model) {
        holder.courseName.setText(model.getName());
        holder.courseMajor.setText(model.getMajor());

        String picUrlString = model.getPictureUrl();
        if(!picUrlString.isEmpty()) {
            Uri picUrl = Uri.parse(model.getPictureUrl());
            Picasso.with(context).load(picUrl).noFade().into(holder.coursePic);
        }
    }

    @NonNull
    @Override
    public FindCoursesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.course_list_item,
                parent, false);

        return new FindCoursesHolder(view);
    }

    class FindCoursesHolder extends RecyclerView.ViewHolder {

        ImageView coursePic;
        ImageView openCourseBtn;

        TextView courseName;
        TextView courseMajor;

        public FindCoursesHolder(@NonNull View itemView) {
            super(itemView);

            coursePic = itemView.findViewById(R.id.fc_coursePicture);
            courseName = itemView.findViewById(R.id.fc_courseListItemName);
            courseMajor = itemView.findViewById(R.id.fc_courseListItemMajor);

            openCourseBtn = itemView.findViewById(R.id.fc_openCourseDetailsButton);
            openCourseBtn.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if(position != RecyclerView.NO_POSITION) {
                    // Open course details
                    listener.onItemClick(getSnapshots().getSnapshot(position), position);
                }
            });
        }
    }

    public interface ItemClickListener {
        void onItemClick(DocumentSnapshot snapshot, int position);
    }

    public void setOnItemClickListener(ItemClickListener listener) {
        this.listener = listener;
    }
}

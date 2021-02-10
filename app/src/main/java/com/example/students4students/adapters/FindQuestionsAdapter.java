package com.example.students4students.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.students4students.R;
import com.example.students4students.collections.Question;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindQuestionsAdapter extends FirestoreRecyclerAdapter<Question, FindQuestionsAdapter.FindQuestionsHolder> {

    private Context context;
    private ItemClickListener listener;


    public FindQuestionsAdapter(@NonNull FirestoreRecyclerOptions<Question> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull FindQuestionsHolder holder, int position, @NonNull Question model) {
        holder.questionTitle.setText(model.getTitle());
        holder.creatorUsername.setText(model.getCreatorNickname());

        Timestamp timestamp = model.getTimestamp();
        Date date = timestamp.toDate();

        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM, yyyy hh:mm", Locale.getDefault());
        String dateString = formatter.format(date);

        holder.questionTimestamp.setText(dateString);

        holder.questionTopic.setText(model.getTopicName());
        holder.questionContent.setText(model.getContent());

        String creatorProfileUrl = model.getCreatorProfileUrl();
        if(!creatorProfileUrl.isEmpty()) {
            Uri picUrl = Uri.parse(creatorProfileUrl);
            Picasso.with(context).load(picUrl).noFade().into(holder.creatorPicture);
        }

        // Check if question is user's question - change border color ?
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = currentUser.getUid();
        if(uid.equals(model.getCreatorId())) {
            holder.creatorPicture.setBorderColor(ContextCompat.getColor(context, R.color.teal_900));
        }
    }


    @NonNull
    @Override
    public FindQuestionsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.question_item,
                parent, false);

        return new FindQuestionsHolder(view);
    }



    class FindQuestionsHolder extends RecyclerView.ViewHolder {
        TextView questionTitle;
        TextView creatorUsername;
        TextView questionTimestamp;
        TextView questionTopic;
        TextView questionContent;

        CircleImageView creatorPicture;
        ImageView openThreadBtn;

        public FindQuestionsHolder(@NonNull View itemView) {
            super(itemView);

            questionTitle = itemView.findViewById(R.id.questionItemTitle);
            creatorUsername = itemView.findViewById(R.id.questionItemUsername);
            questionTimestamp = itemView.findViewById(R.id.questionItemTimestamp);
            questionTopic = itemView.findViewById(R.id.questionItemTopicName);
            questionContent = itemView.findViewById(R.id.questionItemContent);

            creatorPicture = itemView.findViewById(R.id.questionItemUserPicture);

            openThreadBtn = itemView.findViewById(R.id.questionItemOpenThreadButton);
            openThreadBtn.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if(position != RecyclerView.NO_POSITION) {
                    // Open question thread click listener
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

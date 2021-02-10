package com.example.students4students.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.students4students.R;
import com.example.students4students.collections.Comment;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends FirestoreRecyclerAdapter<Comment, CommentAdapter.CommentHolder> {

    private Context context;

    public CommentAdapter(@NonNull FirestoreRecyclerOptions<Comment> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull CommentHolder holder, int position, @NonNull Comment model) {
        holder.creatorNickname.setText(model.getCreatorNickname());

        Timestamp timestamp = model.getTimestamp();
        Date date = timestamp.toDate();

        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM, yyyy hh:mm", Locale.getDefault());
        String dateString = formatter.format(date);

        holder.commentTimestamp.setText(dateString);

        holder.content.setText(model.getContent());

        String creatorProfileUrl = model.getCreatorProfileUrl();
        if(!creatorProfileUrl.isEmpty()) {
            Uri picUrl = Uri.parse(creatorProfileUrl);
            Picasso.with(context).load(picUrl).noFade().into(holder.creatorPicture);
        }

        // Check if question is user's comment - change border color ?
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = currentUser.getUid();
        if(uid.equals(model.getCreatorId())) {
            holder.creatorPicture.setBorderColor(ContextCompat.getColor(context, R.color.teal_900));
        }

    }

    @NonNull
    @Override
    public CommentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.comment_item,
                parent, false);

        return new CommentHolder(view);
    }

    class CommentHolder extends RecyclerView.ViewHolder {

        TextView creatorNickname;
        TextView commentTimestamp;
        TextView content;

        CircleImageView creatorPicture;

        public CommentHolder(@NonNull View itemView) {
            super(itemView);

            creatorNickname = itemView.findViewById(R.id.commentItemUsername);
            commentTimestamp = itemView.findViewById(R.id.commentItemTimestamp);
            content = itemView.findViewById(R.id.commentItemContent);

            creatorPicture = itemView.findViewById(R.id.commentItemUserPicture);
        }
    }

}

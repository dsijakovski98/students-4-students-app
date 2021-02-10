package com.example.students4students;

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.students4students.collections.Comment;
import com.example.students4students.collections.Question;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentDialogFragment extends DialogFragment {

    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    CollectionReference commentsRef = firestore.collection(Firebase.COMMENT_COLLECTION);

    private Question question;
    
    private TextView commenterUsername;
    private CircleImageView commenterProfilePicture;
    
    private EditText commentInput;
    
    private Button commentButton;
    private ImageView closeDialogButton;

    private ProgressBar progressBar;

    public CommentDialogFragment() {
        // Required empty public constructor
    }

    public CommentDialogFragment(Question question) {
        this.question = question;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_comment_dialog, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        commenterUsername = getDialog().findViewById(R.id.commentDialog_username);
        commenterProfilePicture = getDialog().findViewById(R.id.commentDialog_profilePic);
        
        commentInput = getDialog().findViewById(R.id.commentDialog_commentInput);
        
        closeDialogButton = getDialog().findViewById(R.id.commentDialog_closeButton);
        closeDialogButton.setOnClickListener(v -> getDialog().dismiss());

        progressBar = getDialog().findViewById(R.id.commentDialog_progressBar);
        
        commentButton = getDialog().findViewById(R.id.commentDialog_commentButton);
        commentButton.setOnClickListener(v -> {
            if(validateCommentInput())
                addComment();
        });

        commenterUsername.setText(currentUser.getDisplayName());
        Uri currentUserUri = currentUser.getPhotoUrl();
        if(currentUserUri != null) {
            Picasso.with(getActivity()).load(currentUserUri).noFade().into(commenterProfilePicture);
        }

    }

    private boolean validateCommentInput() {
        String comment = commentInput.getText().toString();
        
        // Empty field
        if(comment.trim().isEmpty())
            return setErrorMessage(commentInput, "Enter comment!");
        
        // Pretty much valid comment
        return true;
    }

    private void addComment() {
        progressBar.setVisibility(View.VISIBLE);

        String id = UUID.randomUUID().toString();
        String content = commentInput.getText().toString();

        Timestamp timestamp = Timestamp.now();

        String questionId = question.getId();

        String creatorId = currentUser.getUid();
        String creatorNickname = currentUser.getDisplayName();

        Uri currentUserProfileUri = currentUser.getPhotoUrl();
        String creatorProfileUrl = "";
        if(currentUserProfileUri != null) {
            creatorProfileUrl = currentUserProfileUri.toString();
        }

        Comment comment = new Comment(id, content, timestamp,
                questionId,
                creatorId, creatorNickname, creatorProfileUrl);

        commentsRef.document(id).set(comment)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if(task.isSuccessful()) {
                        // Comment added successfully
                        Toast.makeText(getActivity(), "Commented successfully!", Toast.LENGTH_SHORT).show();
                        getDialog().dismiss();
                    }
                    else {
                        // TODO: Add comment error handling
                        Toast.makeText(getActivity(), "Add comment error!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean setErrorMessage(EditText input, String errorMessage) {
        input.setError(errorMessage);
        input.requestFocus();
        
        return false;
    }
}
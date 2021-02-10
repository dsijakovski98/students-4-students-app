package com.example.students4students.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.students4students.R;
import com.example.students4students.collections.Question;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MyQuestionsAdapter extends FirestoreRecyclerAdapter<Question, MyQuestionsAdapter.MyQuestionsHolder> {

    private final Context context;
    private ItemClickListener listener;

    public MyQuestionsAdapter(@NonNull FirestoreRecyclerOptions<Question> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull MyQuestionsHolder holder, int position, @NonNull Question model) {
        holder.myQuestionTitle.setText(model.getTitle());
        holder.myQuestionTopic.setText(model.getTopicName());
        holder.myQuestionContent.setText(model.getContent());

        Timestamp timestamp = model.getTimestamp();
        Date date = timestamp.toDate();

        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM, yyyy hh:mm", Locale.getDefault());
        String dateString = formatter.format(date);

        holder.myQuestionDate.setText(dateString);
    }

    public void deleteItem(int position) {
        getSnapshots().getSnapshot(position).getReference().delete();
    }

    @NonNull
    @Override
    public MyQuestionsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.my_question_item,
                parent, false);

        return new MyQuestionsHolder(view);
    }

    class MyQuestionsHolder extends RecyclerView.ViewHolder {

        TextView myQuestionTitle;
        TextView myQuestionTopic;
        TextView myQuestionContent;
        TextView myQuestionDate;

        Button myQuestionOpenThreadBtn;

        public MyQuestionsHolder(@NonNull View itemView) {
            super(itemView);

            myQuestionTitle = itemView.findViewById(R.id.myQuestionItemTitle);
            myQuestionTopic = itemView.findViewById(R.id.myQuestionItemTopic);
            myQuestionContent = itemView.findViewById(R.id.myQuestionItemContent);
            myQuestionDate = itemView.findViewById(R.id.myQuestionItemTimestamp);

            myQuestionOpenThreadBtn = itemView.findViewById(R.id.myQuestionItemOpenThread);
            myQuestionOpenThreadBtn.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if(position != RecyclerView.NO_POSITION) {
                    // Open question thread
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

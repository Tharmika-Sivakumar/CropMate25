package com.example.cropmate25;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.MyViewHolderFeedback> {
    private List<Feedback> feedbackList;

    public FeedbackAdapter(List<Feedback> feedbackList) {
        this.feedbackList = feedbackList;
    }

    @NonNull
    @Override
    public MyViewHolderFeedback onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_of_feedback, parent, false);
        return new MyViewHolderFeedback(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolderFeedback holder, int position) {
        Feedback feedback = feedbackList.get(position);
        holder.feedback.setText(feedback.getFeedback());
        holder.whenPost.setText("Posted: " + feedback.getTimeStamp());
    }

    @Override
    public int getItemCount() {
        return feedbackList.size();
    }

    static class MyViewHolderFeedback extends RecyclerView.ViewHolder {
        TextView feedback, whenPost;
        public MyViewHolderFeedback(@NonNull View itemView) {
            super(itemView);
            feedback = itemView.findViewById(R.id.feedback_past);
            whenPost = itemView.findViewById(R.id.whenpost);
        }
    }
}

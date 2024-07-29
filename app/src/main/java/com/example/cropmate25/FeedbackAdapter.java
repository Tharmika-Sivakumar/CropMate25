package com.example.cropmate25;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.MyViewHolderFeedback> {

    @NonNull
    @Override
    public MyViewHolderFeedback onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_of_feedback, parent, false);
        return new MyViewHolderFeedback(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolderFeedback holder, int position) {
        // Bind data to your ViewHolder here
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    static class MyViewHolderFeedback extends RecyclerView.ViewHolder {
        TextView feedback, whoPost;
        public MyViewHolderFeedback(@NonNull View itemView) {
            super(itemView);
            feedback = itemView.findViewById(R.id.feedback_past);
            whoPost = itemView.findViewById(R.id.whopost);
        }
    }
}

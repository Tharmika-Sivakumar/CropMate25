package com.example.cropmate25;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.List;

public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.MyViewHolderFeedback> {
    private static final String TAG = "FeedbackAdapter";
    private List<Feedback> feedbackList;
    private Context context;

    public FeedbackAdapter(Context context, List<Feedback> feedbackList) {
        this.context = context;
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

        holder.itemView.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View dialogView = LayoutInflater.from(context).inflate(R.layout.delete_feedback_confirm, null);
            Button deleteButton = dialogView.findViewById(R.id.delete);
            Button cancelButton = dialogView.findViewById(R.id.btnCancel);

            builder.setView(dialogView);
            AlertDialog dialog = builder.create();

            deleteButton.setOnClickListener(view -> {
                deleteFeedback(feedback);
                dialog.dismiss();
            });
            cancelButton.setOnClickListener(view -> dialog.dismiss());

            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }

            dialog.show();
        });
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

    private void deleteFeedback(Feedback feedback) {
        FirebaseManager firebaseManager = FirebaseManager.getInstance(TAG);
        FirebaseFirestore database = firebaseManager.getDatabase();

        database.collection("Feedback")
                .document(UserData.getId())
                .collection("UserFeedback")
                .whereEqualTo("When Submitted", feedback.getTimeStamp())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            document.getReference().delete()
                                    .addOnSuccessListener(aVoid -> {
                                        Log.e(TAG, "Feedback Deleted");
                                        feedbackList.remove(feedback);
                                        notifyDataSetChanged();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(context, "Failed to delete feedback", Toast.LENGTH_SHORT).show();
                                        Log.e(TAG, "Failed to delete feedback: " + e.getMessage());
                                    });
                        }
                    } else {
                        Toast.makeText(context, "Failed to find feedback", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Failed to find feedback: " + task.getException().getMessage());
                    }
                });
    }
}

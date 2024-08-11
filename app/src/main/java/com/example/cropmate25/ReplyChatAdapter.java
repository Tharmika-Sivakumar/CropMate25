package com.example.cropmate25;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ReplyChatAdapter extends RecyclerView.Adapter<ReplyChatAdapter.MyViewHolderReplyChat>{
    private static final String TAG = "ReplyChatAdapter";
    private List<ReplyChat> replyList;
    private Context context;
    private FirebaseManager firebaseManager = FirebaseManager.getInstance(TAG);
    private FirebaseFirestore database = firebaseManager.getDatabase();

    public ReplyChatAdapter(Context context, List<ReplyChat> replyList) {
        this.context = context;
        this.replyList = replyList;
    }

    @NonNull
    @Override
    public ReplyChatAdapter.MyViewHolderReplyChat onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_of_reply, parent, false);
        return new ReplyChatAdapter.MyViewHolderReplyChat(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ReplyChatAdapter.MyViewHolderReplyChat holder, int position) {
        ReplyChat replyHistory = replyList.get(position);
        holder.message.setText(replyHistory.getChat());
        holder.whenPost.setText("Posted: " + replyHistory.getTimeStamp());
        holder.whoPost.setText("Posted by: " + replyHistory.getUserName());

        int ownThreadColor = Color.parseColor("#A2EDA5");
        int otherThreadColor = Color.parseColor("#D9B139");

        if (replyHistory.getUserId().equals(UserData.getId())) {
            holder.cardView.setCardBackgroundColor(ownThreadColor);
        }
        else {
            holder.cardView.setCardBackgroundColor(otherThreadColor);
        }

        holder.itemView.setOnLongClickListener(v -> {
            if (replyHistory.getUserId().equals(UserData.getId())) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                View dialogView = LayoutInflater.from(context).inflate(R.layout.delete_reply_confirm, null);
                Button deleteButton = dialogView.findViewById(R.id.delete);
                Button cancelButton = dialogView.findViewById(R.id.btnCancel);

                builder.setView(dialogView);
                AlertDialog dialog = builder.create();

                deleteButton.setOnClickListener(view -> {
                    deleteReply(replyHistory.getKey(), position, replyHistory.getThreadID(), replyHistory.getMessageID());
                    dialog.dismiss();
                });
                cancelButton.setOnClickListener(view -> dialog.dismiss());

                if (dialog.getWindow() != null) {
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                }

                dialog.show();
            }
            else {
                Toast.makeText(context, "You can only delete your own threads", Toast.LENGTH_SHORT).show();
            }
            return true;
        });


    }

    @Override
    public int getItemCount() {
        return replyList.size();
    }
    private void deleteReply(String key, int position, String documentId, String messageId) {
        database.collection("Threads")
                .document(documentId)
                .collection("Message")
                .document(messageId)
                .collection("Replies")
                .document(key)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Reply successfully deleted!");
                    replyList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, replyList.size());
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error deleting Reply", e);
                    Toast.makeText(context, "Error deleting Reply", Toast.LENGTH_SHORT).show();
                });
    }
    static class MyViewHolderReplyChat extends RecyclerView.ViewHolder {
        public CardView cardView;
        TextView message, whenPost, whoPost;

        public MyViewHolderReplyChat(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.message);
            whenPost = itemView.findViewById(R.id.whenpost);
            whoPost = itemView.findViewById(R.id.whopost);
            cardView = itemView.findViewById(R.id.recCard);
        }
    }
}

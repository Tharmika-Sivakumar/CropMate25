package com.example.cropmate25;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class forumDetailsAdapter extends RecyclerView.Adapter<forumDetailsAdapter.MyViewHolderForumDetails>{
    private static final String TAG = "forumDetailsAdapter";
    private List<ForumChat> chatList;
    private Context context;
    private FirebaseManager firebaseManager = FirebaseManager.getInstance(TAG);
    private FirebaseFirestore database = firebaseManager.getDatabase();

    public forumDetailsAdapter(Context context, List<ForumChat> chatList) {
        this.context = context;
        this.chatList = chatList;
    }

    @NonNull
    @Override
    public MyViewHolderForumDetails onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_of_chat, parent, false);
        return new forumDetailsAdapter.MyViewHolderForumDetails(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolderForumDetails holder, int position) {
        ForumChat chatHistory = chatList.get(position);
        holder.message.setText(chatHistory.getChat());
        holder.whenPost.setText("Posted: " + chatHistory.getTimeStamp());
        holder.whoPost.setText("Posted by: " + chatHistory.getUserName());

        int ownThreadColor = Color.parseColor("#A2EDA5");
        int otherThreadColor = Color.parseColor("#D9B139");

        if (chatHistory.getUserId().equals(UserData.getId())) {
            holder.cardView.setCardBackgroundColor(ownThreadColor);
        }
        else {
            holder.cardView.setCardBackgroundColor(otherThreadColor);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, viewReply.class);
            intent.putExtra("MESSAGE", chatHistory.getChat());
            intent.putExtra("WHO_POSTED", chatHistory.getUserName());
            intent.putExtra("WHEN_POSTED", chatHistory.getTimeStamp());
            intent.putExtra("THREAD_ID", chatHistory.getThreadID());
            intent.putExtra("MESSAGE_ID", chatHistory.getKey());
            context.startActivity(intent);
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (chatHistory.getUserId().equals(UserData.getId())) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                View dialogView = LayoutInflater.from(context).inflate(R.layout.delete_chat_confirm, null);
                Button deleteButton = dialogView.findViewById(R.id.delete);
                Button cancelButton = dialogView.findViewById(R.id.btnCancel);

                builder.setView(dialogView);
                AlertDialog dialog = builder.create();

                deleteButton.setOnClickListener(view -> {
                    deleteChat(chatHistory.getKey(), position, chatHistory.getThreadID());
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
        return chatList.size();
    }

    private void deleteChat(String key, int position, String documentId) {
        database.collection("Threads")
                .document(documentId)
                .collection("Message")
                .document(key)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Message successfully deleted!");
                    chatList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, chatList.size());
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error deleting Message", e);
                    Toast.makeText(context, "Error deleting Message", Toast.LENGTH_SHORT).show();
                });
    }

    static class MyViewHolderForumDetails extends RecyclerView.ViewHolder {
        public CardView cardView;
        TextView message, whenPost, whoPost;

        public MyViewHolderForumDetails(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.message);
            whenPost = itemView.findViewById(R.id.whenpost);
            whoPost = itemView.findViewById(R.id.whopost);
            cardView = itemView.findViewById(R.id.recCard);
        }
    }
}

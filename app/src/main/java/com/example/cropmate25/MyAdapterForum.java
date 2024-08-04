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

import java.util.ArrayList;
import java.util.List;

public class MyAdapterForum extends RecyclerView.Adapter<MyViewHolderForum> {
    private static final String TAG = "MyAdapterForum";
    private final Context context;
    private List<DataClassForum> dataList;
    public MyAdapterForum(Context context, List<DataClassForum> dataList) {
        this.context = context;
        this.dataList = dataList;
    }
    @NonNull
    @Override
    public MyViewHolderForum onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyler_view_forum, parent, false);
        return new MyViewHolderForum(view);

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolderForum holder, int position) {
        DataClassForum item = dataList.get(position);
        holder.threadTitle.setText(item.getTitle());
        holder.whenPost.setText("Posted: " + item.getTimeStamp());
        holder.whoPost.setText("Posted By: " + item.getUserName());

        int ownThreadColor = Color.parseColor("#A2EDA5");
        int otherThreadColor = Color.parseColor("#D9B139");

        if (item.getUserId().equals(UserData.getId())) {
            holder.cardView.setCardBackgroundColor(ownThreadColor);
        }
        else {
            holder.cardView.setCardBackgroundColor(otherThreadColor);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, forumDetails.class);
                intent.putExtra("Image", dataList.get(holder.getAdapterPosition()).getImage());
                intent.putExtra("Question", dataList.get(holder.getAdapterPosition()).getQuestion());
                intent.putExtra("Title", dataList.get(holder.getAdapterPosition()).getTitle());
                intent.putExtra("Name", dataList.get(holder.getAdapterPosition()).getUserName());
                intent.putExtra("ID", item.getUserId());
                intent.putExtra("whenPost", dataList.get(holder.getAdapterPosition()).getTimeStamp());
                intent.putExtra("key", dataList.get(holder.getAdapterPosition()).getKey());
                context.startActivity(intent);
            }
        });
        holder.itemView.setOnLongClickListener(v -> {
            if (item.getUserId().equals(UserData.getId())) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                View dialogView = LayoutInflater.from(context).inflate(R.layout.delete_thread_confirm, null);
                Button deleteButton = dialogView.findViewById(R.id.delete);
                Button cancelButton = dialogView.findViewById(R.id.btnCancel);

                builder.setView(dialogView);
                AlertDialog dialog = builder.create();

                deleteButton.setOnClickListener(view -> {
                    deleteThread(item.getKey(), position);
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
        return dataList.size();
    }
    @SuppressLint("NotifyDataSetChanged")
    public void searchDataList(ArrayList<DataClassForum> searchList){
        dataList = searchList;
        notifyDataSetChanged();
    }

    private void deleteThread(String threadId, int position) {
        FirebaseManager firebaseManager = FirebaseManager.getInstance(TAG);
        FirebaseFirestore database = firebaseManager.getDatabase();
        database.collection("Threads").document(threadId).delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Thread successfully deleted!");
                    dataList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, dataList.size());
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error deleting thread", e);
                    Toast.makeText(context, "Error deleting thread", Toast.LENGTH_SHORT).show();
                });
    }

}
class MyViewHolderForum extends RecyclerView.ViewHolder {
    TextView threadTitle;
    TextView whoPost;
    TextView whenPost;
    CardView cardView;

    public MyViewHolderForum(@NonNull View itemView) {
        super(itemView);
        threadTitle = itemView.findViewById(R.id.threadTitle);
        whoPost = itemView.findViewById(R.id.whopost);
        whenPost = itemView.findViewById(R.id.whenpost);
        cardView = itemView.findViewById(R.id.recCard);
    }
}
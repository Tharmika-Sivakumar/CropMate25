package com.example.cropmate25;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private EditText loginEmail, loginPassword;
    private Button loginButton;
    private TextView signupRedirectText;
    private TextView forgotRedirect;
    private FirebaseAuth auth;
    private FirebaseFirestore database;
    private FirebaseManager firebaseManager;

    private int attempt = 0;
    private final long WAIT_TIME_MS = 30000; // 30 seconds
    private final int maxAttempt = 3;
    private long waitStartTime; // Declare waitStartTime variable
    private Handler handler = new Handler();
    private AlertDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        showProgressDialog();
        auth = FirebaseAuth.getInstance();
        firebaseManager = FirebaseManager.getInstance(TAG);
        database = firebaseManager.getDatabase();
        hideProgressDialog();


        loginEmail = findViewById(R.id.login_email);
        loginPassword = findViewById(R.id.login_password);
        signupRedirectText = findViewById(R.id.signupredirect);
        loginButton = findViewById(R.id.login_button);
        forgotRedirect = findViewById(R.id.forgotpassword);

        loginButton.setOnClickListener(v -> {
            String email = loginEmail.getText().toString().trim();
            String password = loginPassword.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                loginEmail.setError("Email cannot be empty");
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                loginEmail.setError("Please enter a valid email address");
                return;
            }

            if (TextUtils.isEmpty(password)) {
                loginPassword.setError("Password cannot be empty");
                return;
            }

            showProgressDialog();

            auth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener(authResult -> {
                        FirebaseUser currentUser = auth.getCurrentUser();
                        if (currentUser != null) {
                            String userId = currentUser.getUid();
                            UserData.setId(userId);
                            getUserName(userId);
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        }
                        hideProgressDialog();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Login failed", e);
                        hideProgressDialog();
                    });
        });

        signupRedirectText.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, SignupActivity.class)));

        forgotRedirect.setOnClickListener(v -> {
            if (attempt >= maxAttempt) {
                long timeLeft = (WAIT_TIME_MS - (System.currentTimeMillis() - waitStartTime)) / 1000;
                if (timeLeft > 0) {
                    Toast.makeText(LoginActivity.this, "Please wait " + timeLeft + " seconds before trying again.", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    attempt = 0;
                }
            }
            showForgotPasswordDialog();
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            LayoutInflater inflater = getLayoutInflater();
            builder.setView(inflater.inflate(R.layout.progress_layout, null));

            progressDialog = builder.create();
        }
        progressDialog.show();
    }

    private void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void showForgotPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        View dialogView = getLayoutInflater().inflate(R.layout.activity_forgot_password, null);
        EditText emailBox = dialogView.findViewById(R.id.emailBox);

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        dialogView.findViewById(R.id.btnReset).setOnClickListener(v -> {
            String userEmail = emailBox.getText().toString().trim();

            if (TextUtils.isEmpty(userEmail)) {
                Toast.makeText(LoginActivity.this, "Enter your registered Email address", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
                Toast.makeText(LoginActivity.this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
                return;
            }

            showProgressDialog();


            database.collection("User")
                    .whereEqualTo("Email", userEmail)
                    .get()
                    .addOnCompleteListener(task -> {
                        hideProgressDialog();  // Hide the progress dialog when the task completes

                        if (task.isSuccessful()) {
                            if (!task.getResult().isEmpty()) {
                                auth.sendPasswordResetEmail(userEmail)
                                        .addOnCompleteListener(resetTask -> {
                                            if (resetTask.isSuccessful()) {
                                                Toast.makeText(LoginActivity.this, "Check your Email", Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();
                                            } else {
                                                Log.e(TAG, "Password reset email failed to send: " + resetTask.getException());
                                                Toast.makeText(LoginActivity.this, "Unable to send, failed", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            } else {
                                // Email does not exist in the database
                                attempt++;
                                if (attempt >= maxAttempt) {
                                    waitStartTime = System.currentTimeMillis();
                                    handler.postDelayed(() -> attempt = 0, WAIT_TIME_MS);
                                    Toast.makeText(LoginActivity.this, "Too many attempts. Please wait 30 seconds and try again.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(LoginActivity.this, "Email not registered. Please enter a registered email address.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "Unable to verify email, please try again", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "Error checking email existence: " + task.getException());
                        }
                    });
        });

        dialogView.findViewById(R.id.btnCancel).setOnClickListener(v -> dialog.dismiss());

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        dialog.show();
    }

    private void getUserName(String userId) {
        DocumentReference docRef = database.collection("User").document(userId);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String userName = document.getString("Name");
                    UserData.setName(userName);
                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "Get user name failed: ", task.getException());
            }
        });
    }
}

package com.example.cropmate25;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private EditText loginEmail, loginPassword;
    private Button loginButton;
    private TextView signupRedirectText;
    private TextView forgotRedirect;
    private FirebaseAuth auth;
    private FirebaseFirestore database;

    private int attempt = 0;
    private final long WAIT_TIME_MS = 30000; // 30 seconds
    private final int maxAttempt = 3;
    private long waitStartTime; // Declare waitStartTime variable
    private Handler handler = new Handler();
    String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();

        try { // Check the FirebaseApp with the name "secondary" already there or not, because of the error we faced
            FirebaseApp secondaryApp;
            try { // check it is there ot not
                secondaryApp = FirebaseApp.getInstance("secondary");
                Log.e(TAG, "FirebaseApp 'secondary' already exists");
            }
            catch (IllegalStateException e) { // if not initialized
                FirebaseOptions options = new FirebaseOptions.Builder()
                        .setProjectId("shoppingcart-e3525")
                        .setApplicationId("1:669624840785:android:70295f617f188094577e70")
                        .setApiKey("AIzaSyDUSi3BMEa8KlYD1BXTvzBEgqctU8SEH2o")
                        .build();
                secondaryApp = FirebaseApp.initializeApp(this, options, "secondary");
                Log.e(TAG, "FirebaseApp 'secondary' initialized" + e.getMessage());
            }
            database = FirebaseFirestore.getInstance(secondaryApp);
            Toast.makeText(this, "Firebase initialized Successfully", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e) {
            Log.e(TAG, "Firebase initialization error: " + e.getMessage());
            Toast.makeText(this, "Firebase initialization failed", Toast.LENGTH_SHORT).show();
        }


        loginEmail = findViewById(R.id.login_email);
        loginPassword = findViewById(R.id.login_password);
        signupRedirectText = findViewById(R.id.signupredirect);
        loginButton = findViewById(R.id.login_button);
        forgotRedirect = findViewById(R.id.forgotpassword);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = loginEmail.getText().toString();
                String password = loginPassword.getText().toString();

                if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    if (!password.isEmpty()) {
                        auth.signInWithEmailAndPassword(email, password)
                                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {
                                        FirebaseUser currentUser = auth.getCurrentUser();
                                        if (currentUser != null) {
                                            String userId = currentUser.getUid();
                                            Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                            intent.putExtra("userID", userId);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                                        Log.e(TAG, "Login failed", e);
                                    }
                                });
                    } else {
                        loginPassword.setError("Password cannot be empty");
                    }
                } else if (email.isEmpty()) {
                    loginEmail.setError("Email cannot be empty");
                } else {
                    loginEmail.setError("Please enter a valid email address");
                }
            }
        });

        signupRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            }
        });

        forgotRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (attempt >= maxAttempt) {
                    long timeLeft = (WAIT_TIME_MS - (System.currentTimeMillis() - waitStartTime)) / 1000;
                    if (timeLeft > 0) {
                        Toast.makeText(LoginActivity.this, "Please wait " + timeLeft + " seconds before trying again.", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        attempt = 0;
                    }
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                View dialogView = getLayoutInflater().inflate(R.layout.activity_forgot_password, null);
                EditText emailBox = dialogView.findViewById(R.id.emailBox);

                builder.setView(dialogView);
                AlertDialog dialog = builder.create();

                dialogView.findViewById(R.id.btnReset).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        userEmail = emailBox.getText().toString();

                        if (TextUtils.isEmpty(userEmail)) {
                            Toast.makeText(LoginActivity.this, "Enter your registered Email address", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
                            Toast.makeText(LoginActivity.this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Check if the email exists in the Firestore database
                        database.collection("User")
                                .whereEqualTo("Email", userEmail)
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            if (!task.getResult().isEmpty()) {
                                                auth.sendPasswordResetEmail(userEmail)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    Toast.makeText(LoginActivity.this, "Check your Email", Toast.LENGTH_SHORT).show();
                                                                    dialog.dismiss();
                                                                } else {
                                                                    Log.e(TAG, "Password reset email failed to send: " + task.getException());
                                                                    Toast.makeText(LoginActivity.this, "Unable to send, failed", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                            } else {
                                                // Email does not exist in the database
                                                attempt++;
                                                if (attempt >= maxAttempt) {
                                                    waitStartTime = System.currentTimeMillis();
                                                    handler.postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            attempt = 0;
                                                        }
                                                    }, WAIT_TIME_MS);
                                                    Toast.makeText(LoginActivity.this, "Too many attempts. Please wait 30 seconds and try again.", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(LoginActivity.this, "Email not registered. Please enter a registered email address.", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        } else {
                                            Toast.makeText(LoginActivity.this, "Unable to verify email, please try again", Toast.LENGTH_SHORT).show();
                                            Log.e(TAG, "Error checking email existence: " + task.getException());
                                        }
                                    }
                                });
                    }
                });

                dialogView.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                if (dialog.getWindow() != null) {
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                }

                dialog.show();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}

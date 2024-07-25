package com.example.cropmate25;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;  // need to transfer
    private EditText signupName, signupEmail, signupPassword;
    private Button signupBtn;
    private TextView LoginRedirectText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);

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
            firestore = FirebaseFirestore.getInstance(secondaryApp);
            Toast.makeText(this, "Firebase initialized Successfully", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e) {
            Log.e(TAG, "Firebase initialization error: " + e.getMessage());
            Toast.makeText(this, "Firebase initialization failed", Toast.LENGTH_SHORT).show();
        }

        signupName = findViewById(R.id.signup_name);
        signupEmail = findViewById(R.id.signup_email);
        signupPassword = findViewById(R.id.signup_password);
        signupBtn = findViewById(R.id.signup_button);
        LoginRedirectText = findViewById(R.id.loginredirect);

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = signupName.getText().toString().trim();
                String email = signupEmail.getText().toString().trim();
                String password = signupPassword.getText().toString().trim();

                if (email.isEmpty()) {
                    signupEmail.setError("Email can not be empty");
                    return;
                }
                if (password.isEmpty()) {
                    signupPassword.setError("Password can not be empty");
                    return;
                }

                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser currentUser = auth.getCurrentUser();
                            if (currentUser != null) {
                                String userId = currentUser.getUid();

                                Map<String, Object> data = new HashMap<>();
                                data.put("Name", name);
                                data.put("Email",email);

                                // Store user data in Fire store
                                firestore.collection("User").document(userId)
                                        .set(data)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(SignupActivity.this, "Data Stored Successfully", Toast.LENGTH_SHORT).show();
                                                }
                                                else {
                                                    Toast.makeText(SignupActivity.this, "Failed to store user data: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                                Toast.makeText(SignupActivity.this, "Signup successful", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                                intent.putExtra("userID", userId);
                                startActivity(intent);
                                finish();
                            }
                            else {
                                Toast.makeText(SignupActivity.this, "Failed to get user ID", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            Toast.makeText(SignupActivity.this, "SignUp Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        LoginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}

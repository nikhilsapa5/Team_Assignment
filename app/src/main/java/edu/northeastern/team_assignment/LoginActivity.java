package edu.northeastern.team_assignment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity{
    private EditText etLogin;
    private DatabaseReference dataRef;
    private ProgressBar loadingCircle;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        etLogin = findViewById(R.id.editTextLogin);
        loadingCircle = findViewById(R.id.loadCircle);
        findViewById(R.id.buttonLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
    }

    private void login() {
        String name = etLogin.getText().toString();
        if (!TextUtils.isEmpty(name)) {
            loadingCircle.setVisibility(View.VISIBLE);
            FirebaseDatabase.getInstance().getReference().child("User").child("u1").child(name).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    if (user != null&&!TextUtils.isEmpty(user.getUserName())) {
                        SharedPreferences sp = getSharedPreferences("User", MODE_PRIVATE);
                        sp.edit().putString("name",name).apply();
                        startActivity(new Intent(LoginActivity.this, edu.northeastern.team_assignment.HomeActivity.class));
                    }
                    loadingCircle.setVisibility(View.GONE);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}

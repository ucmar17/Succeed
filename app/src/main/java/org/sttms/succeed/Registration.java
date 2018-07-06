package org.sttms.succeed;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.flags.impl.DataUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Registration extends AppCompatActivity {

    private EditText firstEditText, lastEditText, emailEditText, passwordEditText;
    private Button register;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    Intent intent = new Intent(Registration.this, FindPlaces.class);
                    intent.putExtra("Role", name);
                    intent.putExtra("First", getIntent().getStringExtra("First"));
                    intent.putExtra("Last", getIntent().getStringExtra("Last"));
                    startActivity(intent);
                    finish();
                }
            }
        };

        name = getIntent().getExtras().getString("Role");
        Toast.makeText(this, name, Toast.LENGTH_SHORT).show();

        firstEditText = findViewById(R.id.first_name);
        lastEditText = findViewById(R.id.last_name);
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        register = findViewById(R.id.register);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = emailEditText.getText().toString();
                final String pass = passwordEditText.getText().toString();
                final String first = firstEditText.getText().toString();
                final String last = lastEditText.getText().toString();
                if (email.equals("") || pass.equals("") || first.equals("") || last.equals("")) {
                    Toast.makeText(Registration.this, "All Fields Must be Filled In", Toast.LENGTH_SHORT).show();
                } else {
                    mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(Registration.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(Registration.this, "Error signing up", Toast.LENGTH_SHORT).show();
                            } else {
                                String userId = mAuth.getCurrentUser().getUid();
                                DatabaseReference currentDB = FirebaseDatabase.getInstance().getReference().child("Users").child(name).child(userId);
                                DatabaseReference fName = currentDB.child("First");
                                DatabaseReference lName = currentDB.child("Last");
                                currentDB.setValue(true);
                                fName.setValue(first);
                                lName.setValue(last);
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onStart(){
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthListener);
    }

    @Override
    public void onStop(){
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthListener);
    }
}
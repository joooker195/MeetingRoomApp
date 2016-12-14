package com.meetingroom;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    //класс для аутентификации

    private EditText mPasswordText;
    private EditText mEmailText;
    private Button mOkButton;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ProgressBar mProgressBar;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPasswordText = (EditText) findViewById(R.id.edit_password);
        mEmailText = (EditText) findViewById(R.id.edit_email);
        mOkButton = (Button) findViewById(R.id.ok_button);
        mAuth = FirebaseAuth.getInstance();
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);



        //если аутентификация прошла, заходим в программу и не выходим от туда
        mAuthListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() != null)
                {
                    Intent intent= new Intent(MainActivity.this, MeetingListActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                    finish();
                }
            }
        };


        mOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSignIn();

            }
        });
    }

    @Override
    protected void onStart() {

        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthListener);
    }

    private void startSignIn() {

        //проверка аутентификации
        mProgressBar.setVisibility(ProgressBar.VISIBLE);
        String email = mEmailText.getText().toString();
        String password = mPasswordText.getText().toString();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(MainActivity.this, "Заполнены не все поля", Toast.LENGTH_LONG).show();
            mProgressBar.setVisibility(ProgressBar.INVISIBLE);
        }
        else
        {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "Аутентификация не пройдена", Toast.LENGTH_LONG).show();
                        mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                    }
                }
            });
        }
    }
}

package com.luitmed.prashantimedicos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.luitmed.prashantimedicos.Models.Common;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserAccount extends AppCompatActivity {

    TextView phoneTv;
    EditText nameTv, emailTv;
    String name, email, phone;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;
    Button update;
    Toolbar toolbar;
    public TextView cqTv;
    ImageButton back;
    private RoomDB database;
    BottomNavigationView bottomNavigationView;
    private int firstTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_account);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        firstTime = getIntent().getIntExtra("firstTime", 0);

        toolbar = findViewById(R.id.aTBar);
        cqTv = toolbar.findViewById(R.id.tBarCq);
        back = toolbar.findViewById(R.id.tBarBack);
        phoneTv = findViewById(R.id.uAcPhone);
        nameTv = findViewById(R.id.uAcNAme);
        emailTv = findViewById(R.id.uAcEmail);
        update = findViewById(R.id.updateBtn);
        firebaseAuth = FirebaseAuth.getInstance();
        Common.currentUser = firebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        database = RoomDB.getInstance(this);

        int size = database.mainDao().getAll().size();
        if(size > 0){
            cqTv.setText("" + size);
            cqTv.setVisibility(View.VISIBLE);
        }

        getUserData();

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserData();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void updateUserData() {

        if(firstTime == 1){
            // validation part
            String fullName = nameTv.getText().toString().trim();
            String email = emailTv.getText().toString().trim();

            if(TextUtils.isEmpty(fullName)) {
                Toast.makeText(this, "Entering name is mandatory", Toast.LENGTH_SHORT).show();
                return;
            }

            if(TextUtils.isEmpty(email)){
                Toast.makeText(this, "Entering email is mandatory", Toast.LENGTH_SHORT).show();
                return;
            }
            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                Toast.makeText(this, "Invalid email pattern, retry!", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        db.collection("users").document(firebaseAuth.getUid()).update("email", emailTv.getText().toString(), "name", nameTv.getText().toString())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if(firstTime == 1){
                            Intent intent = new Intent(getApplicationContext(), Dashboard.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                        Toast.makeText(UserAccount.this, "Users credentials updated", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void getUserData() {

        db.collection("users").document(firebaseAuth.getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        name = documentSnapshot.get("name").toString();
                        email = documentSnapshot.get("email").toString().trim();
                        phone = documentSnapshot.get("phone").toString();

                        phoneTv.setText(phone);
                        nameTv.setText(name);
                        emailTv.setText(email);
                    }
                });
    }
}
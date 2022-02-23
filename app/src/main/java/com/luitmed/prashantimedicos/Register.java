package com.luitmed.prashantimedicos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class Register extends AppCompatActivity{          // implements LocationListener required for location

    Button registerBtn;
    TextInputEditText phoneTV, addressTV, passwordTV, confirmPasswordTV;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    LinearLayout mainLayout;
    private ImageButton back;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.white));
        }


        registerBtn = (Button) findViewById(R.id.registerBtn);
        phoneTV = (TextInputEditText) findViewById(R.id.phoneNumber);
        mainLayout = findViewById(R.id.mainLayout);
        back = findViewById(R.id.rlBack);

        firebaseAuth = FirebaseAuth.getInstance();

        db = FirebaseFirestore.getInstance();

        mainLayout.animate().translationY(0).setDuration(400).setStartDelay(300);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputData();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private String fullName, phone, address, email, password, confirmPassword;
    private Boolean isRegistered = false;

    private void inputData() {
        phone = phoneTV.getText().toString().trim();


        if(TextUtils.isEmpty(phone)){
            Toast.makeText(this, "Entering phone number is mandatory", Toast.LENGTH_SHORT).show();
            return;
        }


        db.collection("users").whereEqualTo("phone", phone)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    List<DocumentSnapshot> docs = task.getResult().getDocuments();
                    if(docs.size() > 0){
                        isRegistered = true;
                    }
                }else{
                    Toast.makeText(Register.this, "" + task.getException(), Toast.LENGTH_SHORT).show();
                }
                Intent intent = new Intent(getApplicationContext(), Verification.class);
                intent.putExtra("phone", phone);
                intent.putExtra("isRegistered", isRegistered);
                startActivity(intent);
            }
        });

    }

}
package com.luitmed.prashantimedicos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class AddAddress extends AppCompatActivity {

    ImageButton backBtn;
    Button addAddress;
    TextInputEditText addressTv, pinCodeTv, nameTv, phoneTv;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        backBtn = findViewById(R.id.backBtn);
        addAddress = findViewById(R.id.addAddressBtn);
        addressTv  = findViewById(R.id.userAddress);
        pinCodeTv = findViewById(R.id.pinCode);
        nameTv = findViewById(R.id.adName);
        phoneTv = findViewById(R.id.adPhone);
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        addAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewAddress();
            }
        });

    }

    private void addNewAddress() {

        progressDialog.setMessage("Address is being added..");
        progressDialog.show();

        // auto generated address id
        String name = nameTv.getText().toString().trim();
        String phone = phoneTv.getText().toString().trim();
        String address = addressTv.getText().toString().trim();
        String pinCode = pinCodeTv.getText().toString().trim();
        String timestamp = String.valueOf(System.currentTimeMillis());
        String addressId = "A"+timestamp;

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("name", name);
        hashMap.put("phone", phone);
        hashMap.put("address", address);
        hashMap.put("pinCode", pinCode);
        hashMap.put("id", addressId);

        db.collection("users").document(firebaseAuth.getUid()).collection("addresses").document(addressId).set(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        progressDialog.dismiss();
                        Toast.makeText(AddAddress.this, "Added successfully..", Toast.LENGTH_SHORT).show();
                        db.collection("users").document(firebaseAuth.getUid()).update("address", "true");
                        Intent intent = new Intent(getApplicationContext(), Addresses.class);
                        startActivity(intent);

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(AddAddress.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }


}
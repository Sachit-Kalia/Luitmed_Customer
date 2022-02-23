package com.luitmed.prashantimedicos;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PrescriptionDetails extends AppCompatActivity {

    private String id = "", phone = "";
    private ImageButton back;
    private PhotoView photoView;
    private FirebaseFirestore db;
    private TextView nameTv, phoneTv, emailTv, dateTv, idTv;
    ShimmerFrameLayout shimmerFrameLayout;
    private ScrollView mainLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prescription_details);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        Intent intent = getIntent();
        id = intent.getStringExtra("id");

        back = findViewById(R.id.prdBackButton);
        photoView = findViewById(R.id.photoView);
        nameTv = findViewById(R.id.prdName);
        phoneTv = findViewById(R.id.prdPhone);
        emailTv = findViewById(R.id.prdEmail);
        dateTv = findViewById(R.id.prdDate);
        idTv = findViewById(R.id.prdID);
        db = FirebaseFirestore.getInstance();
        shimmerFrameLayout = findViewById(R.id.pdShimmer);
        mainLayout = findViewById(R.id.pdMainLayout);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        loadDetails();

    }

    private void loadDetails() {

        shimmerFrameLayout.startShimmerAnimation();

        db.collection("prescriptions").document(id).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        // set all details
                        String cid = documentSnapshot.get("cId").toString();
                        String date = documentSnapshot.get("timestamp").toString();
                        String image = documentSnapshot.get("image").toString();

                        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm aaa  dd/MM/yyyy");
                        String formattedDate = formatter.format(new Date(Long.parseLong(date)));

                        db.collection("users").document(cid).get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        String name = documentSnapshot.get("name").toString();
                                        phone = documentSnapshot.get("phone").toString();
                                        String email = documentSnapshot.get("email").toString();

                                        idTv.setText(id);
                                        nameTv.setText(name);
                                        phoneTv.setText(phone);
                                        emailTv.setText(email);
                                        dateTv.setText(formattedDate);

                                        try{
                                            Picasso.get().load(image).placeholder(R.color.light_gray).into(photoView);
                                        }catch (Exception e){
                                            photoView.setImageResource(R.drawable.splash_image);
                                        }
                                        shimmerFrameLayout.stopShimmerAnimation();
                                        mainLayout.setVisibility(View.VISIBLE);
                                        shimmerFrameLayout.setVisibility(View.GONE);
                                    }
                                });
                    }
                });
    }

}
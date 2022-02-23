package com.luitmed.prashantimedicos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.luitmed.prashantimedicos.Adapters.PrescriptionAdapter;
import com.luitmed.prashantimedicos.Models.Prescription;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Prescriptions extends AppCompatActivity {

    ImageButton backBtn;
    RecyclerView recyclerView;
    ArrayList<Prescription> prescriptionList;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;
    PrescriptionAdapter adapter;
    ShimmerFrameLayout shimmerFrameLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prescriptions);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        backBtn = findViewById(R.id.prBackButton);
        prescriptionList = new ArrayList<>();
        recyclerView = findViewById(R.id.prescriptionRV);
        shimmerFrameLayout = findViewById(R.id.ordersShimmerLayout);
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        loadOrders();
    }

    private void loadOrders() {

        shimmerFrameLayout.startShimmerAnimation();

        db.collection("prescriptions").whereEqualTo("cId", firebaseAuth.getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        prescriptionList.clear();
                        adapter = new PrescriptionAdapter(Prescriptions.this, prescriptionList);
                        recyclerView.setAdapter(adapter);

                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                            String oid = documentSnapshot.get("id").toString();
                            String oDate = documentSnapshot.get("timestamp").toString();

                            Prescription prescription = new Prescription(oid, oDate);
                            prescriptionList.add(0, prescription);
                            adapter.notifyDataSetChanged();
                        }

                        recyclerView.setVisibility(View.VISIBLE);
                        shimmerFrameLayout.setVisibility(View.GONE);
                        shimmerFrameLayout.stopShimmerAnimation();
                    }
                });
    }
}
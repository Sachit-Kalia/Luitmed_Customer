package com.luitmed.prashantimedicos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MyOrders extends AppCompatActivity {

    ImageButton backBtn;
    RecyclerView recyclerView;
    ArrayList<Order> orderList;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;
    String userId;
    OrdersAdapter adapter;
    ShimmerFrameLayout shimmerFrameLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        backBtn = findViewById(R.id.ordersBackButton);
        orderList = new ArrayList<>();
        recyclerView = findViewById(R.id.orderRV);
        shimmerFrameLayout = findViewById(R.id.ordersShimmerLayout);
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userId = firebaseAuth.getUid();

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

        CollectionReference collectionReference = db.collection("orders");

        collectionReference.whereEqualTo("orderBy", userId).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                orderList.clear();
                adapter = new OrdersAdapter(MyOrders.this, orderList);
                recyclerView.setAdapter(adapter);

                for(DocumentSnapshot documentSnapshot: queryDocumentSnapshots.getDocuments()){

                    String oid = documentSnapshot.get("orderId").toString();
                    String oStatus = documentSnapshot.get("orderStatus").toString();
                    String oCost = documentSnapshot.get("orderCost").toString();
                    String oDate = documentSnapshot.get("orderTime").toString();
                    String oTo = documentSnapshot.get("orderTo").toString();

                    Order order = new Order(oid, oDate, oStatus, oCost, oTo);
                    orderList.add(0, order);
                    adapter.notifyDataSetChanged();
                }


                recyclerView.setVisibility(View.VISIBLE);
                shimmerFrameLayout.setVisibility(View.GONE);
                shimmerFrameLayout.stopShimmerAnimation();
            }
        });


    }
}
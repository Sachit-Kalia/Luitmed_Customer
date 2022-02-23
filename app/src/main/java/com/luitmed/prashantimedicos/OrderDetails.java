package com.luitmed.prashantimedicos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class OrderDetails extends AppCompatActivity {

    Toolbar toolbar;
    ImageButton back, cart;
    String id, sId;
    TextView costTvf, statusTvf, dateTvf, addressTvf, idTVf;
    RecyclerView recyclerView;
    ArrayList<CartItem> orderList;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;
    OrderDetailsAdapter adapter;
    LinearLayout linearLayout;
    ShimmerFrameLayout shimmerFrameLayout;
    private Button cancelOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        toolbar = findViewById(R.id.toolbar1);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        sId = intent.getStringExtra("sellerId");

        back = toolbar.findViewById(R.id.tBarBack);
        cart = toolbar.findViewById(R.id.tBarCart);
        idTVf = findViewById(R.id.detailID);
        statusTvf = findViewById(R.id.detailStatus);
        costTvf = findViewById(R.id.detailCost);
        dateTvf = findViewById(R.id.detailDate);
        addressTvf = findViewById(R.id.detailAddress);
        cancelOrder = findViewById(R.id.cancelOrder);
        recyclerView = findViewById(R.id.orderItemRV);
        shimmerFrameLayout = findViewById(R.id.odShimmer);
        linearLayout = findViewById(R.id.odLayout);
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        orderList = new ArrayList<>();


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        cancelOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DocumentReference documentReference = db.collection("orders").document(id);

                documentReference.collection("items").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                          for(DocumentSnapshot documentSnapshot: queryDocumentSnapshots.getDocuments()){
                              String pid = documentSnapshot.get("pId").toString();
                              int num = Integer.parseInt(documentSnapshot.get("number").toString());

                              db.collection("products").document(pid).get()
                                      .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                          @Override
                                          public void onSuccess(DocumentSnapshot ds) {

                                              int stock = Integer.parseInt(ds.get("stock").toString()) + num;
                                              db.collection("products").document(pid).update("stock", "" + num);

                                          }
                                      }).addOnFailureListener(new OnFailureListener() {
                                  @Override
                                  public void onFailure(@NonNull Exception e) {
                                      Toast.makeText(OrderDetails.this, "Failed to cancel the order: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                  }
                              });
                          }
                        documentReference.update("orderStatus", "Cancelled")
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        statusTvf.setText("Cancelled");
                                        cancelOrder.setVisibility(View.GONE);
                                        Toast.makeText(OrderDetails.this, "Order cancelled successfully", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });
            }
        });

        loadOrderDetails();
    }

    String sellerId, image, cost, date, status, address, formattedDate;

    private void loadOrderDetails() {

        shimmerFrameLayout.startShimmerAnimation();


        db.collection("orders").document(id).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot ds) {

                        orderList.clear();

                        cost = ds.get("orderCost").toString();
                        date = ds.get("orderTime").toString();
                        status = ds.get("orderStatus").toString();
                        address = ds.get("Address").toString();

                        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm aaa  dd/MM/yyyy");
                        String formattedDate = formatter.format(new Date(Long.parseLong(date)));

                        costTvf.setText("₹" + cost);
                        dateTvf.setText(formattedDate);
                        statusTvf.setText(status);
                        addressTvf.setText(address);

                        if(status.equals("Delivered") || status.equals("Cancelled")){
                            cancelOrder.setVisibility(View.GONE);
                        }


                        db.collection("orders").document(id).collection("items").get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {

                                            String title = documentSnapshot.get("name").toString();
                                            String number = documentSnapshot.get("number").toString();
                                            String price = documentSnapshot.get("price").toString();
                                            String quantity = documentSnapshot.get("quantity").toString();
                                            String pid = documentSnapshot.get("pId").toString();


                                            db.collection("products").document(pid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                @Override
                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                    image = documentSnapshot.get("productIcon").toString();

                                                    CartItem cartItem = new CartItem("1", pid, title, price, price, quantity, number, image);
                                                    orderList.add(cartItem);
                                                    adapter = new OrderDetailsAdapter(OrderDetails.this, orderList);
                                                    recyclerView.setAdapter(adapter);
                                                }
                                            });
                                        }
                                        shimmerFrameLayout.stopShimmerAnimation();
                                        shimmerFrameLayout.setVisibility(View.GONE);
                                        linearLayout.setVisibility(View.VISIBLE);
                                    }
                                });

                    }
                });
    }

}
package com.luitmed.prashantimedicos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.luitmed.prashantimedicos.Models.Common;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Products extends AppCompatActivity {

    Toolbar toolbar;
    ImageButton back, cart, search;
    RelativeLayout filter;
    RecyclerView recyclerView;
    ArrayList<Product> productList;
    ProductsRecyclerViewAdapter adapter;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;
    String query = "";
    String searchQuery = "";
    String type = "";
    ShimmerFrameLayout shimmerFrameLayout;
    public TextView cqTv;
    private RoomDB database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        Intent intent = getIntent();

        type = intent.getStringExtra("type");
        query = intent.getStringExtra("query");
        searchQuery = intent.getStringExtra("searchQuery");

        toolbar = findViewById(R.id.toolbar2);
        cqTv = toolbar.findViewById(R.id.t2Quantity);
        back = toolbar.findViewById(R.id.t2BarBack);
        cart = findViewById(R.id.t2BarCart);


        filter = findViewById(R.id.cpFilter);
        recyclerView = findViewById(R.id.productsRV);
        productList = new ArrayList<>();
        shimmerFrameLayout = findViewById(R.id.productsShimmer);
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        database = RoomDB.getInstance(this);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Cart.class));
            }
        });

        int size = database.mainDao().getAll().size();

        if(size > 0){
            cqTv.setText("" + size);
            cqTv.setVisibility(View.VISIBLE);
        }


        // filter set on click listener

        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCategories();
            }
        });

        if(type == null){
            loadSearchProducts();
        }else{
            loadAllProducts(type);
        }

    }

    private void showCategories() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Product Category")
                .setItems(Common.productCategories1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String category = Common.productCategories1[which];
                        query = category;
                        loadAllProducts("category");
                    }
                }).show();


    }

    // where('name', '>=', queryText).where('name', '<=', queryText+ '\uf8ff')
    private void loadSearchProducts() {

        shimmerFrameLayout.startShimmerAnimation();

        db.collection("products").whereGreaterThanOrEqualTo("searchTitle", searchQuery).
                whereLessThanOrEqualTo("searchTitle", searchQuery + '\uf8ff').get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        productList.clear();

                        for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()){

                            String productID = documentSnapshot.get("productID").toString();
                            String title = documentSnapshot.get("title").toString();
                            String description = documentSnapshot.get("description").toString();
                            String category = documentSnapshot.get("category").toString();
                            String quantity = documentSnapshot.get("quantity").toString();
                            String productIcon = documentSnapshot.get("productIcon").toString();
                            String price = documentSnapshot.get("price").toString();
                            String discountedPrice = documentSnapshot.get("discountedPrice").toString();
                            String timestamp = documentSnapshot.get("timestamp").toString();
                            String uid = documentSnapshot.get("uid").toString();
                            String stock = documentSnapshot.get("stock").toString();

                            Product product = new Product(productID, title, description, category, quantity, productIcon, price, discountedPrice, timestamp, uid, stock);
                            productList.add(product);
                        }
                        adapter = new ProductsRecyclerViewAdapter(Products.this, productList);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setVisibility(View.VISIBLE);
                        shimmerFrameLayout.stopShimmerAnimation();
                        shimmerFrameLayout.setVisibility(View.GONE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Products.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void loadAllProducts(String type) {

        shimmerFrameLayout.startShimmerAnimation();

        db.collection("products").whereEqualTo(type, query).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        productList.clear();

                        for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()){

                            String productID = documentSnapshot.get("productID").toString();
                            String title = documentSnapshot.get("title").toString();
                            String description = documentSnapshot.get("description").toString();
                            String category = documentSnapshot.get("category").toString();
                            String quantity = documentSnapshot.get("quantity").toString();
                            String productIcon = documentSnapshot.get("productIcon").toString();
                            String price = documentSnapshot.get("price").toString();
                            String discountedPrice = documentSnapshot.get("discountedPrice").toString();
                            String timestamp = documentSnapshot.get("timestamp").toString();
                            String uid = documentSnapshot.get("uid").toString();
                            String stock = documentSnapshot.get("stock").toString();

                            Product product = new Product(productID, title, description, category, quantity, productIcon, price, discountedPrice, timestamp, uid, stock);
                            productList.add(product);
                        }
                        adapter = new ProductsRecyclerViewAdapter(Products.this, productList);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setVisibility(View.VISIBLE);
                        shimmerFrameLayout.stopShimmerAnimation();
                        shimmerFrameLayout.setVisibility(View.GONE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Products.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRestart() {
        super.onRestart();
        cqTv.setText("" + database.mainDao().getAll().size());
        if(cqTv.getText().toString().equals("0")){
            cqTv.setVisibility(View.GONE);
        }
    }


}
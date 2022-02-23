package com.luitmed.prashantimedicos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;



public class ProductDetails extends AppCompatActivity {

    Toolbar toolbar;
    String id;
    ImageButton back, cart, pdPlus, pdMinus;
    Button addToCart;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;
    ImageView image;
    TextView titleTV, quantityTV, dPriceTV, priceTV, descTV, discountTV, pdNum;
    ShimmerFrameLayout shimmerFrameLayout;
    LinearLayout productLayout;
    RelativeLayout adjustPanel;
    RecyclerView recyclerView;
    private HomeRecyclerViewAdapter adapter;
    private ArrayList<Product> productList;
    private List<MainData> dataList;
    Product product;
    private RoomDB database;
    public TextView cqTv;

    String productID, productIcon, price, discountedPrice, stock;
    private double cost = 0;
    private double finalCost = 0;
    private int quantity = 0;
    boolean isAdded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        toolbar = findViewById(R.id.toolbar1);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");

        back = toolbar.findViewById(R.id.tBarBack);
        cart = toolbar.findViewById(R.id.tBarCart);
        cqTv = toolbar.findViewById(R.id.tBarCq);

        addToCart = findViewById(R.id.productAddToCart);
        recyclerView = findViewById(R.id.productDetailsRV);

        titleTV = findViewById(R.id.productTitle);
        quantityTV = findViewById(R.id.productQuantity);
        priceTV = findViewById(R.id.productPrice);
        dPriceTV = findViewById(R.id.productFinalPrice);
        descTV = findViewById(R.id.productDesc);
        discountTV = findViewById(R.id.proDisc);
        image = findViewById(R.id.productImage);
        pdMinus = findViewById(R.id.pdMinus);
        pdPlus = findViewById(R.id.pdPlus);
        pdNum = findViewById(R.id.pdNum);
        adjustPanel = findViewById(R.id.pdAdjustPanel);
        shimmerFrameLayout = findViewById(R.id.productShimmerLayout);
        productLayout = findViewById(R.id.productLayout);
        productList = new ArrayList<>();
        dataList = new ArrayList<>();
        database = RoomDB.getInstance(this);
        // firestore
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        int size = database.mainDao().getAll().size();
        if(size > 0){
            cqTv.setText("" + size);
            cqTv.setVisibility(View.VISIBLE);
        }



        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataList = database.mainDao().getAll();
                int initialNum = 0;

                for(int i=0; i<dataList.size(); i++){
                    if(String.valueOf(dataList.get(i).getId()).equals(id)){
                        initialNum = Integer.parseInt(dataList.get(i).getNumber());
                    }
                }

                Integer stockValue = Integer.parseInt(stock);

                if(stockValue > initialNum){
                    quantity = initialNum + 1;
                    pdNum.setText("" + quantity);
                    cost = Double.parseDouble(discountedPrice.replaceAll("₹", ""))*quantity;
                    finalCost = Double.parseDouble(discountedPrice.replaceAll("₹", ""));
                    addToCart.setVisibility(View.GONE);
                    adjustPanel.setVisibility(View.VISIBLE);
                    String itemTitle = titleTV.getText().toString();
                    addToCart(productID, itemTitle, product.getQuantity(), price,  discountedPrice, String.valueOf(cost), String.valueOf(quantity), productIcon);
                    cqTv.setText(""+ database.mainDao().getAll().size());
                    cqTv.setVisibility(View.VISIBLE);
                }else{
                    Toast.makeText(ProductDetails.this, "Insufficient stock for this action.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        pdPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Integer stockValue = Integer.parseInt(stock);

                if(stockValue > quantity){
                    quantity++;
                    cost = cost + finalCost;
                    pdNum.setText("" + quantity);
                    database.mainDao().update(Long.parseLong(productID), String.valueOf(quantity));
                    database.mainDao().updatePrice(Long.parseLong(productID), String.valueOf(cost));
                }else{
                    Toast.makeText(ProductDetails.this, "Insufficient stock for this action.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        pdMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(quantity > 1){
                    quantity--;
                    cost = cost - finalCost;
                    pdNum.setText("" + quantity);
                    database.mainDao().update(Long.parseLong(productID), String.valueOf(quantity));
                    database.mainDao().updatePrice(Long.parseLong(productID), String.valueOf(cost));
                }
            }
        });

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

        LoadProduct();
    }
    private String category, subCategory;
    private void LoadProduct() {

        shimmerFrameLayout.startShimmerAnimation();

        db.collection("products").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                productID = documentSnapshot.get("productID").toString();
                String title = documentSnapshot.get("title").toString();
                String description = documentSnapshot.get("description").toString();
                category = documentSnapshot.get("category").toString();
                subCategory = documentSnapshot.get("subcategory").toString();
                String quantity = documentSnapshot.get("quantity").toString();
                productIcon = documentSnapshot.get("productIcon").toString();
                price = documentSnapshot.get("price").toString();
                discountedPrice = documentSnapshot.get("discountedPrice").toString();
                stock = documentSnapshot.get("stock").toString();
                String uid = documentSnapshot.get("uid").toString();


                Double discountt = (((Double.parseDouble(price) - Double.parseDouble(discountedPrice))*100)/Double.parseDouble(price));
                int discount = (int)Math.floor(discountt);
                if(discount == 0){
                    discountTV.setVisibility(View.GONE);
                }
                titleTV.setText(title);
                quantityTV.setText(quantity);
                priceTV.setText("₹" + price);
                dPriceTV.setText("₹" + discountedPrice);
                priceTV.setPaintFlags(priceTV.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                discountTV.setText(String.valueOf(discount) + "% off");
                descTV.setText(description);

                product = new Product(productID, title, description, category, quantity, productIcon, price, discountedPrice, productID, uid, stock);

                shimmerFrameLayout.stopShimmerAnimation();
                shimmerFrameLayout.setVisibility(View.GONE);
                productLayout.setVisibility(View.VISIBLE);
                try{
                    Picasso.get().load(productIcon).placeholder(R.color.light_gray).into(image);
                }catch (Exception e){
                    image.setImageResource(R.drawable.splash_image);
                }
                LoadSuggestions();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProductDetails.this, "Something went wrong! Try again after some time.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void LoadSuggestions() {

        productList = new ArrayList<>();
        // get products from firestore

        db.collection("products").whereEqualTo("category", category).whereEqualTo("subcategory", subCategory).limit(10).get()
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
                            String stockk = documentSnapshot.get("stock").toString();
                            String discountedPrice = documentSnapshot.get("discountedPrice").toString();
                            String timestamp = documentSnapshot.get("timestamp").toString();
                            String uid = documentSnapshot.get("uid").toString();
                            if(productID.equals(id)){
                                continue;
                            }
                            Product suggestion = new Product(productID, title, description, category, quantity, productIcon, price, discountedPrice, timestamp, uid, stockk);
                            productList.add(suggestion);
                        }
                        adapter = new HomeRecyclerViewAdapter(ProductDetails.this, productList);
                        recyclerView.setAdapter(adapter);
                    }
                });

    }

    private void addToCart(String pID, String pTitle, String pQuantity, String originalPrice, String priceEach, String price, String number, String image) {

        MainData data = new MainData();
        data.setId(Long.parseLong(pID));
        data.setPid(pID);
        data.setName(pTitle);
        data.setQuantity(pQuantity);
        data.setNumber(number);
        data.setOriginalPrice(originalPrice);
        data.setPrice(price);
        data.setPriceEach(priceEach);
        data.setImage(image);

        database.mainDao().insert(data);


    }

    @Override
    public void onResume()
    {  // After a pause OR at startup
        super.onResume();
        addToCart.setVisibility(View.VISIBLE);
        pdNum.setText("1");
        adjustPanel.setVisibility(View.GONE);
        cqTv.setText(""+ database.mainDao().getAll().size());
        if(cqTv.getText().toString().equals("0")){
            cqTv.setVisibility(View.GONE);
        }
    }

}
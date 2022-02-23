package com.luitmed.prashantimedicos;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class Cart extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recyclerView;
    TextView cartTotal, discTotal, mrpTotal, savings;
    TextView checkout;
    LinearLayout cartLayout, emptyLayout;
    ImageButton backBtn;
    public double totalCost = 0.00;
    public double originalCost = 0.00;
    public double discount = 0.00;
    private CartAdapter adapter;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;
    String isAdded = "false";
    public List<MainData> dataList;
    RoomDB database;
    public TextView cqTv;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        toolbar = findViewById(R.id.cartToolbar);
        cqTv = toolbar.findViewById(R.id.tBarCq);
        recyclerView = findViewById(R.id.cartRV);
        cartTotal = findViewById(R.id.amountTotal);
        discTotal = findViewById(R.id.discTotal);
        mrpTotal = findViewById(R.id.mrpTotal);
        savings = findViewById(R.id.savingsTotal);
        cartLayout = findViewById(R.id.cartLayout);
        emptyLayout = findViewById(R.id.emptyCart);
        checkout = findViewById(R.id.checkOut);
        backBtn = findViewById(R.id.tBarBack);
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        database = RoomDB.getInstance(this);
        dataList = new ArrayList<>();

        loadCartItems();

        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dataList.size() == 0){
                    Toast.makeText(Cart.this, "Can't checkout as your cart is empty!", Toast.LENGTH_SHORT).show();
                    return;
                }
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if(firebaseUser == null){
                    startActivity(new Intent(getApplicationContext(), Register.class));
                    Toast.makeText(Cart.this, "You need to be logged in to place an order!", Toast.LENGTH_SHORT).show();
                    return;
                }

                db.collection("users").document(firebaseAuth.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        isAdded = documentSnapshot.get("address").toString();

                        if(isAdded.equals("true")){
                            Intent intent = new Intent(getApplicationContext(), Addresses.class);
                            startActivity(intent);
                        }else{
                            Intent intent = new Intent(getApplicationContext(), AddAddress.class);
                            startActivity(intent);
                        }
                    }
                });


            }
        });



        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private void loadCartItems() {

        dataList = database.mainDao().getAll();

        if(dataList.size() > 0){
            cqTv.setText("" + dataList.size());
            cqTv.setVisibility(View.VISIBLE);
        }


        for(int i = 0; i<dataList.size(); i++){
            double itemCost = Double.valueOf(dataList.get(i).getPrice());
            double itemMrp = Double.valueOf(dataList.get(i).getOriginalPrice());
            int num = Integer.valueOf(dataList.get(i).getNumber());
            totalCost += itemCost;
            originalCost += (itemMrp*num);
        }

        adapter = new CartAdapter(this, dataList);
        recyclerView.setAdapter(adapter);
        if(dataList.size() > 0){
            emptyLayout.setVisibility(View.GONE);
            cartLayout.setVisibility(View.VISIBLE);
            checkout.setVisibility(View.VISIBLE);
        }
        cartTotal.setText("₹" + String.format("%.2f", totalCost));  // upto 2 decimals
        mrpTotal.setText("₹" + String.format("%.2f", originalCost));
        discTotal.setText("-₹" + String.format("%.2f", originalCost - totalCost));
        savings.setText("You save ₹" + String.format("%.2f", originalCost - totalCost));
    }
}
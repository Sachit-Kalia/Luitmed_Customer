package com.luitmed.prashantimedicos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Addresses extends AppCompatActivity {

    ImageButton addAddress;
    ArrayList<Address> addressList;
    List<MainData> cartList;
    List<MainData> basketList;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;
    TextView payableAmount;
    Button finalCheckout;
    ProgressDialog progressDialog;
    RecyclerView recyclerView, basketRecylerView;
    AddressAdapter adapter;
    BasketAdapter basketAdapter;
    public String name = "";
    public String address = "";
    public String pinCode = "";
    public String phone = "";
    private String sEmail = "", sPassword = "";
    private RoomDB database;
    Boolean isSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addresses);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        addAddress = findViewById(R.id.addAddress);
        finalCheckout = findViewById(R.id.finalCheckout);
        payableAmount = findViewById(R.id.payableAmount);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);
        recyclerView = findViewById(R.id.addressRV);
        basketRecylerView = findViewById(R.id.basketRV);
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        addressList = new ArrayList<>();
        cartList = new ArrayList<>();
        basketList = new ArrayList<>();
        database = RoomDB.getInstance(this);


        loadAllAddresses();


        addAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddAddress.class);
                startActivity(intent);
            }
        });

        finalCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(addressList.size() == 0){
                    Toast.makeText(Addresses.this, "Kindly add an address first.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(isSelected == false){
                    Toast.makeText(Addresses.this, "Kindly select an address.", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(getApplicationContext(), Payment.class);
                intent.putExtra("name", name);
                intent.putExtra("address", address);
                intent.putExtra("pinCode", pinCode);
                intent.putExtra("phone", phone);
                intent.putExtra("amount", payableAmount.getText());
                startActivity(intent);
            }
        });
    }

    private void loadAllAddresses() {

        progressDialog.setMessage("Loading");
        progressDialog.show();

        loadBasket();

        db.collection("users").document(firebaseAuth.getUid()).collection("addresses").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        addressList.clear();
                        for(DocumentSnapshot documentSnapshot: queryDocumentSnapshots.getDocuments()){

                            String name = documentSnapshot.get("name").toString();
                            String phone = documentSnapshot.get("phone").toString();
                            String address = documentSnapshot.get("address").toString();
                            String pinCode = documentSnapshot.get("pinCode").toString();
                            String addressId = documentSnapshot.get("id").toString();

                            Address address1 = new Address(name, phone, address, pinCode, addressId);
                            addressList.add(address1);
                        }
                        adapter = new AddressAdapter(Addresses.this, addressList);
                        recyclerView.setAdapter(adapter);
                        progressDialog.dismiss();
                    }
                });

    }

    private void loadBasket() {

        basketList = database.mainDao().getAll();
        basketAdapter = new BasketAdapter(this, basketList);
        basketRecylerView.setAdapter(basketAdapter);

        double amount = 0.0;

        for(int i=0; i<basketList.size(); i++){
            amount += Double.parseDouble(basketList.get(i).getPrice());
        }

        payableAmount.setText("₹" + String.format("%.2f", amount));
    }


}
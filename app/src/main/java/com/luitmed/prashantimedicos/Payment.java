package com.luitmed.prashantimedicos;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.luitmed.prashantimedicos.Models.Common;
import com.luitmed.prashantimedicos.Models.FCMResponse;
import com.luitmed.prashantimedicos.Models.FCMSendData;
import com.luitmed.prashantimedicos.Remote.RetrofitFCMClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class Payment extends AppCompatActivity {

    RadioGroup radioGroup;
    RadioButton selectedMethod;
    private ImageButton back;
    Button payment;
    TextView amountTv;
    ProgressDialog progressDialog;
    List<MainData> cartList;
    private RoomDB database;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    IFCMService ifcmService;
    CompositeDisposable compositeDisposable;
    String shopID = "";
    private double orderPrice = 0.00;
    String name = "", address = "", pinCode = "", phone = "", amount = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        // get address for delivery
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        address = intent.getStringExtra("address");
        pinCode = intent.getStringExtra("pinCode");
        phone = intent.getStringExtra("phone");
        amount = intent.getStringExtra("amount");

        radioGroup = findViewById(R.id.radioGroup);
        payment = findViewById(R.id.paymentBtn);
        back = findViewById(R.id.backBtn);
        amountTv = findViewById(R.id.finalAmount);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);
        cartList = new ArrayList<>();
        database = RoomDB.getInstance(this);
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        ifcmService = RetrofitFCMClient.getInstance().create(IFCMService.class);
        compositeDisposable = new CompositeDisposable();
        amountTv.setText(amount);

        payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int selectedId = radioGroup.getCheckedRadioButtonId();
                selectedMethod = (RadioButton)findViewById(selectedId);

                switch (selectedId){
                    case R.id.cod:
                        placeOrder();
                        break;
                    case -1:
                        Toast.makeText(Payment.this, "Kindly check a payment method!", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(Payment.this, "This payment method is currently unavailable!", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private void placeOrder() {

        progressDialog.setMessage("Placing your order....");
        progressDialog.show();

        //retrieve from room database
        cartList = database.mainDao().getAll();

        for(int i=0; i<cartList.size(); i++){
            orderPrice += Double.parseDouble(cartList.get(i).getPrice());
        }


        db.collection("products").document(cartList.get(0).getPid()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        shopID = documentSnapshot.get("uid").toString();
                        // send to fireBase async task
                        addDataToFirebase();
                    }
                });


        // a list of items prepared, also final price calculated.
    }
    private String orderID;
    private Integer count;
    private void addDataToFirebase() {

        String timestamp = "" + System.currentTimeMillis();
        // get month and year
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;

        String cost = String.format("%.2f", orderPrice);
        orderID = timestamp;
        // order info

        String isPaid = "false";
        String mop = "cod";

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("orderId", "" + timestamp);
        hashMap.put("orderTime", "" + timestamp);
        hashMap.put("orderStatus", "In Progress");
        hashMap.put("orderCost", "" + cost);
        hashMap.put("orderBy", "" + firebaseAuth.getUid());
        hashMap.put("orderTo", "" + shopID);
        hashMap.put("Address", "" + address);
        hashMap.put("PinCode", "" + pinCode);
        hashMap.put("name", "" + name);
        hashMap.put("phone", "" + phone);
        hashMap.put("month", "" + month);
        hashMap.put("year", "" + year);
        hashMap.put("isPaid", "" + isPaid);
        hashMap.put("mop", "" + mop);

        CollectionReference collectionReference = db.collection("orders");
        collectionReference.document(timestamp).set(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onSuccess(Void aVoid) {
                        count = cartList.size();
                        for(int i=0; i<cartList.size(); i++){
                            String pId = cartList.get(i).getPid();
                            String cost = cartList.get(i).getPrice();
                            String itemName = cartList.get(i).getName();
                            String price = cartList.get(i).getPriceEach();
                            String quantity = cartList.get(i).getQuantity();
                            String number = cartList.get(i).getNumber();

                            HashMap<String, String> hashMap1 = new HashMap<>();
                            hashMap1.put("pId", pId);
                            hashMap1.put("name", itemName);
                            hashMap1.put("cost", cost);
                            hashMap1.put("price", price);
                            hashMap1.put("quantity", quantity);
                            hashMap1.put("number", number);

                            collectionReference.document(timestamp).collection("items").document(pId).set(hashMap1);

                            db.collection("products").document(pId).get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            int oldStock = Integer.parseInt(documentSnapshot.get("stock").toString());
                                            int newStock = oldStock - Integer.parseInt(number);
                                            db.collection("products").document(pId).update("stock", newStock)
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(Payment.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });

//                                            Toast.makeText(Payment.this, "Order placed successfully!", Toast.LENGTH_SHORT).show();

                                            // Make cart empty
                                            database.mainDao().reset(cartList);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Payment.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        sendConfirmationMail();
                        prepareNotification(orderID);
                    }
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void prepareNotification(String oid) {

        // get token

        db.collection("Tokens").document(shopID)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String token = documentSnapshot.get("token").toString();
                Map<String, String> data = new HashMap<>();
                data.put(Common.NOT_TITLE, "New Order");
                data.put(Common.NOT_CONTENT, "You have a new order with order id: " + oid);
                data.put(Common.NOT_ID, oid);
                FCMSendData sendData = new FCMSendData(token, data);

                compositeDisposable.add(ifcmService.sendNotification(sendData)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<FCMResponse>() {
                            @Override
                            public void accept(FCMResponse fcmResponse) throws Exception {
                                finish();
                                Intent intent = new Intent(getApplicationContext(), OrderConfirmation.class);
                                intent.putExtra("orderId", oid);
                                startActivity(intent);
                                progressDialog.dismiss();
//                          Toast.makeText(Payment.this, "Notification sent", Toast.LENGTH_SHORT).show();
                            }
                        }, throwable -> {
                            finish();
                            Intent intent = new Intent(getApplicationContext(), OrderConfirmation.class);
                            intent.putExtra("orderId", oid);
                            startActivity(intent);
                            progressDialog.dismiss();
//                      Toast.makeText(this, "Notification was not sent", Toast.LENGTH_SHORT).show();
                        }));

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
            }
        });

    }

    private String mailId = "";
    private void sendConfirmationMail() {

        db.collection("users").document(firebaseAuth.getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        mailId = documentSnapshot.get("email").toString().trim();
                        String msg = "<h3>Order placed successfully!!</h3> <p>Your order with order id " + orderID + " is confirmed. Thanks for placing your order with us.</p> </br></br> <p>Luitmed</p>";
                        String subject = "Your LUITMED order #"+ orderID + " of " + String.valueOf(count) + " items";

                        // send mail
                        JavaMailAPI javaMailAPI = new JavaMailAPI(Payment.this, mailId, subject, msg);
                        javaMailAPI.execute();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), Dashboard.class));
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }

}
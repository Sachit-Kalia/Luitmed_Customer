package com.luitmed.prashantimedicos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.luitmed.prashantimedicos.Adapters.SearchProductAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    private EditText searchTv;
    private FirebaseFirestore db;
    private ArrayList<Product> productList;
    private SearchProductAdapter adapter1;
    private RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        searchTv = findViewById(R.id.searchEditText);
        recyclerView = findViewById(R.id.searchRV);


        db = FirebaseFirestore.getInstance();

        productList = new ArrayList<>();
        adapter1 = new SearchProductAdapter(SearchActivity.this, productList);

        searchTv.requestFocus();

        searchTv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                // Filter after fetch

//                    try{
//                        adapter1.getFilter().filter(s);
//                        if(s.length() == 0){
//                            recyclerView.setVisibility(GONE);
//                        }else{
//                            recyclerView.setVisibility(View.VISIBLE);
//                        }
//                    }
//                    catch (Exception e){
//                        e.printStackTrace();
//                    }

                    // fetch after 3 letters entered

                    if(s.length() >= 3){
                        recyclerView.setVisibility(View.VISIBLE);
                        loadSearchProducts(s);
                    }

                }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        searchTv.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    Toast.makeText(SearchActivity.this, "Search clicked!", Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        });

//        loadProducts();

    }

    private void loadSearchProducts(CharSequence str) {

        String temp = String.valueOf(str);
        StringBuilder t = new StringBuilder(temp);
        t.setCharAt(0, Character.toUpperCase(temp.charAt(0)));
        String s = String.valueOf(t);


        db.collection("products").orderBy("title")
                .whereGreaterThanOrEqualTo("title", s).whereLessThan("title" ,s + "\uf8ff").limit(8)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                productList.clear();
                recyclerView.setAdapter(adapter1);

                for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                    String productID = documentSnapshot.get("productID").toString();
                    String stock = documentSnapshot.get("stock").toString();
                    int stockValue = Integer.parseInt(stock);
                    String title = documentSnapshot.get("title").toString();
                    String description = documentSnapshot.get("description").toString();
                    String category = documentSnapshot.get("category").toString();
                    String quantity = documentSnapshot.get("quantity").toString();
                    String productIcon = documentSnapshot.get("productIcon").toString();
                    String price = documentSnapshot.get("price").toString();
                    String discountedPrice = documentSnapshot.get("discountedPrice").toString();
                    String timestamp = documentSnapshot.get("timestamp").toString();
                    String uid = documentSnapshot.get("uid").toString();

                    Product homeProduct = new Product(productID, title, description, category, quantity, productIcon, price, discountedPrice, timestamp, uid, stock);
                    productList.add(homeProduct);
                    adapter1.notifyDataSetChanged();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                productList.clear();
                recyclerView.setAdapter(adapter1);
            }
        });
    }

//    private void loadProducts() {
//
//        db.collection("products").get()
//                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                    @Override
//                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                        productList.clear();
//                        recyclerView.setAdapter(adapter1);
//
//                        for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()){
//                            String productID = documentSnapshot.get("productID").toString();
//                            String stock = documentSnapshot.get("stock").toString();
//                            int stockValue = Integer.parseInt(stock);
//
//                            db.collection("products").document(productID).get()
//                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                                        @Override
//                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
//                                            String title = documentSnapshot.get("title").toString();
//                                            String description = documentSnapshot.get("description").toString();
//                                            String category = documentSnapshot.get("category").toString();
//                                            String quantity = documentSnapshot.get("quantity").toString();
//                                            String productIcon = documentSnapshot.get("productIcon").toString();
//                                            String price = documentSnapshot.get("price").toString();
//                                            String discountedPrice = documentSnapshot.get("discountedPrice").toString();
//                                            String timestamp = documentSnapshot.get("timestamp").toString();
//                                            String uid = documentSnapshot.get("uid").toString();
//
//                                            Product homeProduct = new Product(productID, title, description, category, quantity, productIcon, price, discountedPrice, timestamp, uid, stock);
//                                            productList.add(homeProduct);
//                                            adapter1.notifyDataSetChanged();
//                                        }
//                                    });
//                        }
//
//
//                    }
//                });
//    }


}
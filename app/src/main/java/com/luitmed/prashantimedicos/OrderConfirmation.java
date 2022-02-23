package com.luitmed.prashantimedicos;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class OrderConfirmation extends AppCompatActivity {

    Button btn;
    TextView orderID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirmation);
        Intent intent = getIntent();
        String id = intent.getStringExtra("orderId");
        orderID = findViewById(R.id.orderIdTv);
        btn = findViewById(R.id.continueShopping);

        orderID.setText(id);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Dashboard.class));
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        startActivity(new Intent(getApplicationContext(), Dashboard.class));
    }
}
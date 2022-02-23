package com.luitmed.prashantimedicos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class OrderDetailsAdapter extends RecyclerView.Adapter<OrderDetailsAdapter.CartHolder>{

    private Context context;
    public ArrayList<CartItem> cartList;


    public OrderDetailsAdapter(Context context, ArrayList<CartItem> cartList){
        this.context = context;
        this.cartList = cartList;
    }

    @NonNull
    @Override
    public CartHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // inflate layout
        View view = LayoutInflater.from(context).inflate(R.layout.order_detail_layout, parent, false);
        return new CartHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartHolder holder, int position) {
        // get data
        CartItem cartItem = cartList.get(position);

        String title = cartItem.getName();
        Integer titleSize = title.length();
        if(titleSize > 40){
            title = title.substring(0,40);
            title += "...";
        }

        String id = cartItem.getId();
        String pid = cartItem.getpId();
        String price = cartItem.getPrice();
        String image = cartItem.getIcon();
        String quantity = cartItem.getQuantity();
        String number = cartItem.getNumber();
        String cost = cartItem.getCost();
        // set data

        holder.cartTitle.setText(""+title);
        holder.cartQuantity.setText(""+quantity);
        holder.cartPrice.setText("₹" + price);
        holder.cartNumber.setText("X" + number);

        try{
            Picasso.get().load(image).placeholder(R.color.white).into(holder.cartIV);
        }catch (Exception e){
            holder.cartIV.setImageResource(R.drawable.splash_image);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // handle item click
            }
        });

    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }



    class CartHolder extends RecyclerView.ViewHolder{

        private ImageView cartIV;
        private TextView cartTitle, cartQuantity, cartPrice, cartNumber;


        public CartHolder(@NonNull View itemView) {
            super(itemView);

            cartIV = itemView.findViewById(R.id.orvImage);
            cartTitle = itemView.findViewById(R.id.orvTitle);
            cartQuantity = itemView.findViewById(R.id.orvQuantity);
            cartPrice = itemView.findViewById(R.id.orvPrice);
            cartNumber = itemView.findViewById(R.id.orvNumber);
        }
    }
}

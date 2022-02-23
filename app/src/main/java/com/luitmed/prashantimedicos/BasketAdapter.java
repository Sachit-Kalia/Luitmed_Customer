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

import java.util.List;

public class BasketAdapter extends RecyclerView.Adapter<BasketAdapter.BasketHolder>{

    private Context context;
    private List<MainData> dataList;
    private RoomDB database;


    public BasketAdapter(Context context, List<MainData> dataList){
        this.context = context;
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BasketHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // inflate layout
        View view = LayoutInflater.from(context).inflate(R.layout.basket_layout, parent, false);
        return new BasketHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BasketHolder holder, int position) {
        // get data
        MainData cartItem = dataList.get(position);
        database = RoomDB.getInstance(context);

        String title = cartItem.getName();
        Integer titleSize = title.length();
        if(titleSize > 40){
            title = title.substring(0,40);
            title += "...";
        }

        String image = cartItem.getImage();
        String quantity = cartItem.getQuantity();
        String number = cartItem.getNumber();
        String discPrice = cartItem.getPriceEach();
        String mrp = cartItem.getOriginalPrice();
        int discount = (int)(((Double.parseDouble(mrp) -  Double.parseDouble(discPrice))*100)/(Double.parseDouble(mrp)));

        // set data

        holder.cartTitle.setText(""+title);
        holder.cartQuantity.setText(""+quantity);
        holder.cartDiscPrice.setText("₹" + discPrice);
        holder.cartNumber.setText("x" + number);
        holder.cartPrice.setText("₹" + mrp);
        holder.cartDiscount.setText(discount + "% off");

        try{
            Picasso.get().load(image).placeholder(R.color.white).into(holder.cartIV);
        }catch (Exception e){
            holder.cartIV.setImageResource(R.drawable.splash_image);
        }

//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // handle item click
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }



    class BasketHolder extends RecyclerView.ViewHolder{

        private ImageView cartIV;
        private TextView cartTitle, cartQuantity, cartPrice, cartNumber, cartDiscPrice, cartDiscount;


        public BasketHolder(@NonNull View itemView) {
            super(itemView);

            cartIV = itemView.findViewById(R.id.basImage);
            cartTitle = itemView.findViewById(R.id.basTitle);
            cartQuantity = itemView.findViewById(R.id.baskQuantity);
            cartPrice = itemView.findViewById(R.id.baskPrice);
            cartNumber = itemView.findViewById(R.id.baskNumber);
            cartPrice = itemView.findViewById(R.id.baskPrice);
            cartNumber = itemView.findViewById(R.id.baskNumber);
            cartDiscPrice = itemView.findViewById(R.id.basDiscPrice);
            cartDiscount = itemView.findViewById(R.id.baskDiscount);

        }
    }
}


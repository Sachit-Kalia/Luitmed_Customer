package com.luitmed.prashantimedicos;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class ProductsRecyclerViewAdapter extends RecyclerView.Adapter<ProductsRecyclerViewAdapter.ProductHolder> {

    private Context context;
    public ArrayList<Product> productList;
    private RoomDB database;
    private boolean activate = true;

    public ProductsRecyclerViewAdapter(Context context, ArrayList<Product> productList){
        this.context = context;
        this.productList = productList;
    }

    public void activateButtons(boolean activate) {
        this.activate = activate;
        notifyDataSetChanged(); //need to call it for the child views to be re-created with buttons.
    }

    @NonNull
    @Override
    public ProductHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // inflate layout
        View view = LayoutInflater.from(context).inflate(R.layout.products_layout, parent, false);
        return new ProductHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductHolder holder, int position) {
        // get data
        database = RoomDB.getInstance(context);
        Product product = productList.get(position);
        String productID = product.getProductID();
        String title = product.getTitle();
        Integer titleSize = title.length();
        if(titleSize > 60){
            title = title.substring(0,60);
            title += "...";
        }

        String quantity = product.getQuantity();
        String price = product.getPrice();
        String discPrice = product.getDiscountedPrice();

        String image = product.getProductIcon();
        int stockValue = Integer.parseInt(product.getStock());

        // set data

        holder.spTitle.setText(title);
        holder.spPrice.setText("₹" + price);
        holder.spDiscountedPrice.setText("₹" + discPrice);
        holder.spQuantity.setText(quantity);
        Double discountt = (((Double.parseDouble(price) - Double.parseDouble(discPrice))*100)/Double.parseDouble(price));
        int discount = (int)Math.floor(discountt);

        holder.spDiscount.setText(String.valueOf(discount) + "% off");
        if(price == discPrice){
            holder.spDiscount.setVisibility(View.GONE);
            holder.spPrice.setVisibility(View.GONE);
        }else{
            holder.spDiscount.setVisibility(View.VISIBLE);
            holder.spPrice.setVisibility(View.VISIBLE);
            holder.spPrice.setPaintFlags(holder.spPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG); // strike-through the MRP
        }

        if(discount == 0){
            holder.spDiscount.setVisibility(View.GONE);
        }

        try{
            Picasso.get().load(image).placeholder(R.color.light_gray).into(holder.productIV);
        }catch (Exception e){
            holder.productIV.setImageResource(R.drawable.splash_image);
        }

        // initial stock value

        holder.productIV.setAlpha((float) 1.0);
        holder.outOfStock.setVisibility(View.GONE);
        holder.atcBtn.setVisibility(View.VISIBLE);

        if (activate) {
            holder.atcBtn.setVisibility(View.VISIBLE);
            holder.panel.setVisibility(View.GONE);
        }

        if(stockValue <= 0){
            holder.productIV.setAlpha((float) 0.4);
            holder.outOfStock.setVisibility(View.VISIBLE);
            holder.atcBtn.setVisibility(View.GONE);
        }

        final double[] cost = {Double.parseDouble(discPrice.replaceAll("₹", ""))};
        double finalCost = Double.parseDouble(discPrice.replaceAll("₹", ""));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // handle item click
                Intent intent = new Intent(context, ProductDetails.class);
                intent.putExtra("id", productID);
                context.startActivity(intent);
            }
        });

        holder.atcBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<MainData> dataList = database.mainDao().getAll();
                int initialNum = 0;
                for(int i=0; i<dataList.size(); i++){
                    if(String.valueOf(dataList.get(i).getId()).equals(productID)){
                        initialNum = Integer.parseInt(dataList.get(i).getNumber());
                    }
                }
                if(stockValue < (initialNum+1)){
                    Toast.makeText(context, "Insufficient stock to add this item.", Toast.LENGTH_SHORT).show();
                    return;
                }
                initialNum++;
                holder.number.setText("" + initialNum);
                holder.atcBtn.setVisibility(View.GONE);
                holder.panel.setVisibility(View.VISIBLE);
                String itemTitle = holder.spTitle.getText().toString();
                cost[0] = cost[0]*initialNum;
                addToCart(productID, itemTitle, product.getQuantity(), price, discPrice, String.valueOf(cost[0]), String.valueOf(initialNum), image);
                ((Products)context).cqTv.setText(""+ database.mainDao().getAll().size());
                ((Products)context).cqTv.setVisibility(View.VISIBLE);
            }
        });

        holder.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int proQuantity = Integer.parseInt(holder.number.getText().toString());
                if(stockValue < (proQuantity+1)){
                    Toast.makeText(context, "Insufficient stock to add this item.", Toast.LENGTH_SHORT).show();
                    return;
                }
                proQuantity++;
                cost[0] = cost[0] + finalCost;
                holder.number.setText("" + proQuantity);
                database.mainDao().update(Long.parseLong(productID), String.valueOf(proQuantity));
                database.mainDao().updatePrice(Long.parseLong(productID), String.valueOf(cost[0]));
            }
        });

        holder.minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int proQuantity = Integer.parseInt(holder.number.getText().toString());
                if(proQuantity > 1){
                    proQuantity--;
                    cost[0] = cost[0] - finalCost;
                    holder.number.setText("" + proQuantity);
                    database.mainDao().update(Long.parseLong(productID), String.valueOf(proQuantity));
                    database.mainDao().updatePrice(Long.parseLong(productID), String.valueOf(cost[0]));

                    if(proQuantity == 0){
                        holder.atcBtn.setVisibility(View.VISIBLE);
                        holder.panel.setVisibility(View.GONE);
                    }
                }else if(proQuantity == 1){
                    proQuantity--;
                    holder.number.setText("" + proQuantity);
                    holder.atcBtn.setVisibility(View.VISIBLE);
                    holder.panel.setVisibility(View.GONE);
                    database.mainDao().deleteItem(Long.parseLong(productID));
                    ((Products)context).cqTv.setText(""+ database.mainDao().getAll().size());
                    if(((Products)context).cqTv.getText().toString().equals("0")){
                        ((Products)context).cqTv.setVisibility(View.GONE);
                    }
                }
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
    public int getItemCount() {
        return productList.size();
    }



    class ProductHolder extends RecyclerView.ViewHolder{

        private ImageView productIV;
        private TextView spTitle, spDiscountedPrice, spPrice, spDiscount, spQuantity, number, outOfStock;
        private Button atcBtn;
        private ImageButton plus, minus;
        RelativeLayout panel;

        public ProductHolder(@NonNull View itemView) {
            super(itemView);

            productIV = itemView.findViewById(R.id.cpImage);
            spTitle = itemView.findViewById(R.id.cpTitle);
            spDiscountedPrice = itemView.findViewById(R.id.cpDiscountedPrice);
            spPrice = itemView.findViewById(R.id.cpPrice);
            spDiscount = itemView.findViewById(R.id.cpDiscount);
            spQuantity = itemView.findViewById(R.id.cpQuantity);
            atcBtn = itemView.findViewById(R.id.cpATC);
            panel = itemView.findViewById(R.id.productPanel);
            plus = itemView.findViewById(R.id.productItemPlus);
            minus = itemView.findViewById(R.id.productItemMinus);
            number = itemView.findViewById(R.id.productItemNumber);
            outOfStock = itemView.findViewById(R.id.pofs);

        }
    }
}

package com.luitmed.prashantimedicos.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.luitmed.prashantimedicos.Product;
import com.luitmed.prashantimedicos.ProductDetails;
import com.luitmed.prashantimedicos.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SearchProductAdapter extends RecyclerView.Adapter<SearchProductAdapter.ShopHolder> implements Filterable {

    private Context context;
    public ArrayList<Product> productList, filterList;
    private FilterShopProduct filter;
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;

    public SearchProductAdapter(Context context, ArrayList<Product> productList){
        this.context = context;
        this.productList = productList;
        this.filterList = productList;
    }

    @NonNull
    @Override
    public ShopHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // inflate layout
        View view = LayoutInflater.from(context).inflate(R.layout.search_item, parent, false);
        return new ShopHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShopHolder holder, int position) {
        // get data
        firebaseAuth =FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        Product product = productList.get(position);

        String title = product.getTitle();
        Integer titleSize = title.length();
        if(titleSize > 30){
            title = title.substring(0,27);
            title += "...";
        }
        String pid = product.getProductID();
        String image = product.getProductIcon();
        String description = product.getDescription();
        String quantity = product.getQuantity();

        // set data

        holder.nameTv.setText("" + title);
        holder.addressTv.setText("" + quantity);

        try{
            Picasso.get().load(image).placeholder(R.color.light_gray).into(holder.imageView);
        }catch (Exception e){
            holder.imageView.setImageResource(R.drawable.splash_image);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProductDetails.class);
                intent.putExtra("id", pid);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    @Override
    public Filter getFilter() {
        if(filter == null){
            filter = new FilterShopProduct(this, filterList);
        }
        return filter;
    }

    class ShopHolder extends RecyclerView.ViewHolder{

        private TextView nameTv, addressTv;
        private ImageView imageView;

        public ShopHolder(@NonNull View itemView) {
            super(itemView);

            nameTv = itemView.findViewById(R.id.srName);
            addressTv = itemView.findViewById(R.id.srAddress);
            imageView = itemView.findViewById(R.id.srImage);

        }
    }


}

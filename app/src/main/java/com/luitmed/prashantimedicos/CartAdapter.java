package com.luitmed.prashantimedicos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartHolder>{

    private Context context;
    private List<MainData> dataList;
    private RoomDB database;
    private FirebaseFirestore db;


    public CartAdapter(Context context, List<MainData> dataList){
        this.context = context;
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CartHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // inflate layout
        View view = LayoutInflater.from(context).inflate(R.layout.cart_item, parent, false);
        return new CartHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartHolder holder, int position) {
        // get data
        MainData cartItem = dataList.get(position);
        database = RoomDB.getInstance(context);
        db = FirebaseFirestore.getInstance();

        String title = cartItem.getName();
        Integer titleSize = title.length();
        if(titleSize > 40){
            title = title.substring(0,40);
            title += "...";
        }

        long id = cartItem.getId();
        String pid = cartItem.getPid();
        String price = cartItem.getPriceEach();
        String image = cartItem.getImage();
        String quantity = cartItem.getQuantity();
        final int[] number = {Integer.parseInt(cartItem.getNumber())};
        final double[] totalPrice = {Double.parseDouble(cartItem.getPrice())};

        double cost = Double.parseDouble(cartItem.getPriceEach());
        double mrp = Double.parseDouble(cartItem.getOriginalPrice());
        // set data

        holder.cartTitle.setText(""+title);
        holder.cartQuantity.setText(""+quantity);
        holder.cartPrice.setText("₹" + price);
        holder.cartNumber.setText(""+ number[0]);

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

        holder.minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Cart)context).totalCost -= cost;
                ((Cart)context).originalCost -= mrp;
                number[0]--;
                totalPrice[0] = totalPrice[0] - cost;
                cartItem.setNumber("" + number[0]);
                holder.cartNumber.setText(""+ number[0]);
                database.mainDao().update(Long.parseLong(pid), String.valueOf(number[0]));
                database.mainDao().updatePrice(Long.parseLong(pid), String.valueOf(totalPrice[0]));
                double newCost = ((Cart)context).totalCost;
                double newMrp = ((Cart)context).originalCost;
                ((Cart)context).cartTotal.setText("₹"+ String.format("%.2f", newCost));
                ((Cart)context).mrpTotal.setText("₹"+ String.format("%.2f", newMrp));
                ((Cart)context).discTotal.setText("-₹"+ String.format("%.2f", newMrp - newCost));
                ((Cart)context).savings.setText("You Save ₹"+ String.format("%.2f", newMrp - newCost));

                if(number[0] == 0){
                    database.mainDao().delete(cartItem);
                    dataList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, dataList.size());
                    ((Cart)context).cqTv.setText("" + dataList.size());
                }

                if(dataList.size() == 0){
                    ((Cart)context).emptyLayout.setVisibility(View.VISIBLE);
                    ((Cart)context).cartLayout.setVisibility(View.GONE);
                    ((Cart)context).checkout.setVisibility(View.GONE);
                    ((Cart)context).cqTv.setVisibility(View.GONE);
                }
            }
        });

        holder.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                db.collection("products").document(pid).get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                Integer stock = Integer.parseInt(documentSnapshot.get("stock").toString());
                                if(stock > number[0]){
                                    ((Cart)context).totalCost += cost;
                                    ((Cart)context).originalCost += mrp;
                                    number[0]++;
                                    totalPrice[0] = totalPrice[0] + cost;
                                    holder.cartNumber.setText(""+ number[0]);
                                    database.mainDao().update(Long.parseLong(pid), String.valueOf(number[0]));
                                    database.mainDao().updatePrice(Long.parseLong(pid), String.valueOf(totalPrice[0]));
                                    double newCost = ((Cart)context).totalCost;
                                    double newMrp = ((Cart)context).originalCost;
                                    ((Cart)context).cartTotal.setText("₹"+ String.format("%.2f", newCost));
                                    ((Cart)context).mrpTotal.setText("₹"+ String.format("%.2f", newMrp));
                                    ((Cart)context).discTotal.setText("-₹"+ String.format("%.2f", newMrp - newCost));
                                    ((Cart)context).savings.setText("You Save ₹"+ String.format("%.2f", newMrp - newCost));
                                }else{
                                    Toast.makeText(context, "Can't increment quantity due to insufficient stock!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }



    class CartHolder extends RecyclerView.ViewHolder{

        private ImageView cartIV;
        private TextView cartTitle, cartQuantity, cartPrice, cartNumber;
        private ImageButton plus, minus;

        public CartHolder(@NonNull View itemView) {
            super(itemView);

            cartIV = itemView.findViewById(R.id.ciImage);
            cartTitle = itemView.findViewById(R.id.ciTitle);
            cartQuantity = itemView.findViewById(R.id.ciQuantity);
            cartPrice = itemView.findViewById(R.id.ciPrice);
            cartNumber = itemView.findViewById(R.id.cartItemNumber);
            plus = itemView.findViewById(R.id.cartItemPlus);
            minus = itemView.findViewById(R.id.cartItemMinus);
        }
    }
}


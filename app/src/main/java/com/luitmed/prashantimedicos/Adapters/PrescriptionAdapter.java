package com.luitmed.prashantimedicos.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.luitmed.prashantimedicos.Models.Prescription;
import com.luitmed.prashantimedicos.PrescriptionDetails;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PrescriptionAdapter extends RecyclerView.Adapter<PrescriptionAdapter.OrderHolder>{

    private Context context;
    public ArrayList<Prescription> orderList;



    public PrescriptionAdapter(Context context, ArrayList<Prescription> orderList){
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // inflate layout
        View view = LayoutInflater.from(context).inflate(com.luitmed.prashantimedicos.R.layout.prescription_item, parent, false);
        return new OrderHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderHolder holder, int position) {
        // get data
        Prescription orderItem = orderList.get(position);

        String id = orderItem.getId();
        String date = orderItem.getDate();

        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm aaa  dd/MM/yyyy");
        String formattedDate = formatter.format(new Date(Long.parseLong(date)));

        // set data

        holder.oId.setText(""+ id);
        holder.oDate.setText("" + formattedDate);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PrescriptionDetails.class);
                intent.putExtra("id", id);
                context.startActivity(intent);
            }
        });
    }




    @Override
    public int getItemCount() {
        return orderList.size();
    }



    class OrderHolder extends RecyclerView.ViewHolder{


        private TextView oId, oDate;

        public OrderHolder(@NonNull View itemView) {
            super(itemView);

            oId = itemView.findViewById(com.luitmed.prashantimedicos.R.id.prId);
            oDate = itemView.findViewById(com.luitmed.prashantimedicos.R.id.prDate);

        }
    }
}
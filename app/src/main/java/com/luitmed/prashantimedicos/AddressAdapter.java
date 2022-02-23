package com.luitmed.prashantimedicos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressHolder>{

    private Context context;
    public ArrayList<Address> addressList;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;
    private int lastCheckedPosition = -1;


    public AddressAdapter(Context context, ArrayList<Address> addressList){
        this.context = context;
        this.addressList = addressList;
    }

    @NonNull
    @Override
    public AddressHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // inflate layout
        View view = LayoutInflater.from(context).inflate(R.layout.address_layout, parent, false);
        return new AddressHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressHolder holder, int position) {
        // get data
        Address addressItem = addressList.get(position);

        String address = addressItem.getAddress();
        String name = addressItem.getName();
        String phone = addressItem.getPhone();
        String pinCode = addressItem.getPinCode();
        String addressId = addressItem.getAddressID();

        // set data

        holder.aAddress.setText(""+ address);
        holder.aName.setText(""+ name);
        holder.aPhone.setText("" + phone);
        holder.aPinCode.setText("" + pinCode);
        holder.radioButton.setChecked(position == lastCheckedPosition);

        holder.removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("users").document(firebaseAuth.getUid()).collection("addresses")
                        .document(addressId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        ((Addresses)context).finish();
                        context.startActivity(((Addresses)context).getIntent());
                    }
                });
            }
        });

    }




    @Override
    public int getItemCount() {
        return addressList.size();
    }



    class AddressHolder extends RecyclerView.ViewHolder{


        private TextView aAddress, aName, aPhone, aPinCode;
        private Button removeBtn, editBtn;
        private RadioButton radioButton;

        public AddressHolder(@NonNull View itemView) {
            super(itemView);

            aAddress = itemView.findViewById(R.id.aAddress);
            aName = itemView.findViewById(R.id.aName);
            aPhone = itemView.findViewById(R.id.aPhone);
            aPinCode = itemView.findViewById(R.id.aPinCode);
            removeBtn = itemView.findViewById(R.id.removeAddress);
//            editBtn = itemView.findViewById(R.id.editAddress);
            radioButton = itemView.findViewById(R.id.radioCheck);
            firebaseAuth = FirebaseAuth.getInstance();
            db = FirebaseFirestore.getInstance();

            radioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int copyOfLastCheckedPosition = lastCheckedPosition;
                    lastCheckedPosition = getAdapterPosition();
                    notifyItemChanged(copyOfLastCheckedPosition);
                    notifyItemChanged(lastCheckedPosition);
                    ((Addresses)context).name = aName.getText().toString();
                    ((Addresses)context).address = aAddress.getText().toString();
                    ((Addresses)context).phone = aPhone.getText().toString();
                    ((Addresses)context).pinCode = aPinCode.getText().toString();
                    ((Addresses)context).isSelected = true;
                }
            });
        }
    }
}

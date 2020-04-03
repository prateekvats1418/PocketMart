package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import Model.AdminOrders;

public class AdminNewOrdersActivity extends AppCompatActivity {

    private RecyclerView ordersList;
    private DatabaseReference ordersRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_new_orders);



        ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders");

        ordersList = (RecyclerView) findViewById(R.id.orders_list);
        ordersList.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    protected void onStart()
    {
        super.onStart();
        FirebaseRecyclerOptions<AdminOrders> options =
                new FirebaseRecyclerOptions.Builder<AdminOrders>()
                .setQuery(ordersRef,AdminOrders.class).build();

        FirebaseRecyclerAdapter<AdminOrders,AdminOrdersView> adapter =
                new FirebaseRecyclerAdapter<AdminOrders, AdminOrdersView>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final AdminOrdersView adminOrdersView, final int i, @NonNull final AdminOrders adminOrders)
                    {
                        adminOrdersView.userName.setText("Name : "+ adminOrders.getName());
                        adminOrdersView.userPhoneNumber.setText("Phone: "+ adminOrders.getPhone());
                        adminOrdersView.userTotalPrice.setText("Total Amount : "+ adminOrders.getTotalAmount());
                        adminOrdersView.userShippingAddress.setText("Shipping Adress : "+ adminOrders.getAddress()+", "+adminOrders.getCity());
                        adminOrdersView.userDateTime.setText("Order At : "+ adminOrders.getDate() +" "+ adminOrders.getTime());

                        adminOrdersView.ShowOrdersBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                String uID = getRef(i).getKey();
                                Intent intent = new Intent(AdminNewOrdersActivity.this,AdminUserProductsActivity.class);
                                intent.putExtra("uid",uID);
                                startActivity(intent);
                            }
                        });

                        adminOrdersView.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v)
                            {
                                CharSequence options [] = new CharSequence[]
                                        {
                                                "YES",
                                                "NO"
                                        };
                                AlertDialog.Builder builder =
                                        new AlertDialog.Builder(AdminNewOrdersActivity.this);

                                builder.setTitle("Have you shipped this order Products");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        if(which == 0)
                                        {
                                            String uID = getRef(i).getKey();
                                            RemoveOrder(uID);

                                        }
                                        else
                                        {
                                            finish();
                                        }
                                    }
                                });

                                builder.show();
                            }
                        });


                    }

                    @NonNull
                    @Override
                    public AdminOrdersView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_layout,parent,false);
                        return new AdminOrdersView(view);
                    }
                };
        ordersList.setAdapter(adapter);
        adapter.startListening();
    }

    private void RemoveOrder(String uID)
    {
        ordersRef.child(uID).removeValue();
    }

    public static class AdminOrdersView extends RecyclerView.ViewHolder
    {
        public TextView userName,userPhoneNumber,userTotalPrice,userDateTime,userShippingAddress;
        public Button ShowOrdersBtn;

        public AdminOrdersView(@NonNull View itemView)
        {
            super(itemView);

            userName = itemView.findViewById(R.id.order_user_name);
            userPhoneNumber = itemView.findViewById(R.id.order_phone_number);
            userTotalPrice = itemView.findViewById(R.id.order_total_price);
            userDateTime = itemView.findViewById(R.id.order_date_time);
            userShippingAddress = itemView.findViewById(R.id.order_address_city);

            ShowOrdersBtn = itemView.findViewById(R.id.show_all_products_btn);

        }
    }
}

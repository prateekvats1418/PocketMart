package Sellers;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.SellerProductsCategoryActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import Model.Products;
import ViewHolder.itemViewHolder;

public class SellersHomeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    private DatabaseReference unverifiedProductsRef;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sellers_home);
        BottomNavigationView navView = findViewById(R.id.nav_view);


      unverifiedProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");

      recyclerView = findViewById(R.id.seller_home_recyclerview);
      recyclerView.setHasFixedSize(true);
      layoutManager = new LinearLayoutManager(this);

      recyclerView.setLayoutManager(layoutManager);




        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_add, R.id.navigation_logout)
                .build();
       // NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
       // NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        //NavigationUI.setupWithNavController(navView, navController);

      navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
          @Override
          public boolean onNavigationItemSelected(@NonNull MenuItem menuItem)
          {
              switch (menuItem.getItemId())
              {
                  case R.id.navigation_home:
                      Intent intent2 = new Intent(SellersHomeActivity.this,SellersHomeActivity.class);
                      startActivity(intent2);
                      return true;


                  case R.id.navigation_add:
                      Intent intent1 = new Intent(SellersHomeActivity.this, SellerProductsCategoryActivity.class);


                      startActivity(intent1);

                      return true;


                  case R.id.navigation_logout:
                      final FirebaseAuth mAuth ;
                      mAuth = FirebaseAuth.getInstance();
                      mAuth.signOut();

                      Intent intent = new Intent(SellersHomeActivity.this, MainActivity.class);
                      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                      startActivity(intent);
                      finish();
                      return true;


              }
              return false;
          }
      });
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        FirebaseRecyclerOptions<Products> options = new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(unverifiedProductsRef.orderByChild("sid").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid()),Products.class)
                .build();

        FirebaseRecyclerAdapter<Products, itemViewHolder> adapter =
                new FirebaseRecyclerAdapter<Products, itemViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull itemViewHolder productViewHolder, int i, @NonNull final Products products)
                    {
                        productViewHolder.txtProductName.setText(products.getName());
                        productViewHolder.txtProductDescription.setText(products.getDescription());

                        productViewHolder.txtProductState.setText("State : "+products.getProductState());

                        productViewHolder.txtProductPrice.setText("price = "+ products.getPrice()+ "$");
                        Picasso.get().load(products.getImage()).into(productViewHolder.imageView);

                        productViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v)
                            {
                                final String productID = products.getPid();

                                CharSequence options [] = new CharSequence []
                                        {
                                                "Yes",
                                                "No"
                                        };
                                AlertDialog.Builder builder = new AlertDialog.Builder(SellersHomeActivity.this);
                                builder.setTitle("Do you want to delete this product.. Are you Sure");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        if(which == 0)
                                        {
                                            deleteProduct(productID);

                                        }
                                        if(which == 1)
                                        {

                                        }

                                    }
                                });
                                builder.show();


                            }
                        });

                    }

                    @NonNull
                    @Override
                    public itemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.seller_item_view,parent,false);

                        itemViewHolder holder = new itemViewHolder(view);
                        return holder;
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }

    private void deleteProduct(String productID)
    {
        unverifiedProductsRef.child(productID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if(task.isSuccessful())
                {
                    Toast.makeText(SellersHomeActivity.this,"The item has been deleted successfully",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}


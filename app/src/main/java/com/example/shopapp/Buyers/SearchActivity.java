package com.example.shopapp.Buyers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.shopapp.Model.Products;
import com.example.shopapp.R;
import com.example.shopapp.ViewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class SearchActivity extends AppCompatActivity {

    private Button search ;
    private RecyclerView searchList;
    private EditText text;
    private String SEARCH;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

    text = findViewById(R.id.search_product_name);
    search = findViewById(R.id.searchbtn);
    searchList = findViewById(R.id.search_list);
    searchList.setLayoutManager(new LinearLayoutManager(SearchActivity.this));

    search.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        SEARCH = text.getText().toString();
        onStart();
        }
    });
    }

    @Override
    protected void onStart() {
        super.onStart();

        DatabaseReference searchRef = FirebaseDatabase.getInstance().getReference().child("Products");

        FirebaseRecyclerOptions<Products> options
                = new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(searchRef.orderByChild("pname").startAt(SEARCH),Products.class)
                .build();
        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter
                = new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull final Products model) {
                holder.txtProductName.setText(String.valueOf(model.getPname()));
                holder.txtProductDescription.setText(String.valueOf(model.getDescription()));
                holder.txtProductPrice.setText("Prix :"+ model.getPrice() + "DA");
                Picasso.get().load(model.getImage()).into(holder.imageView);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(SearchActivity.this,ProductDetailsActivity.class);
                        i.putExtra("pid",model.getPid());
                        startActivity(i);
                    }
                });
            }

            @NonNull
            @Override
            public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items_layout,parent,false);
                ProductViewHolder holder = new ProductViewHolder(view);
                return holder;
            }
        };
        searchList.setAdapter(adapter);
        adapter.startListening();
    }
}
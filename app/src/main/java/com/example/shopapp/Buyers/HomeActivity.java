package com.example.shopapp.Buyers;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopapp.Admin.AdminMaintainActivity;
import com.example.shopapp.MainActivity;
import com.example.shopapp.Model.Products;
import com.example.shopapp.Prevalent.Prevalent;
import com.example.shopapp.R;
import com.example.shopapp.ViewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DatabaseReference produit;
    private String dbname ="Products";
    private RecyclerView recycler;
    private RecyclerView.LayoutManager layoutmanager;
    private String type="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Intent i = getIntent();
        Bundle bundle = i.getExtras();

        if (bundle != null)
        {
            type = getIntent().getExtras().get("Admin").toString();
        }


        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Home");
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if( !type.equals("Admin"))
                {
                    Intent i = new Intent(HomeActivity.this, CartActivity.class);
                    startActivity(i);
                }

            }
        });


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        Paper.init(this);

        View headerView =navigationView.getHeaderView(0);
        TextView userNameTextView =   headerView.findViewById(R.id.user_profile_name);
        CircleImageView profileImageView = headerView.findViewById(R.id.user_profile_image);

       if(!type.equals("Admin"))
       {
           userNameTextView.setText(Prevalent.currentOnlineUser.getName());
           Picasso.get().load(Prevalent.currentOnlineUser.getImage()).placeholder(R.drawable.profile).into(profileImageView);
       }

        produit = FirebaseDatabase.getInstance().getReference().child(dbname);

        recycler = findViewById(R.id.recycler_menu);
        recycler.setHasFixedSize(true);
        layoutmanager = new LinearLayoutManager(this);
        recycler.setLayoutManager(layoutmanager);

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Products> options = new FirebaseRecyclerOptions.Builder<Products>().setQuery(produit.orderByChild("productState").equalTo("Approved"),Products.class).build();

        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter = new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull final Products model) {

                holder.txtProductName.setText(String.valueOf(model.getPname()));
                holder.txtProductDescription.setText(String.valueOf(model.getDescription()));
                holder.txtProductPrice.setText("Prix :"+ model.getPrice() + "$");
                Picasso .get().load(model.getImage()).into(holder.imageView);



                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if( type.equals("Admin"))
                        {
                            Intent i = new Intent(HomeActivity.this, AdminMaintainActivity.class);
                            i.putExtra("pid",model.getPid());
                            startActivity(i);
                        }
                        else
                        {
                            Intent i = new Intent(HomeActivity.this,ProductDetailsActivity.class);
                            i.putExtra("pid",model.getPid());
                            startActivity(i);
                        }

                    }
                });
            }


            @Override
            public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items_layout,parent,false);
                ProductViewHolder holder = new ProductViewHolder(view);
                return holder;
            }
        };
        recycler.setAdapter(adapter);
        adapter.startListening();
    }

    @Override

    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        else
        {
            super.onBackPressed();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean  onOptionsItemSelected(MenuItem item)
    {
        int id =item.getItemId();
        /*if (id == R.id.action_settings)
        {
            return true;
        }*/
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if (id==R.id.nav_cart)
        { if( !type.equals("Admin"))
        {
            Intent i = new Intent(HomeActivity.this,CartActivity.class);
            startActivity(i);
        }
        }
        else if (id==R.id.nav_search)
        { if( !type.equals("Admin"))
        {
            Intent i = new Intent(HomeActivity.this,SearchActivity.class);
            startActivity(i);
        }
        }
        else if (id==R.id.nav_categories)
        {

        }
        else if (id==R.id.nav_settings)
        { if( !type.equals("Admin"))
        {
            Intent i =  new Intent(HomeActivity.this,SettingsActivity.class);
            startActivity(i);
        }
        }
        else if (id==R.id.nav_logout)
        { if( !type.equals("Admin"))
        {
            Paper.book().destroy();

            Intent i = new Intent(HomeActivity.this, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
        }

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        drawer.closeDrawer(GravityCompat.START);

        return true;
    }


}

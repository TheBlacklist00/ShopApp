package com.example.shopapp.Admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.shopapp.Buyers.HomeActivity;
import com.example.shopapp.MainActivity;
import com.example.shopapp.R;
import com.example.shopapp.Sellers.SellerCategoryActivity;

public class AdminHomeActivity extends AppCompatActivity {
    private Button checkOrders,logout,maintain,approve;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);
        checkOrders = (Button) findViewById(R.id.check_orders);
        logout = (Button) findViewById(R.id.admin_logout);
        maintain= (Button) findViewById(R.id.maintain);
        approve = (Button) findViewById(R.id.admin_approve_products);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AdminHomeActivity.this, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();
            }
        });
        checkOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AdminHomeActivity.this,AdminOrdersActivity.class);

                startActivity(i);

            }
        });

        maintain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AdminHomeActivity.this, HomeActivity.class);
                i.putExtra("Admin","Admin");
                startActivity(i);
            }
        });
        approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AdminHomeActivity.this, AdminApproveActivity.class);
                i.putExtra("Admin","Admin");
                startActivity(i);
            }
        });
    }
}
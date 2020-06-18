package com.example.shopapp.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.shopapp.Interface.ItemClickListner;
import com.example.shopapp.R;


public class SellerProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    public TextView txtProductName, txtProductDescription, txtProductPrice,txtProductStatus;
    public ImageView imageView;
    public ItemClickListner listner;


    public SellerProductViewHolder(View itemView)
    {
        super(itemView);


        imageView = (ImageView) itemView.findViewById(R.id.product_seller_image);
        txtProductName = (TextView) itemView.findViewById(R.id.product_seller_name);
        txtProductDescription = (TextView) itemView.findViewById(R.id.product_seller_description);
        txtProductPrice = (TextView) itemView.findViewById(R.id.product_seller_price);
        txtProductStatus = (TextView) itemView.findViewById(R.id.product_seller_State);
    }

    public void setItemClickListner(ItemClickListner listner)
    {
        this.listner = listner;
    }

    @Override
    public void onClick(View view)
    {
        listner.onClick(view, getAdapterPosition(), false);
    }
}

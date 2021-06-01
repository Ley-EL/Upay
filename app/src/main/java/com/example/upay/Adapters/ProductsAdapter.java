package com.example.upay.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.upay.Models.ProductInfos;
import com.example.upay.R;

import java.util.ArrayList;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ProductsAdapterViewHolder> {

    private onItemClickListener mListener;

    private ArrayList<ProductInfos> productsList = new ArrayList<>();
    private Context context;

    public ProductsAdapter(ArrayList<ProductInfos> productsList, Context context) {
        this.productsList = productsList;
        this.context = context;
    }



    public static class ProductsAdapterViewHolder extends RecyclerView.ViewHolder {

        ImageView productImg, productOptions;
        TextView productName, productPrice;

        public ProductsAdapterViewHolder(@NonNull View itemView, onItemClickListener listener) {
            super(itemView);

            // retrieve all id
            productImg = itemView.findViewById(com.example.upay.R.id.product_img);
            productOptions = itemView.findViewById(R.id.product_options);
            productName = itemView.findViewById(com.example.upay.R.id.product_name);
            productPrice = itemView.findViewById(com.example.upay.R.id.product_price);

            productOptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }



    @NonNull
    @Override
    public ProductsAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(com.example.upay.R.layout.product_item_model,
                parent, false);

        return new ProductsAdapterViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductsAdapterViewHolder holder, int position) {
        // get current product item
        ProductInfos currentProduct = productsList.get(position);

        // set product image
        Glide.with(context).load(currentProduct.getProductImgLink()).into(holder.productImg);

        // set product name
        holder.productName.setText(currentProduct.getProductName());

        // set product price
        holder.productPrice.setText(currentProduct.getProductPrice());

    }

    @Override
    public int getItemCount() {
        return productsList.size();
    }

    public interface onItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(onItemClickListener listener) {
        mListener = listener;
    }
}

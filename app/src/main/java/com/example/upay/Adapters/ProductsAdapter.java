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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ProductsAdapterViewHolder> {

    private onItemClickListener itemListener;
    private onClickListener clickListener;

    private ArrayList<ProductInfos> productsList = new ArrayList<>();
    private Context context;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    public ProductsAdapter(ArrayList<ProductInfos> productsList, Context context) {
        this.productsList = productsList;
        this.context = context;
    }

    public static class ProductsAdapterViewHolder extends RecyclerView.ViewHolder {

        ImageView productImg, productOptions;
        TextView productName, productPrice;

        public ProductsAdapterViewHolder(@NonNull View itemView, onItemClickListener itemListener,
                                         onClickListener clickListener, FirebaseUser currentUser) {
            super(itemView);

            // retrieve all id
            productImg = itemView.findViewById(com.example.upay.R.id.product_img);
            productOptions = itemView.findViewById(R.id.product_options);
            productName = itemView.findViewById(com.example.upay.R.id.product_name);
            productPrice = itemView.findViewById(com.example.upay.R.id.product_price);

            if (currentUser.getDisplayName().equals("Upay") && currentUser.getEmail().equals("upay@gmail.com")) {
                productOptions.setVisibility(View.VISIBLE);
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            itemListener.onItemClick(position);
                        }
                    }
                }
            });

            productOptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (clickListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            clickListener.onClick(productOptions, position);
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

        // initialize firebase variable
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        return new ProductsAdapterViewHolder(view, itemListener, clickListener, currentUser);
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
        itemListener = listener;
    }

    public interface onClickListener {
        void onClick(View view, int position);
    }

    public void setOnClickListener(onClickListener listener) {
        clickListener = listener;
    }
}

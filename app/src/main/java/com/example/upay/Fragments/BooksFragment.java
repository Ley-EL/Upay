package com.example.upay.Fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.upay.Adapters.ProductsAdapter;
import com.example.upay.Models.ProductInfos;
import com.example.upay.Models.ProductsDetailsModel;
import com.example.upay.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class BooksFragment extends Fragment {

    private TextView noProductFound;

    private ProgressDialog progressDialog;

    private RecyclerView recyclerView;
    private ProductsAdapter adapter;
    private ArrayList<ProductInfos> productsList;

    private FirebaseDatabase database;
    private DatabaseReference clothesRef;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    private final String refName = "Books";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_books, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // set toolbar title
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(refName);

        // retrieve all id
        noProductFound = view.findViewById(R.id.no_product_found);

        // initialize firebase variable
        database = FirebaseDatabase.getInstance();
        clothesRef = database.getReference("Product").child(refName);
        storage = FirebaseStorage.getInstance();

        // initialize progress dialog
        progressDialog = new ProgressDialog(getActivity());

        // set progress dialog not cancelable
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        FillProductList(view);
        BuildRecyclerView(view);
    }

    private void FillProductList(View view) {
        // set progress dialog message
        progressDialog.setMessage(getString(R.string.progress_dialog_product_loading));

        // show progress dialog
        progressDialog.show();

        // initialize array list
        productsList = new ArrayList<>();

        clothesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // check if there are products in this category
                if (snapshot.getValue() == null) {
                    // dismiss progress dialog
                    progressDialog.dismiss();

                    // show text view
                    noProductFound.setVisibility(View.VISIBLE);
                }else {
                    noProductFound.setVisibility(View.GONE);
                    // clear list
                    productsList.clear();

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        ProductInfos productInfos = dataSnapshot.getValue(ProductInfos.class);

                        // add product info to the list
                        productsList.add(0, productInfos);
                    }

                    adapter.notifyDataSetChanged();

                    // dismiss progress dialog
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void BuildRecyclerView(View view) {
        // get recycler view id
        recyclerView = view.findViewById(R.id.recycler_view);

        recyclerView.setHasFixedSize(true);

        // initialize adapter
        adapter = new ProductsAdapter(productsList, getActivity());

        // define layout manager
        RecyclerView.LayoutManager layoutManager = new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        // attach adapter to recycler view
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new ProductsAdapter.onItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // get current product clicked
                ProductInfos currentProduct = productsList.get(position);

                Intent intent = new Intent(getActivity(), ProductsDetailsModel.class);
                intent.putExtra("product_image", currentProduct.getProductImgLink());
                intent.putExtra("product_name", currentProduct.getProductName());
                intent.putExtra("product_price", currentProduct.getProductPrice());
                intent.putExtra("product_description", currentProduct.getProductDesc());
                startActivity(intent);
            }
        });

        adapter.setOnClickListener(new ProductsAdapter.onClickListener() {
            @Override
            public void onClick(View view, int position) {
                ShowDeleteProductDialog(position);
            }
        });
    }

    private void ShowDeleteProductDialog(int position) {
        // create dialog
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity(), R.style.AlertDialogTheme);
        dialog.setTitle(R.string.dialog_title_delete_product);
        dialog.setCancelable(false);


        dialog.setNegativeButton(R.string.dialog_negative_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // dismiss dialog
                dialog.dismiss();
            }
        });

        dialog.setPositiveButton(R.string.dialog_positive_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DeleteProduct(position);
            }
        });

        // show dialog
        dialog.show();

    }

    private void DeleteProduct(int position) {
        // set progress dialog message
        progressDialog.setMessage(getString(R.string.progress_dialog_product_deleting));

        // show progress dialog
        progressDialog.show();

        // get current product clicked
        ProductInfos currentProduct = productsList.get(position);

        // create a ref to cloud storage
        storageRef = storage.getReference(currentProduct.getProductImgName());

        Query query = clothesRef.orderByChild("productName").equalTo(currentProduct.getProductName());

        storageRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    // remove product from db
                                    dataSnapshot.getRef().removeValue();

                                    // remove product from list
                                    productsList.remove(position);

                                    // dismiss progress dialog
                                    progressDialog.dismiss();

                                    Toast.makeText(getActivity(), getString(R.string.product_delete_successfully),
                                            Toast.LENGTH_SHORT).show();
                                }
                                adapter.notifyItemRemoved(position);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // dismiss progress dialog
                        progressDialog.dismiss();

                        Toast.makeText(getActivity(), getString(R.string.product_not_delete), Toast.LENGTH_SHORT).show();
                        Toast.makeText(getActivity(), "Something wrong: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

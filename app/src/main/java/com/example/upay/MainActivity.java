package com.example.upay;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.upay.Fragments.BooksFragment;
import com.example.upay.Fragments.ClothesFragment;
import com.example.upay.Fragments.ElectronicsFragment;
import com.example.upay.Fragments.HomeFragment;
import com.example.upay.Fragments.ShoesFragment;
import com.example.upay.Fragments.StreamingFragment;
import com.example.upay.Fragments.TopFragment;
import com.example.upay.Models.ProductInfos;
import com.example.upay.UserConnections.LoginActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ExtendedFloatingActionButton addProduct;
    private Spinner spinner;
    private ImageView mainProductImg;
    private TextInputLayout productNameLayout, productDescLayout, productPriceLayout;
    private TextInputEditText productName, productDesc, productPrice;
    private String productNameVal, productDescVal, productPriceVal, productCategory;
    private ProgressDialog progressDialog;
    private Dialog dialog;
    private Uri mainPickedImgUri;
    private final int EXTERNAL_STORAGE_RC = 1;
    private final int MAIN_GALLERY_RC = 0;
    private DrawerLayout drawer;
    private NavigationView navigationView;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseDatabase database;
    private DatabaseReference databaseRef;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get toolbar id
        Toolbar toolbar = findViewById(R.id.toolbar);

        // set toolbar
        setSupportActionBar(toolbar);

        // set toolbar title
        getSupportActionBar().setTitle("Upay");

        // set home fragment default fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new HomeFragment()).commit();

        // retrieve all id
        drawer = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.navigation_view);// retrieve all id
        addProduct = findViewById(R.id.add_product);

        // initialize progress dialog
        progressDialog = new ProgressDialog(MainActivity.this);

        // set progress dialog message
        progressDialog.setMessage("Product is adding...");

        // set progress dialog not cancelable
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);

        // initialize firebase variable
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        databaseRef = database.getReference().child("Product");
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference().child("Product Image");

        // draw button for navigation view
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(MainActivity.this, drawer,
                toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowDialog();
            }
        });

        UpdateNavHeader();
    }

    public void UpdateNavHeader() {
        // get header view
        View headerView = navigationView.getHeaderView(0);

        // retrieve nav header id
        TextView navUserName = headerView.findViewById(R.id.user_name);
        TextView navUserEmail = headerView.findViewById(R.id.user_email);

        // set nav user name text
        navUserName.setText(currentUser.getDisplayName());

        // set nav user email
        navUserEmail.setText(currentUser.getEmail());
    }

    private void ShowDialog() {
        // create dialog
        dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.custom_dialog_add_product);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        // retrieve all id
        mainProductImg = dialog.findViewById(R.id.main_product_img);
        productNameLayout = dialog.findViewById(R.id.product_name_layout);
        productDescLayout = dialog.findViewById(R.id.product_desc_layout);
        productPriceLayout = dialog.findViewById(R.id.product_price_layout);
        productName = dialog.findViewById(R.id.product_name);
        productDesc = dialog.findViewById(R.id.product_desc);
        productPrice = dialog.findViewById(R.id.product_price);
        spinner = dialog.findViewById(R.id.spinner_category);
        Button cancelBtn = dialog.findViewById(R.id.cancel_btn);
        Button addBtn = dialog.findViewById(R.id.add_btn);

        // initialize spinner adapter
        ArrayAdapter spinnerAdapter = ArrayAdapter.createFromResource(MainActivity.this,
                R.array.product_category, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                productCategory = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mainProductImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestPermission();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // dismiss dialog
                dialog.dismiss();
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateFields();
            }
        });

        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);

        // set dialog size
        dialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);

        // show dialog
        dialog.show();
    }

    private void RequestPermission() {
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_RC);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case EXTERNAL_STORAGE_RC:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    OpenMainGallery();
                } else {
                    Toast.makeText(MainActivity.this,
                            getString(R.string.external_storage_permission_denied), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void OpenMainGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, MAIN_GALLERY_RC);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == MAIN_GALLERY_RC && data != null) {
            // get image
            mainPickedImgUri = data.getData();

            // set image to ImageView
            mainProductImg.setImageURI(mainPickedImgUri);

        }
    }

    private void ValidateFields() {
        // get value from TextInputEditText
        productNameVal = productName.getText().toString().trim();
        productDescVal = productDesc.getText().toString().trim();
        productPriceVal = productPrice.getText().toString().trim();

        if (productNameVal.isEmpty()) {
            productNameLayout.setError(getString(R.string.fill_in_field));
        } else {
            productNameLayout.setError(null);
        }

        if (productDescVal.isEmpty()) {
            productDescLayout.setError(getString(R.string.fill_in_field));
        } else {
            productDescLayout.setError(null);
        }

        if (productPriceVal.isEmpty()) {
            productPriceLayout.setError(getString(R.string.fill_in_field));
        } else {
            productPriceLayout.setError(null);
        }

        if (mainPickedImgUri == null) {
            Toast.makeText(MainActivity.this, getString(R.string.add_an_image), Toast.LENGTH_SHORT).show();
        }

        if (productNameLayout.getError() == null && productDescLayout.getError() == null &&
                productPriceLayout.getError() == null && mainPickedImgUri != null) {
            AddProductImg();
        }
    }

    private void AddProductImg() {
        // show progress dialog
        progressDialog.show();

        StorageReference productCategoryRef = storageRef.child(productCategory);
        StorageReference productImg = productCategoryRef.child(mainPickedImgUri.getLastPathSegment());

        if (mainPickedImgUri != null) {
            productImg.putFile(mainPickedImgUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            productImg.getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            ProductInfos productInfos = new ProductInfos(productNameVal,
                                                    productDescVal, productPriceVal);
                                            productInfos.setProductImgLink(uri.toString());
                                            AddProductInfos(productInfos);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // dismiss progress dialog
                                            progressDialog.dismiss();

                                            Toast.makeText(MainActivity.this, "Image" +
                                                    " link not add to database", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // dismiss progress dialog
                            progressDialog.dismiss();

                            Toast.makeText(MainActivity.this, "Image not add", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void AddProductInfos(ProductInfos productInfos) {
        DatabaseReference productCategoryRef = databaseRef.child(productCategory).push();

        // set value to db
        productCategoryRef.setValue(productInfos)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        // clear fields value
                        mainPickedImgUri = null;
                        mainProductImg.setImageURI(null);
                        productName.getText().clear();
                        productDesc.getText().clear();
                        productPrice.getText().clear();

                        // dismiss progress dialog
                        progressDialog.dismiss();

                        // dismiss dialog
                        dialog.dismiss();

                        Toast.makeText(MainActivity.this, "Product Add", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // dismiss progress dialog
                        progressDialog.dismiss();

                        Toast.makeText(MainActivity.this, "Product don't add", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            // close drawer
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.home:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new HomeFragment()).commit();
                break;

            case R.id.clothes:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ClothesFragment()).commit();
                break;

            case R.id.shoes:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ShoesFragment()).commit();
                break;

            case R.id.top:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new TopFragment()).commit();
                break;

            case R.id.books:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new BooksFragment()).commit();
                break;

            case R.id.electronics:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ElectronicsFragment()).commit();
                break;

            case R.id.streaming:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new StreamingFragment()).commit();
                break;

            case R.id.wishlist:
                Intent wishlist = new Intent(MainActivity.this, WishList.class);
                startActivity(wishlist);
                break;

            case R.id.log_out:
                ShowLogOutDialog();
                break;
        }

        // close drawer
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    private void ShowLogOutDialog() {
        // create dialog
        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setTitle("Do you want to log out?");
        dialog.setCancelable(false);

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // dismiss dialog
                dialog.dismiss();
            }
        });

        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // log out user
                mAuth.signOut();

                // redirect user to log in page
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // show dialog
        dialog.show();
    }
}
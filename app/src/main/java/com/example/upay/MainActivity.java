package com.example.upay;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
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
import com.example.upay.UserConnections.LogOut;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ExtendedFloatingActionButton addProduct;
    private Spinner spinner;
    private ImageView mainProductImg, firstProductImg, secondProductImg, thirdProductImg, fourthProductImg;
    private TextInputLayout productNameLayout, productDescLayout, productPriceLayout;
    private TextInputEditText productName, productDesc, productPrice;
    private String productNameVal, productDescVal, productPriceVal, productCategory;
    private int currentImg;
    private ProgressDialog progressDialog;
    private Dialog dialog;
    private Uri mainPickedImgUri, firstPickedImgUri, secondPickedImgUri, thirdPickedImgUri, fourthPickedImgUri;
    private final int EXTERNAL_STORAGE_RC = 1;
    private final int MAIN_GALLERY_RC = 0;
    private final int FIRST_GALLERY_RC = 1;
    private final int SECOND_GALLERY_RC = 2;
    private final int THIRD_GALLERY_RC = 3;
    private final int FOURTH_GALLERY_RC = 4;
    private DrawerLayout drawer;
    private NavigationView navigationView;

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
    }

    private void ShowDialog() {
        // create dialog
        dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.custom_dialog_add_product);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        // retrieve all id
        mainProductImg = dialog.findViewById(R.id.main_product_img);
//        firstProductImg = dialog.findViewById(R.id.first_product_img);
//        secondProductImg = dialog.findViewById(R.id.second_product_img);
//        thirdProductImg = dialog.findViewById(R.id.third_product_img);
//        fourthProductImg = dialog.findViewById(R.id.fourth_product_img);
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
        ArrayAdapter spinnerAdapter =  ArrayAdapter.createFromResource(MainActivity.this,
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
                currentImg = 0;
                RequestPermission();
            }
        });

//        firstProductImg.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                currentImg = 1;
//                RequestPermission();
//            }
//        });
//
//        secondProductImg.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                currentImg = 2;
//                RequestPermission();
//            }
//        });
//
//        thirdProductImg.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                currentImg = 3;
//                RequestPermission();
//            }
//        });
//
//        fourthProductImg.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                currentImg = 4;
//                RequestPermission();
//            }
//        });

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

        // allow TextInputEditText to scroll
//        productDesc.setOnTouchListener(new View.OnTouchListener() {
//
//            public boolean onTouch(View v, MotionEvent event) {
//                if (productDesc.hasFocus()) {
//                    v.getParent().requestDisallowInterceptTouchEvent(true);
//                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
//                        case MotionEvent.ACTION_SCROLL:
//                            v.getParent().requestDisallowInterceptTouchEvent(false);
//                            return true;
//                    }
//                }
//                return false;
//            }
//        });

        // get 90% of the windows size
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
                    switch (currentImg) {
                        case 0:
                            OpenMainGallery();
                            break;
//                        case 1:
//                            OpenFirstGallery();
//                            break;
//                        case 2:
//                            OpenSecondGallery();
//                            break;
//                        case 3:
//                            OpenThirdGallery();
//                            break;
//                        case 4:
//                            OpenFourthGallery();
//                            break;
                    }
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

//    private void OpenFirstGallery() {
//        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
//        galleryIntent.setType("image/*");
//        startActivityForResult(galleryIntent, FIRST_GALLERY_RC);
//    }
//
//    private void OpenSecondGallery() {
//        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
//        galleryIntent.setType("image/*");
//        startActivityForResult(galleryIntent, SECOND_GALLERY_RC);
//    }
//
//    private void OpenThirdGallery() {
//        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
//        galleryIntent.setType("image/*");
//        startActivityForResult(galleryIntent, THIRD_GALLERY_RC);
//    }
//
//    private void OpenFourthGallery() {
//        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
//        galleryIntent.setType("image/*");
//        startActivityForResult(galleryIntent, FOURTH_GALLERY_RC);
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            switch (requestCode) {
                case MAIN_GALLERY_RC:
                    // get image
                    mainPickedImgUri = data.getData();

                    // set image to ImageView
                    mainProductImg.setImageURI(mainPickedImgUri);

                    break;
                case FIRST_GALLERY_RC:
                    // get image
                    firstPickedImgUri = data.getData();

                    // set image to ImageView
                    firstProductImg.setImageURI(firstPickedImgUri);

                    break;
                case SECOND_GALLERY_RC:
                    // get image
                    secondPickedImgUri = data.getData();

                    // set image to ImageView
                    secondProductImg.setImageURI(secondPickedImgUri);

                    break;
                case THIRD_GALLERY_RC:
                    // get image
                    thirdPickedImgUri = data.getData();

                    // set image to ImageView
                    thirdProductImg.setImageURI(thirdPickedImgUri);

                    break;
                case FOURTH_GALLERY_RC:
                    // get image
                    fourthPickedImgUri = data.getData();

                    // set image to ImageView
                    fourthProductImg.setImageURI(fourthPickedImgUri);

                    break;
            }
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

//        if (firstPickedImgUri != null) {
//            productImg.putFile(firstPickedImgUri)
//                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                            productImg.getDownloadUrl()
//                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
//                                        @Override
//                                        public void onSuccess(Uri uri) {
//                                            ProductInfos productInfos = new ProductInfos();
//                                            productInfos.setProductFirstImgLink(uri.toString());
//                                            Log.d("image f", "image add");
//                                            Log.d("image f", ""+uri.toString());
//                                        }
//                                    })
//                                    .addOnFailureListener(new OnFailureListener() {
//                                        @Override
//                                        public void onFailure(@NonNull Exception e) {
//                                            Toast.makeText(MainActivity.this, "Image" +
//                                                    " link not add to database", Toast.LENGTH_SHORT).show();
//                                        }
//                                    });
//                        }
//                    })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Toast.makeText(MainActivity.this, "Image not add", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//        }
//
//        if (secondPickedImgUri != null) {
//            productImg.putFile(secondPickedImgUri)
//                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                            Toast.makeText(MainActivity.this, "Image add", Toast.LENGTH_SHORT).show();
//                        }
//                    })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Toast.makeText(MainActivity.this, "Image not add", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//        }
//
//        if (thirdPickedImgUri != null) {
//            productImg.putFile(thirdPickedImgUri)
//                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                            Toast.makeText(MainActivity.this, "Image add", Toast.LENGTH_SHORT).show();
//                        }
//                    })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Toast.makeText(MainActivity.this, "Image not add", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//        }
//
//        if (fourthPickedImgUri != null) {
//            productImg.putFile(fourthPickedImgUri)
//                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                            Toast.makeText(MainActivity.this, "Image add", Toast.LENGTH_SHORT).show();
//                        }
//                    })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Toast.makeText(MainActivity.this, "Image not add", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//        }

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
                        firstPickedImgUri = null;
                        secondPickedImgUri = null;
                        thirdPickedImgUri = null;
                        fourthPickedImgUri = null;
                        mainProductImg.setImageURI(null);
//                        firstProductImg.setImageURI(null);
//                        secondProductImg.setImageURI(null);
//                        thirdProductImg.setImageURI(null);
//                        fourthProductImg.setImageURI(null);
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
                Intent logOut = new Intent(MainActivity.this, LogOut.class);
                startActivity(logOut);
                break;
        }

        // close drawer
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }
}
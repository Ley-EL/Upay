package com.example.upay.Models;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.upay.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ProductsDetailsModel extends AppCompatActivity {

    private TextView productNameTV, productPriceTV, productDescContent, infoName, infoNumber, infoAddress;
    private ImageView productMainImg, expandDescIcon, collapseDescIcon, expandInfoIcon, collapseInfoIcon;
    private ConstraintLayout productInfoContent;
    private String productImgLink, productName, productPrice, productDesc;

    private FirebaseDatabase database;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_details_model);

        // retrieve all id
        productNameTV = findViewById(R.id.product_name);
        productPriceTV = findViewById(R.id.product_price);
        productDescContent = findViewById(R.id.desc_content);
        productInfoContent = findViewById(R.id.info_content);
        productMainImg = findViewById(R.id.main_product_img);
        infoName = findViewById(R.id.info_name);
        infoNumber = findViewById(R.id.info_number);
        infoAddress = findViewById(R.id.info_address);
        expandDescIcon = findViewById(R.id.expand_desc_icon);
        collapseDescIcon = findViewById(R.id.collapse_desc_icon);
        expandInfoIcon = findViewById(R.id.expand_info_icon);
        collapseInfoIcon = findViewById(R.id.collapse_info_icon);

        // initialize firebase variable
        database = FirebaseDatabase.getInstance();
        userRef = database.getReference("Users");

        expandDescIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Expand(productDescContent);
                expandDescIcon.setVisibility(View.GONE);
                collapseDescIcon.setVisibility(View.VISIBLE);
            }
        });

        collapseDescIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Collapse(productDescContent);
                collapseDescIcon.setVisibility(View.GONE);
                expandDescIcon.setVisibility(View.VISIBLE);
            }
        });

        expandInfoIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Expand(productInfoContent);
                expandInfoIcon.setVisibility(View.GONE);
                collapseInfoIcon.setVisibility(View.VISIBLE);
            }
        });

        collapseInfoIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Collapse(productInfoContent);
                collapseInfoIcon.setVisibility(View.GONE);
                expandInfoIcon.setVisibility(View.VISIBLE);
            }
        });

        FillViewActivity();

    }

    private void FillViewActivity() {
        // get product image
        productImgLink = getIntent().getExtras().get("product_image").toString();
        // set product image
        Glide.with(ProductsDetailsModel.this).load(productImgLink).into(productMainImg);

        // get product name
        productName = getIntent().getExtras().get("product_name").toString();
        // set product name
        productNameTV.setText(productName);

        // get product price
        productPrice = getIntent().getExtras().get("product_price").toString();
        // set product price
        productPriceTV.setText(productPrice);

        // get product description
        productDesc = getIntent().getExtras().get("product_description").toString();
        // set product description
        productDescContent.setText(productDesc);

        Query query = userRef.orderByChild("name").equalTo("Upay");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);

                    // set info name
                    infoName.setText(user.getInfoName());

                    // set info number
                    infoNumber.setText(user.getInfoNumber());

                    // set info address
                    infoAddress.setText(user.getInfoAddress());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public static void Expand(final View view) {
        int matchParentMeasureSpec = View.MeasureSpec.makeMeasureSpec(((View) view.getParent()).getWidth(), View.MeasureSpec.EXACTLY);
        int wrapContentMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(matchParentMeasureSpec, wrapContentMeasureSpec);
        final int targetHeight = view.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        view.getLayoutParams().height = 1;
        view.setVisibility(View.VISIBLE);

        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                view.getLayoutParams().height = interpolatedTime == 1
                        ? ViewGroup.LayoutParams.WRAP_CONTENT
                        : (int)(targetHeight * interpolatedTime);
                view.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // Expansion speed of 1dp/ms
        a.setDuration((int)(targetHeight / view.getContext().getResources().getDisplayMetrics().density));
        view.startAnimation(a);
    }

    public static void Collapse(final View view) {
        final int initialHeight = view.getMeasuredHeight();

        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    view.setVisibility(View.GONE);
                }else{
                    view.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    view.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // Collapse speed of 1dp/ms
        a.setDuration((int)(initialHeight / view.getContext().getResources().getDisplayMetrics().density));
        view.startAnimation(a);
    }
}
package com.example.upay.Models;

public class ProductInfos {

    private String productName;
    private String productDesc;
    private String productPrice;
    private String productImgLink;
    private String productImgName;

    public ProductInfos() {}

    public ProductInfos(String productName, String productDesc, String productPrice, String productImgLink,
                        String productImgName) {
        this.productName = productName;
        this.productDesc = productDesc;
        this.productPrice = productPrice;
        this.productImgLink = productImgLink;
        this.productImgName = productImgName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductDesc() {
        return productDesc;
    }

    public void setProductDesc(String productDesc) {
        this.productDesc = productDesc;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductImgLink() {
        return productImgLink;
    }

    public void setProductImgLink(String productImgLink) {
        this.productImgLink = productImgLink;
    }

    public String getProductImgName() {
        return productImgName;
    }

    public void setProductImgName(String productImgName) {
        this.productImgName = productImgName;
    }
}

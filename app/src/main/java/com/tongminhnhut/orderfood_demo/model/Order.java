package com.tongminhnhut.orderfood_demo.model;

/**
 * Created by nhut on 2/18/2018.
 */

public class Order {
    private int ID ;
    private String ProductID ;
    private String ProductName;
    private String Quanlity ;
    private String Price ;
    private String Discount ;
    private String Image ;


    public Order() {
    }

    public Order(String productID, String productName, String quanlity, String price, String discount, String image) {
        ProductID = productID;
        ProductName = productName;
        Quanlity = quanlity;
        Price = price;
        Discount = discount;
        Image = image ;
    }

    public Order(int ID, String productID, String productName, String quanlity, String price, String discount,String image) {
        this.ID = ID;
        ProductID = productID;
        ProductName = productName;
        Quanlity = quanlity;
        Price = price;
        Discount = discount;
        Image = image ;
    }



    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getProductID() {
        return ProductID;
    }

    public void setProductID(String productID) {
        ProductID = productID;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public String getQuanlity() {
        return Quanlity;
    }

    public void setQuanlity(String quanlity) {
        Quanlity = quanlity;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getDiscount() {
        return Discount;
    }

    public void setDiscount(String discount) {
        Discount = discount;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }
}

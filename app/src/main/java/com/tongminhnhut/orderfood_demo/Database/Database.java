package com.tongminhnhut.orderfood_demo.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;
import com.tongminhnhut.orderfood_demo.model.Order;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nhut on 2/18/2018.
 */

public class Database extends SQLiteAssetHelper {
    private static final String DB_NAME = "OrderFood.db";
    private static final int DB_VER = 2 ;
    public Database(Context context) {
        super(context, DB_NAME, null, DB_VER);
    }



    public List<Order> getCart(){
        SQLiteDatabase db = getReadableDatabase() ;
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String[] sqlSelect = {"ID","ProductName","ProductID", "Quanlity", "Price", "Discount", "Image"};
        String sqlTable = "OrderDetail";

        qb.setTables(sqlTable);
        Cursor cursor = qb.query(db,sqlSelect, null, null, null, null, null);

        final List<Order> result = new ArrayList<>();
        if (cursor.moveToFirst()){
            do {
                result.add(new Order(
                        cursor.getInt(cursor.getColumnIndex("ID")),
                        cursor.getString(cursor.getColumnIndex("ProductID")),
                        cursor.getString(cursor.getColumnIndex("ProductName")),
                        cursor.getString(cursor.getColumnIndex("Quanlity")),
                        cursor.getString(cursor.getColumnIndex("Price")),
                        cursor.getString(cursor.getColumnIndex("Discount")),
                        cursor.getString(cursor.getColumnIndex("Image"))

                        ));
            }while (cursor.moveToNext()) ;

        }
        return result;


    }

    public void addToCart(Order order){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("INSERT INTO OrderDetail (ProductID,ProductName,Quanlity,Price,Discount,Image) VALUES ('%s','%s','%s','%s','%s','%s'); ",
                order.getProductID(),
                order.getProductName(),
                order.getQuanlity(),
                order.getPrice(),
                order.getDiscount(),
                order.getImage());
        db.execSQL(query);

    }

    public void cleanCart(){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("DELETE FROM OrderDetail");
        db.execSQL(query);

    }

    public int getCountCart() {
        int count = 0 ;
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("SELECT COUNT(*) FROM OrderDetail ");
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()){
            do {
                count = cursor.getInt(0);
            }while (cursor.moveToNext());
        }
        return count ;
    }

    public void updateCart(Order order) {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("UPDATE OrderDetail SET Quanlity= %s WHERE ID = %d", order.getQuanlity(), order.getID());
        db.execSQL(query);
    }




    // function Favorite

    public void addFavorite(String foodID){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("INSERT INTO Favorite(FoodID) VALUES('%s');",foodID );
        db.execSQL(query);
    }

    public void removeFavorite(String foodID){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("DELETE FROM Favorite  WHERE FoodID= '%s' ;",foodID );
        db.execSQL(query);
    }

    public boolean isFavorite(String foodID){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("SELECT * FROM Favorite WHERE FoodID = '%s' ;",foodID );
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.getCount()<=0){
            cursor.close();
            return false ;
        }
        cursor.close();
        return true ;
    }

    public void removeFromCart(String id) {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("DELETE From OderDetail WHERE ID = %d",id );
        db.execSQL(query);
    }

}

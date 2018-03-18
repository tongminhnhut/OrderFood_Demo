package com.tongminhnhut.orderfood_demo.model;

import java.util.List;

/**
 * Created by nhut on 2/19/2018.
 */

public class Requests {
    private String Phone ;
    private String Name ;
    private String Address;
    private String Total ;
    private String status ;
    private String Note ;
    private String Date ;
    private List<Order> foods ;

    public Requests() {
    }

    public Requests(String phone, String name, String address, String total,String note, String date ,List<Order> foods) {
        Phone = phone;
        Name = name;
        Address = address;
        Total = total;
        this.foods = foods;
        this.status = "0";// 0: Tại chỗ,  1:Đang ship ,   2: Đã ship
        Note = note ;
        Date = date;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getNote() {
        return Note;
    }

    public void setNote(String note) {
        Note = note;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getTotal() {
        return Total;
    }

    public void setTotal(String total) {
        Total = total;
    }

    public List<Order> getFoods() {
        return foods;
    }

    public void setFoods(List<Order> foods) {
        this.foods = foods;
    }
}

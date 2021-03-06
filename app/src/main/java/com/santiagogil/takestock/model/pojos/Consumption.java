package com.santiagogil.takestock.model.pojos;

import com.google.firebase.database.PropertyName;
import com.santiagogil.takestock.util.DatabaseHelper;

import java.util.Date;
import java.util.UUID;

/**
 * Created by digitalhouse on 24/12/16.
 */

public class Consumption {

    @PropertyName(DatabaseHelper.ID)
    private String ID;

    @PropertyName(DatabaseHelper.DATE)
    private Long date;

    @PropertyName(DatabaseHelper.ITEMID)
    private String itemID;

    @PropertyName(DatabaseHelper.PRICE)
    private Double price;

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Consumption(){

    }

    public Consumption(Item item) {
        this.ID = UUID.randomUUID().toString();
        this.date = System.currentTimeMillis();
        this.itemID = item.getID();
        this.price = item.getPrice();
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public void setItemID(String itemID) {
        this.itemID = itemID;
    }

    public String getID() {

        return ID;
    }

    public Long getDate() {
        return date;
    }

    public String getItemID() {
        return itemID;
    }

    public Date getDateAsDateObject(){
        return new Date(getDate());
    }
}

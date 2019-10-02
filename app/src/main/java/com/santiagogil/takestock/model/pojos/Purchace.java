package com.santiagogil.takestock.model.pojos;


import com.google.firebase.database.PropertyName;
import com.santiagogil.takestock.util.DatabaseHelper;

import java.util.UUID;

public class Purchace {

    @PropertyName(DatabaseHelper.ID)
    private String ID;

    @PropertyName(DatabaseHelper.DATE)
    private Long date;

    @PropertyName(DatabaseHelper.ITEMID)
    private String itemID;

    @PropertyName(DatabaseHelper.PRICE)
    private Double price;

    @PropertyName(DatabaseHelper.AMMOUNT)
    private Integer ammount;

    public Purchace(Item item) {
        this.ID = UUID.randomUUID().toString();;
        this.date = System.currentTimeMillis();
        this.itemID = item.getID();
        this.price = item.getPrice();
        this.ammount = item.getMinimumPurchaceQuantity();
    }

    public Purchace(Item item, Integer cart) {
        this.ID = UUID.randomUUID().toString();;
        this.date = System.currentTimeMillis();
        this.itemID = item.getID();
        this.price = item.getPrice();
        this.ammount = item.getUnitsInCart();
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

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setAmmount(Integer ammount) {
        this.ammount = ammount;
    }

    public Purchace() {

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

    public Double getPrice() {
        return price;
    }

    public Integer getAmmount() {
        return ammount;
    }


}

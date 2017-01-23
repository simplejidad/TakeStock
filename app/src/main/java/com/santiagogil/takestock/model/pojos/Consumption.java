package com.santiagogil.takestock.model.pojos;

import com.google.firebase.database.PropertyName;
import com.santiagogil.takestock.model.daos.DatabaseHelper;

/**
 * Created by digitalhouse on 24/12/16.
 */

public class Consumption {

    @PropertyName(DatabaseHelper.ID)
    private String ID;

    @PropertyName(DatabaseHelper.DATE)
    private Integer date;

    @PropertyName(DatabaseHelper.ITEMID)
    private String itemID;

    public Consumption(String itemID) {
        this.itemID = itemID;
    }

    public Consumption(){

    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public void setDate(Integer date) {
        this.date = date;
    }

    public void setItemID(String itemID) {
        this.itemID = itemID;
    }

    public String getID() {

        return ID;
    }

    public Integer getDate() {
        return date;
    }

    public String getItemID() {
        return itemID;
    }
}

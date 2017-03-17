package com.santiagogil.takestock.model.pojos;

import com.google.firebase.database.PropertyName;
import com.santiagogil.takestock.util.DatabaseHelper;

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

    private String userID;

    public Consumption(){

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
}

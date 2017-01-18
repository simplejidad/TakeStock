package com.santiagogil.takestock.model.pojos;

/**
 * Created by digitalhouse on 24/12/16.
 */

public class Consumption {

    private String ID;
    private Integer dateOfConsumption;
    private String itemID;

    public Consumption(String itemID) {
        this.itemID = itemID;
    }

    public Consumption(){

    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public void setDateOfConsumption(Integer dateOfConsumption) {
        this.dateOfConsumption = dateOfConsumption;
    }

    public void setItemID(String itemID) {
        this.itemID = itemID;
    }

    public String getID() {

        return ID;
    }

    public Integer getDateOfConsumption() {
        return dateOfConsumption;
    }

    public String getItemID() {
        return itemID;
    }
}

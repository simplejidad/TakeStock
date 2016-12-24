package com.santiagogil.takestock.model.pojos;

import java.util.Date;

/**
 * Created by digitalhouse on 24/12/16.
 */

public class Consumption {

    private Long ID;
    private Integer dateOfConsumption;
    private Integer itemID;

    public Consumption(Integer itemID) {
        this.itemID = itemID;
    }

    public Consumption(){

    }

    public void setID(Long ID) {
        this.ID = ID;
    }

    public void setDateOfConsumption(Integer dateOfConsumption) {
        this.dateOfConsumption = dateOfConsumption;
    }

    public void setItemID(Integer itemID) {
        this.itemID = itemID;
    }

    public Long getID() {

        return ID;
    }

    public Integer getDateOfConsumption() {
        return dateOfConsumption;
    }

    public Integer getItemID() {
        return itemID;
    }
}

package com.santiagogil.takestock.model.pojos;

import java.util.Date;

/**
 * Created by digitalhouse on 24/12/16.
 */

public class Consumption {

    private Date dateOfConsumption;
    private Item item;
    private Integer quantity;

    public Consumption(Date date, Item item, Integer quantity) {
        this.dateOfConsumption = new Date();
        this.item = item;
        this.quantity = quantity;
    }

}

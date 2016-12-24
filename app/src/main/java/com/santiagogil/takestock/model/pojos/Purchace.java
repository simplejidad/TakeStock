package com.santiagogil.takestock.model.pojos;

import java.util.Date;

public class Purchace {

    private Date dateOfPurchace;
    private Item item;
    private Integer quantity;

    public Purchace(Item item, Integer quantity) {
        this.dateOfPurchace = new Date();
        this.item = item;
        this.quantity = quantity;
        updateStock();
    }

    public void updateStock(){
        item.setStock(item.getStock() + quantity);
    }
}

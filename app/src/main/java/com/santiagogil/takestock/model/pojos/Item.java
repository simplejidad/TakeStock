package com.santiagogil.takestock.model.pojos;

/**
 * Created by digitalhouse on 24/12/16.
 */
public class Item {

    static final Integer DEFAULT_CONSUMPTION_RATE = 90;

    private Integer ID;
    private String name;
    private Integer stock;
    private Integer minimumPurchaceQuantity;
    private Integer image;
    private Integer consumptionRate;


    public Item(){

    }

    public Integer getConsumptionRate() {
        return consumptionRate;
    }

    public void setConsumptionRate(Integer consumptionRate) {
        this.consumptionRate = consumptionRate;
    }

    public Item(String name) {
        this.name = name;
        this.stock = 0;
        minimumPurchaceQuantity = 1;
        image = 0;
        consumptionRate = DEFAULT_CONSUMPTION_RATE;

    }

    public String getName() {
        return name;
    }

    public Integer getStock() {
        return stock;
    }

    public Integer getMinimumPurchaceQuantity() {
        return minimumPurchaceQuantity;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Integer getID() {
        return ID;
    }

    public Integer getImage() {
        return image;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMinimumPurchaceQuantity(Integer minimumPurchaceQuantity) {
        this.minimumPurchaceQuantity = minimumPurchaceQuantity;
    }

    public void setImage(Integer image) {
        this.image = image;
    }
}

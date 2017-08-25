package com.santiagogil.takestock.model.pojos;

import com.google.firebase.database.PropertyName;
import com.santiagogil.takestock.R;
import com.santiagogil.takestock.util.DatabaseHelper;

import java.io.Serializable;

public class Item implements Serializable {

    public static final Integer DEFAULT_CONSUMPTION_RATE = 90;

    @PropertyName(DatabaseHelper.ID)
    private String ID;
    @PropertyName(DatabaseHelper.USERID)
    private String userID;
    @PropertyName(DatabaseHelper.NAME)
    private String name;
    @PropertyName(DatabaseHelper.STOCK)
    private Integer stock;
    @PropertyName(DatabaseHelper.MINIMUMPURCHACEQUANTITY)
    private Integer minimumPurchaceQuantity;
    @PropertyName(DatabaseHelper.IMAGE)
    private Integer image;
    @PropertyName(DatabaseHelper.CONSUMPTIONRATE)
    private Integer consumptionRate;
    @PropertyName (DatabaseHelper.ACTIVE)
    private Boolean active;
    @PropertyName (DatabaseHelper.PRICE)
    private Double price;
    @PropertyName (DatabaseHelper.CART)
    private Integer cart;


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
        active = true;
        price = 0.0;
        consumptionRate = DEFAULT_CONSUMPTION_RATE;
        cart = 0;

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

    public String getID() {
        return ID;
    }

    public Integer getImage() {
        return image;
    }

    public void setID(String ID) {
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

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public Integer getIndependence(){
        return Math.round(getConsumptionRate() * (getStock() + getCart()));
    }

    public Integer getCart() {
        return cart;
    }

    public void setCart(Integer cart) {
        this.cart = cart;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getRoundedIndependence() {

        Integer independence = getIndependence();

        if (independence >= 365) {
            return round(independence / 365, 2) + " years";

        } else if (independence >= 30) {

            return round(independence / 30, 2).toString() + " months";

        } else if (independence >= 7) {

            return  round(independence / 7, 2) + " weeks";

        } else {
            return Math.round(getConsumptionRate() * getStock()) + " days";
        }
    }

    private static Double round (double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }

    public String getRoundedConsumptionRate() {

        if(getConsumptionRate() > 0){

            Integer independence = getIndependence();

            if(independence >= 365){
                return "";
            } else if (independence > 90 ){
                return Math.max(round((365-independence)/getConsumptionRate(), 0).intValue(), 1) + " till ";
            } else if(independence > 30){
                return Math.max(round((90-independence)/getConsumptionRate(), 0).intValue(), 1)  + " till ";
            } else if (independence > 7) {
                return Math.max(round((30-independence)/getConsumptionRate(), 0).intValue(), 1)  + " till ";
            } else if (independence >= 0) {
                return Math.max(round((7-independence)/getConsumptionRate(), 0).intValue(), 1)  + " till ";
            } else {
                return "";
            }
        } else{
            return "";
        }
    }

    public Integer getIndependenceEmoticon() {

        Integer independence = getIndependence();

        if(getConsumptionRate() > 0) {

            if (getCart() == 0) {

                if (independence > 90) {
                    return R.drawable.ic_emoticon_excited;
                } else if (independence > 30) {
                    return R.drawable.ic_emoticon_happy;
                } else if (independence > 7) {
                    return R.drawable.ic_emoticon_smile;
                } else if (independence > 0) {
                    return R.drawable.ic_emoticon_neutral;
                } else {
                    return R.drawable.ic_emoticon_sad_24dp;
                }
            } else {

                if (independence > 90) {
                    return R.drawable.ic_emoticon_excited_potential;
                } else if (independence > 30) {
                    return R.drawable.ic_emoticon_happy_potential;
                } else if (independence > 7) {
                    return R.drawable.ic_emoticon_smile_potential;
                } else if (independence > 0) {
                    return R.drawable.ic_emoticon_neutral_potential;
                } else {
                    return R.drawable.ic_emoticon_sad_24dp_potential;
                }
            }

        } else {

            return R.drawable.ic_emoticon_neutral;
        }
    }

    public Integer getNextIndependenceEmoticon(){

        Integer independence = getIndependence();

        if (independence >= 90) {
                return 0;
        } else if (independence > 30) {
            return R.drawable.ic_emoticon_excited;
        } else if (independence > 7) {
            return R.drawable.ic_emoticon_happy;
        } else if (independence > 0) {
            return R.drawable.ic_emoticon_smile;
        } else {
            return R.drawable.ic_emoticon_neutral;
        }

    }
}

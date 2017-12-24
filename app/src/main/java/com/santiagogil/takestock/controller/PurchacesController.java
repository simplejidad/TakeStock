package com.santiagogil.takestock.controller;


import android.content.Context;

import com.santiagogil.takestock.model.daos.PurchacesDAO;
import com.santiagogil.takestock.model.pojos.Consumption;
import com.santiagogil.takestock.model.pojos.Item;
import com.santiagogil.takestock.model.pojos.Purchace;

import java.util.List;

public class PurchacesController {

    public void addPurchaceToDatabases(Context context, Item item){

        Purchace purchace = new Purchace(item);
        PurchacesDAO purchacesDAO = new PurchacesDAO();
        purchacesDAO.addPurchaceToLocalDB(context, purchace);
        purchacesDAO.addPurchaceToFirebaseDB(purchace);

    }

    public void addPurchaceFromCart(Context context, Item item, Integer itemCart){

        Purchace purchace = new Purchace(item, item.getCart());
        PurchacesDAO purchacesDAO = new PurchacesDAO();
        purchacesDAO.addPurchaceToLocalDB(context, purchace);
        purchacesDAO.addPurchaceToFirebaseDB(purchace);

    }

    public List<Purchace> sortedItemConsumptionList(Context context, String itemID) {

        PurchacesDAO purchacesDAO = new PurchacesDAO();
        return purchacesDAO.getSortedPurchacesByDateDescending(getItemPurchaceList(context, itemID));
    }

    public List<Purchace> getItemPurchaceList(Context context, String itemID){
        PurchacesDAO purchacesDAO = new PurchacesDAO();
        return purchacesDAO.getItemPurchacesList(context, itemID);

    }

    public void deletePurchace(Context context, Purchace purchace) {

        PurchacesDAO purchacesDAO = new PurchacesDAO();
        purchacesDAO.deletePurchace(context, purchace);
    }
}

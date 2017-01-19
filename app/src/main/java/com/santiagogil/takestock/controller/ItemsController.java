package com.santiagogil.takestock.controller;

import android.content.Context;

import com.santiagogil.takestock.model.daos.ItemsDAO;
import com.santiagogil.takestock.model.pojos.Item;
import com.santiagogil.takestock.util.ResultListener;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class ItemsController {

    public void getItemsSortedAlphabetically(final Context context, final ResultListener<List<Item>> listenerFromView) {
        final ItemsDAO itemsDao = new ItemsDAO(context);

        itemsDao.getAllItemsFromLocalDBSortedAlphabetically(new ResultListener<List<Item>>() {
            @Override
            public void finish(List<Item> result) {

                if (result.size() > 0) {

                    listenerFromView.finish(result);

                } else itemsDao.getItemsFromFirebase(new ResultListener<List<Item>>() {
                    @Override
                    public void finish(List<Item> result) {
                        itemsDao.addItemsToLocalDatabase(result);

                        listenerFromView.finish(result);
                    }
                });
            }
        });
    }

    public void deleteItemFromDatabases(Context context, Long ID){
        ItemsDAO itemsDAO = new ItemsDAO(context);
        itemsDAO.deleteItemFromDatabases(ID);

    }

    public String addItemToDatabases(Context context, Item item){
        ItemsDAO itemsDao = new ItemsDAO(context);
        return itemsDao.addItemToDatabases(item);

    }

    public void increaseItemStock(Context context, Item item){

        ItemsDAO itemsDao = new ItemsDAO(context);
        itemsDao.increaseItemStock(item);

    }

    public void decreaseItemStock(Context context, Item item){

        ItemsDAO itemsDAO = new ItemsDAO(context);
        itemsDAO.decreaseItemStock(item);
    }

    public List<Item> getItemsFromLocalDBsortedAlphabetically(Context context){
        ItemsDAO itemsDAO = new ItemsDAO(context);
        return itemsDAO.getAllItemsFromLocalDBSortedAlphabetically();
    }

    public List<Item> getItemsFromLocalDatabase(Context context){
        ItemsDAO itemsDAO = new ItemsDAO(context);
        return itemsDAO.getItemsFromLocalDBSortedByID();
    }

    public void addItemToLocalDatabase(Context context, Item item){
        ItemsDAO itemsDAO = new ItemsDAO(context);
        itemsDAO.addItemToLocalDB(item);
    }

    public void updateItemConsumptionDate(Context context, String itemID){

        ConsumptionController consumptionsController = new ConsumptionController();
        Integer updatedConsumptionRate = consumptionsController.getItemConsumptionRate(context, itemID);
        ItemsDAO itemsDAO = new ItemsDAO(context);
        itemsDAO.updateItemConsumptionRateInDatabases(itemID, updatedConsumptionRate);

    }

    public void updateItemDetails(Context context, String itemID, String itemName, Integer itemStock, Integer itemConsumptionRate, Integer itemMinimumPurchace ){
        ItemsDAO itemsDAO = new ItemsDAO(context);
        itemsDAO.updateItemDetails(itemID, itemName, itemStock, itemConsumptionRate, itemMinimumPurchace);
    }
}

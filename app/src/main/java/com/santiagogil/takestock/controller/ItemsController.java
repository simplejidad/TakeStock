package com.santiagogil.takestock.controller;

import android.content.Context;
import android.widget.Toast;

import com.santiagogil.takestock.model.daos.ItemsDAOLocalDB;
import com.santiagogil.takestock.model.pojos.Consumption;
import com.santiagogil.takestock.model.pojos.Item;
import com.santiagogil.takestock.util.ResultListener;

import java.util.List;


public class ItemsController {

    public void toggleItemIsActiveInDatabases(Context context, String itemID){
        ItemsDAOLocalDB itemsDAOLocalDB = new ItemsDAOLocalDB(context);
        itemsDAOLocalDB.toggleItemIsActiveInDatabases(itemID);

    }

    public String addItemToDatabases(Context context, Item item){
        ItemsDAOLocalDB itemsDaoLocalDB = new ItemsDAOLocalDB(context);
        return itemsDaoLocalDB.addItemToDatabases(item);

    }

    public void increaseItemStock(Context context, Item item){

        ItemsDAOLocalDB itemsDaoLocalDB = new ItemsDAOLocalDB(context);
        itemsDaoLocalDB.increaseItemStock(item);

    }

    public void increaseItemStock(Context context, String itemID){

        ItemsDAOLocalDB itemsDaoLocalDB = new ItemsDAOLocalDB(context);
        itemsDaoLocalDB.increaseItemStock(itemID);

    }

    public void decreaseItemStock(Context context, Item item){

        ItemsDAOLocalDB itemsDAOLocalDB = new ItemsDAOLocalDB(context);
        itemsDAOLocalDB.decreaseItemStock(item);
    }

    public void addItemToLocalDatabase(Context context, Item item){
        ItemsDAOLocalDB itemsDAOLocalDB = new ItemsDAOLocalDB(context);
        itemsDAOLocalDB.addItemToLocalDB(item);
    }

    public void updateItemConsumptionRate(Context context, String itemID){

        ItemsDAOLocalDB itemsDAOLocalDB = new ItemsDAOLocalDB(context);
        ConsumptionsController consumptionsController = new ConsumptionsController();
        List<Consumption> consumptions = consumptionsController.getItemConsumptionList(context, itemID);
        if(consumptions.size() > 1){
            Integer updatedConsumptionRate = consumptionsController.getItemConsumptionRate(context, itemID);
            itemsDAOLocalDB.updateItemConsumptionRateInDatabases(itemID, updatedConsumptionRate);
        }
        if(consumptions.size() == 0 || consumptions.size() == 1){
            itemsDAOLocalDB.updateItemConsumptionRateInDatabases(itemID, Item.DEFAULT_CONSUMPTION_RATE);
        }
    }

    public void updateItemDetails(Context context, String itemID, String itemName, Integer itemStock, Integer itemConsumptionRate, Integer itemMinimumPurchace, Boolean itemActiveStatus ){
        ItemsDAOLocalDB itemsDAOLocalDB = new ItemsDAOLocalDB(context);
        itemsDAOLocalDB.updateItemDetails(itemID, itemName, itemStock, itemConsumptionRate, itemMinimumPurchace, itemActiveStatus);
    }


    public Item getItemFromLocalDatabase(Context context, String itemID) {

        ItemsDAOLocalDB itemsDAOLocalDB = new ItemsDAOLocalDB(context);
        return itemsDAOLocalDB.getItemFromLocalDB(itemID);
    }

    public List<Item> sortItemsAlphabetically(Context context, List<Item> itemList){

        ItemsDAOLocalDB itemsDAOLocalDB = new ItemsDAOLocalDB(context);
        return itemsDAOLocalDB.sortItemsAlphabetically(itemList);
    }

    public List<Item> getActiveItemsByIndependence(Context context, Integer independence) {

        ItemsDAOLocalDB itemsDaoLocalDB = new ItemsDAOLocalDB(context);

        return  itemsDaoLocalDB.getActiveItemsByIndependence(independence);

    }

    public List<Item> sortItemsByIndependence(Context context, List<Item> itemList) {

        ItemsDAOLocalDB itemsDAOLocalDB = new ItemsDAOLocalDB(context);
        return itemsDAOLocalDB.sortItemsByIndependence(itemList);
    }

    public Integer getItemConsumptionRate(Context context, String itemID) {

        ItemsDAOLocalDB itemsDAOLocalDB = new ItemsDAOLocalDB(context);
        return itemsDAOLocalDB.getItemConsumptionRate(itemID);
    }

    public void updateItemsDatabase(final Context context, final ResultListener<List<Item>> listenerFromFragment) {

        final ItemsDAOLocalDB itemsDAOLocalDB = new ItemsDAOLocalDB(context);

        List<Item> itemList = itemsDAOLocalDB.getAllItemsFromLocalDB();

        if (itemList.size() > 0) {

            listenerFromFragment.finish(itemList);

        } else {

            itemsDAOLocalDB.getItemsFromFirebase(new ResultListener<List<Item>>() {
                @Override
                public void finish(List<Item> result) {
                    for (Item item : result) {
                        addItemToLocalDatabase(context, item);
                    }

                    if (result.size() > 0) {

                        listenerFromFragment.finish(result);

                    } else {

                        itemsDAOLocalDB.retrieveItemsFromDefaultFirebaseList(new ResultListener<List<Item>>() {
                            @Override
                            public void finish(List<Item> result) {


                                if (result.size() > 0) {

                                    for (Item item : result) {
                                        addItemToDatabases(context, item);
                                        listenerFromFragment.finish(result);
                                    }
                                } else {
                                    Toast.makeText(context, "ItemsDao.retrieveDefaultItems FAILED", Toast.LENGTH_SHORT).show();
                                    listenerFromFragment.finish(result);
                                }
                            }
                        });

                    }
                }
            });
        }
    }

    public List<Item> getAllActiveItems(Context context) {

        ItemsDAOLocalDB itemsDaoLocalDB = new ItemsDAOLocalDB(context);
        return itemsDaoLocalDB.getActiveItemsFromLocalDB();
    }

    public List<Item> getAllItemsWithStockZero(Context context) {
        ItemsDAOLocalDB itemsDAOLocalDB = new ItemsDAOLocalDB(context);
        return itemsDAOLocalDB.getAllItemsWithStockZero();
    }

    public List<Item> sortItemsByConsumptionRate(Context context, List<Item> itemList) {

        ItemsDAOLocalDB itemsDAOLocalDB = new ItemsDAOLocalDB(context);
        return itemsDAOLocalDB.sortItemsByConsumptionRate(itemList);
    }

    public List<Item> getAllInactiveItems(Context context) {

        ItemsDAOLocalDB itemsDAOLocalDB = new ItemsDAOLocalDB(context);
        return itemsDAOLocalDB.getInactiveItemsFromLocalDB();
    }

    public void activateItemInDatabases(Context context, String id) {
    }
}

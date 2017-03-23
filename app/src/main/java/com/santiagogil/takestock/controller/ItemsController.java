package com.santiagogil.takestock.controller;

import android.content.Context;

import com.santiagogil.takestock.model.daos.ConsumptionsDAO;
import com.santiagogil.takestock.model.daos.ItemsDAO;
import com.santiagogil.takestock.model.pojos.Consumption;
import com.santiagogil.takestock.model.pojos.Item;
import com.santiagogil.takestock.util.ResultListener;

import java.util.ArrayList;
import java.util.List;


public class ItemsController {

    public void deleteItemFromDatabases(Context context, String itemID){
        ItemsDAO itemsDAO = new ItemsDAO(context);
        itemsDAO.deleteItemFromDatabases(itemID);

    }

    public String addItemToDatabases(Context context, Item item){
        ItemsDAO itemsDao = new ItemsDAO(context);
        return itemsDao.addItemToDatabases(item);

    }

    public void increaseItemStock(Context context, Item item){

        ItemsDAO itemsDao = new ItemsDAO(context);
        itemsDao.increaseItemStock(item);

    }

    public void increaseItemStock(Context context, String itemID){

        ItemsDAO itemsDao = new ItemsDAO(context);
        itemsDao.increaseItemStock(itemID);

    }

    public void decreaseItemStock(Context context, Item item){

        ItemsDAO itemsDAO = new ItemsDAO(context);
        itemsDAO.decreaseItemStock(item);
    }

    public void addItemToLocalDatabase(Context context, Item item){
        ItemsDAO itemsDAO = new ItemsDAO(context);
        itemsDAO.addItemToLocalDB(item);
    }

    public void updateItemConsumptionRate(Context context, String itemID){

        ItemsDAO itemsDAO = new ItemsDAO(context);
        ConsumptionsController consumptionsController = new ConsumptionsController();
        List<Consumption> consumptions = consumptionsController.getItemConsumptionList(context, itemID);
        if(consumptions.size() > 1){
            Integer updatedConsumptionRate = consumptionsController.getItemConsumptionRate(context, itemID);
            itemsDAO.updateItemConsumptionRateInDatabases(itemID, updatedConsumptionRate);
        }
        if(consumptions.size() == 0 || consumptions.size() == 1){
            itemsDAO.updateItemConsumptionRateInDatabases(itemID, Item.DEFAULT_CONSUMPTION_RATE);
        }
    }

    public void updateItemDetails(Context context, String itemID, String itemName, Integer itemStock, Integer itemConsumptionRate, Integer itemMinimumPurchace, Boolean itemActiveStatus ){
        ItemsDAO itemsDAO = new ItemsDAO(context);
        itemsDAO.updateItemDetails(itemID, itemName, itemStock, itemConsumptionRate, itemMinimumPurchace, itemActiveStatus);
    }

    public Item getItemFromLocalDatabase(Context context, String itemID) {

        ItemsDAO itemsDAO = new ItemsDAO(context);
        return itemsDAO.getItemFromLocalDB(itemID);
    }

    public List<Item> sortItemsAlphabetically(Context context, List<Item> itemList){

        ItemsDAO itemsDAO = new ItemsDAO(context);
        return itemsDAO.sortItemsAlphabetically(itemList);
    }

    public List<Item> getActiveItemsByIndependence(Context context, Integer independence) {

        ItemsDAO itemsDao = new ItemsDAO(context);

        return  itemsDao.getActiveItemsByIndependence(independence);

    }

    public List<Item> sortItemsByIndependence(Context context, List<Item> itemList) {

        ItemsDAO itemsDAO = new ItemsDAO(context);
        return itemsDAO.sortItemsByIndependence(itemList);
    }

    public Integer getItemConsumptionRate(Context context, String itemID) {

        ItemsDAO itemsDAO = new ItemsDAO(context);
        return itemsDAO.getItemConsumptionRate(itemID);
    }

    public void updateItemsDatabase(final Context context, final ResultListener<List<Item>> listenerFromFragment) {

        ItemsDAO itemsDAO = new ItemsDAO(context);

        List<Item> itemList = itemsDAO.getAllItemsFromLocalDB();

        if (itemList.size() > 0) {

            listenerFromFragment.finish(itemList);

        } else {

            itemsDAO.getItemsFromFirebase(new ResultListener<List<Item>>() {
                @Override
                public void finish(List<Item> result) {
                    for (Item item : result) {
                        addItemToLocalDatabase(context, item);
                    }
                    listenerFromFragment.finish(result);
                }
            });

        }
    }

    public List<Item> getAllActiveItems(Context context) {

        ItemsDAO itemsDao = new ItemsDAO(context);
        return itemsDao.getActiveItemsFromLocalDB();
    }

    public List<Item> getAllItemsWithStockZero(Context context) {
        ItemsDAO itemsDAO = new ItemsDAO(context);
        return itemsDAO.getAllItemsWithStockZero();
    }

    public List<Item> sortItemsByConsumptionRate(Context context, List<Item> itemList) {

        ItemsDAO itemsDAO = new ItemsDAO(context);
        return itemsDAO.sortItemsByConsumptionRate(itemList);
    }

    public List<Item> getAllInactiveItems(Context context) {

        ItemsDAO itemsDAO = new ItemsDAO(context);
        return itemsDAO.getInactiveItemsFromLocalDB();
    }
}

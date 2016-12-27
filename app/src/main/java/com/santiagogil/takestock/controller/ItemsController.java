package com.santiagogil.takestock.controller;

import android.content.Context;

import com.santiagogil.takestock.model.daos.ConsumptionsDAO;
import com.santiagogil.takestock.model.daos.ItemsDAO;
import com.santiagogil.takestock.model.pojos.Item;
import com.santiagogil.takestock.util.ResultListener;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class ItemsController {

    public void getItems(final Context context, final ResultListener<List<Item>> listenerFromView) {
        final ItemsDAO itemsDao = new ItemsDAO(context);

        itemsDao.getItemsFromLocalDB(new ResultListener<List<Item>>() {
            @Override
            public void finish(List<Item> result) {

                if (result.size() > 0) {

                    Collections.sort(result, new Comparator<Item>(){
                        public int compare (Item o1, Item o2){
                            return o1.getName().compareTo(o2.getName());
                        }
                    });

                    listenerFromView.finish(result);

                } else itemsDao.getItemsFromFirebase(new ResultListener<List<Item>>() {
                    @Override
                    public void finish(List<Item> result) {
                        itemsDao.addItemsToLocalDatabase(result);

                        Collections.sort(result, new Comparator<Item>(){
                            public int compare (Item o1, Item o2){
                                return o1.getName().compareTo(o2.getName());
                            }
                        });
                        
                        listenerFromView.finish(result);
                    }
                });
            }
        });
    }

    public void deleteItemFromDatabases(Context context, Integer ID){
        ItemsDAO itemsDAO = new ItemsDAO(context);
        itemsDAO.deleteItemFromDatabases(ID);

    }

    public void addItemToDatabases(Context context, Item item){
        ItemsDAO itemsDao = new ItemsDAO(context);
        itemsDao.addItemToDatabases(item);
    }

    public void increaseItemStock(Context context, Item item){

        ItemsDAO itemsDao = new ItemsDAO(context);
        itemsDao.increaseItemStock(item);

    }

    public void decreaseItemStock(Context context, Item item){

        ItemsDAO itemsDAO = new ItemsDAO(context);
        itemsDAO.decreaseItemStock(item);
    }

    public List<Item> getItemsFromLocalDatabase(Context context){
        ItemsDAO itemsDAO = new ItemsDAO(context);
        return itemsDAO.getItemsFromLocalDB();
    }

    public void addItemToLocalDatabase(Context context, Item item){
        ItemsDAO itemsDAO = new ItemsDAO(context);
        itemsDAO.addItemToLocalDB(item);
    }

    public void updateItemConsumptionDate(Context context, Integer itemID){

        ConsumptionController consumptionsController = new ConsumptionController();
        Integer updatedConsumptionDate = consumptionsController.getItemConsumptionRate(context, itemID);
        ItemsDAO itemsDAO = new ItemsDAO(context);
        itemsDAO.updateItemConsumptionRateInDatabases(itemID, updatedConsumptionDate);

    }
}

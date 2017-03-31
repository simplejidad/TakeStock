package com.santiagogil.takestock.controller;

import android.content.Context;

import com.santiagogil.takestock.model.daos.ConsumptionsDAO;
import com.santiagogil.takestock.model.pojos.Consumption;
import com.santiagogil.takestock.util.ResultListener;

import java.util.List;

public class ConsumptionsController {

    public void updateConsumptionsDatabase(final Context context, final ResultListener<List<Consumption>> listenerFromFragment) {

        ConsumptionsDAO consumptionsDAO = new ConsumptionsDAO(context);

        List<Consumption> consumptions;

        consumptions = consumptionsDAO.getConsumptionsFromLocalDB();

        if(consumptions.size() > 0) {

            listenerFromFragment.finish(consumptions);

        } else {
            consumptionsDAO.getConsumptionsFromFirebase(new ResultListener<List<Consumption>>(){
                @Override
                public void finish(List<Consumption> result) {
                    addConsumptionsToDatabase(context, result);
                    listenerFromFragment.finish(result);
                }
            });
        }
    }

    private void addConsumptionsToDatabase(Context context, List<Consumption> consumptionsFromFirebase) {

        for(Consumption consumption : consumptionsFromFirebase){
            addConsumptionToLocalDB(context, consumption);
        }
    }

    private void addConsumptionToLocalDB(Context context, Consumption consumption) {

        ConsumptionsDAO consumptionsDAO = new ConsumptionsDAO(context);
        consumptionsDAO.addConsumptionToLocalDB(consumption);
    }

    public void addConsumptionToDatabases(Context context, String itemID){

        ConsumptionsDAO consumptionsDAO = new ConsumptionsDAO(context);
        consumptionsDAO.addConsumptionToDatabases(itemID);

    }

    public Integer getItemConsumptionRate(Context context, String itemID){

        ItemsController itemsController = new ItemsController();
        ConsumptionsDAO consumptionsDAO = new ConsumptionsDAO(context);
        List<Consumption> consumptionList = consumptionsDAO.getItemConsumptionList(itemID);
        if(consumptionList.size() > 1){
            return consumptionsDAO.getItemConsumptionRate(itemID);
        } else {
            return itemsController.getItemConsumptionRate(context, itemID);
        }

    }

    public List<Consumption> getConsumptions(Context context){
        ConsumptionsDAO consumptionsDAO = new ConsumptionsDAO(context);
        return consumptionsDAO.getConsumptions();
    }

    public List<Consumption> getItemConsumptionList(Context context, String itemID){
        ConsumptionsDAO consumptionsDAO = new ConsumptionsDAO(context);
        return consumptionsDAO.getItemConsumptionList(itemID);

    }

    public void deleteConsumption(Context context, Consumption consumption){
        ConsumptionsDAO consumptionsDAO = new ConsumptionsDAO(context);
        consumptionsDAO.deleteConsumption(consumption);
    }

    public List<Consumption> sortedItemConsumptionList(Context context, String itemID){
        ConsumptionsDAO consumptionsDAO = new ConsumptionsDAO(context);
        return consumptionsDAO.getSortedConsumptionsByDateDescending(getItemConsumptionList(context, itemID));

    }
}


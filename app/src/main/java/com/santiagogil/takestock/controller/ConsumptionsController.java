package com.santiagogil.takestock.controller;

import android.content.Context;

import com.santiagogil.takestock.model.daos.ConsumptionsDAO;
import com.santiagogil.takestock.model.pojos.Consumption;
import com.santiagogil.takestock.util.ResultListener;

import java.util.List;

public class ConsumptionsController {

    public void updateConsumptionsDatabase(final Context context) {

        ConsumptionsDAO consumptionsDAO = new ConsumptionsDAO(context);

        if(consumptionsDAO.getConsumptionsFromLocalDB().size() == 0){
            consumptionsDAO.getConsumptionsFromFirebase(new ResultListener<List<Consumption>>(){
                @Override
                public void finish(List<Consumption> result) {
                    addConsumptionsToDatabase(context, result);
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

        ConsumptionsDAO consumptionsDAO = new ConsumptionsDAO(context);
        return consumptionsDAO.getItemConsumptionRate(itemID);

    }

    public List<Consumption> getConsumptions(Context context){
        ConsumptionsDAO consumptionsDAO = new ConsumptionsDAO(context);
        return consumptionsDAO.getConsumptions();
    }


}


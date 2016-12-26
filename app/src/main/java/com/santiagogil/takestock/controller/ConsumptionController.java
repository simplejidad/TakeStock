package com.santiagogil.takestock.controller;

import android.content.Context;

import com.santiagogil.takestock.model.daos.ConsumptionsDAO;

public class ConsumptionController {

    public void addConsumptionToDatabases(Context context, Integer itemID){

        ConsumptionsDAO consumptionsDAO = new ConsumptionsDAO(context);
        consumptionsDAO.addConsumptionToDatabases(itemID);

    }

    public Integer getItemConsumptionRate(Context context, Integer itemID){

        ConsumptionsDAO consumptionsDAO = new ConsumptionsDAO(context);
        return consumptionsDAO.getItemConsumptionRate(itemID);

    }


}


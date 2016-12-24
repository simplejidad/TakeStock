package com.santiagogil.takestock.controller;

import android.content.Context;

import com.santiagogil.takestock.model.daos.ConsumptionsDAO;
import com.santiagogil.takestock.model.pojos.Consumption;

public class ConsumptionController {



    public void addConsumptionToDatabases(Context context, Integer itemID){

        ConsumptionsDAO consumptionsDAO = new ConsumptionsDAO(context);
        consumptionsDAO.addConsumptionToDatabases(itemID);

    }

}


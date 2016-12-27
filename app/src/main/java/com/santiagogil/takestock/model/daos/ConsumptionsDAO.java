package com.santiagogil.takestock.model.daos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.santiagogil.takestock.model.pojos.Consumption;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ConsumptionsDAO{

    private static final String TABLECONSUMPTIONS = "Consumptions";
    private static final String ID = "ID";
    private static final String DATE = "Date";
    private static final String ITEMID = "ItemID";

    private Context context;
    private DatabaseHelper databaseHelper;

    public ConsumptionsDAO(Context context) {
        this.context = context;
        databaseHelper = new DatabaseHelper(context);
    }

    public void addConsumptionToDatabases(Integer itemID) {
        Long consumptionID = addConsumptionToLocalDB(itemID);
        AddConsumptionToFirebaseTask addConsumptionToFirebaseTask = new AddConsumptionToFirebaseTask(getConsumptionFromLocalDB(consumptionID));
        addConsumptionToFirebaseTask.execute();
    }

    public Consumption getConsumptionFromLocalDB(Long consumptionID){

        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLECONSUMPTIONS + " WHERE " + ID + " = " + consumptionID;

        Cursor cursor = database.rawQuery(selectQuery, null);
        if(cursor.moveToNext()){

            Consumption consumption = new Consumption();
            consumption.setID(cursor.getLong(cursor.getColumnIndex(ID)));
            consumption.setDateOfConsumption(cursor.getInt(cursor.getColumnIndex(DATE)));
            consumption.setItemID(cursor.getInt(cursor.getColumnIndex(ITEMID)));

            cursor.close();

            return consumption;
        }

        return null;
    }

    public Integer currentDateInDays(){

        return (Integer) (int) (System.currentTimeMillis()/1000/60/60/24);
    }

    public Long addConsumptionToLocalDB(Integer itemID){

        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        ContentValues row = new ContentValues();

        row.put(DATE, currentDateInDays());
        row.put(ITEMID, itemID);

        Long id = database.insert(TABLECONSUMPTIONS, null, row);

        database.close();

        return id;

    }

    private void addConsumptionToFirebase(Consumption consumption){

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase.getReference();
        myRef.child("consumptions").child(consumption.getID().toString()).setValue(consumption);

    }

    public Integer getItemConsumptionRate(Integer itemID){

        SQLiteDatabase database = new DatabaseHelper(context).getReadableDatabase();

        String sqlQuery = "SELECT * FROM " + TABLECONSUMPTIONS
                + " WHERE " + ITEMID + " = " + itemID;

        Cursor cursor = database.rawQuery(sqlQuery, null);

        List<Integer> dates = new ArrayList();

        while(cursor.moveToNext()){

            dates.add(cursor.getInt(cursor.getColumnIndex(DATE)));
        }

        cursor.close();

        Collections.sort(dates);

        List<Integer> differences = new ArrayList<>();

        for(Integer i = 1; i < dates.size(); i++){

            differences.add(dates.get(i) - dates.get(i-1));

        }

        Integer consumptionRate = 0;

        for(Integer difference : differences){
            consumptionRate = consumptionRate + difference;
        }

        if(dates.size()>1){
            return consumptionRate/(dates.size()-1);
        }

        return consumptionRate;
    }

    private class AddConsumptionToFirebaseTask extends AsyncTask <String, Void, Void>{

        private Consumption consumption;

        public AddConsumptionToFirebaseTask(Consumption consumption){
            this.consumption = consumption;
        }

        @Override
        protected Void doInBackground(String... strings) {

            addConsumptionToFirebase(consumption);

            return null;
        }
    }
}


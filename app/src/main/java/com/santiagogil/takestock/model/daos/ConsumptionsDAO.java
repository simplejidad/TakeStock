package com.santiagogil.takestock.model.daos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.santiagogil.takestock.controller.ItemsController;
import com.santiagogil.takestock.model.pojos.Consumption;
import com.santiagogil.takestock.util.DatabaseHelper;
import com.santiagogil.takestock.util.FirebaseHelper;
import com.santiagogil.takestock.util.ResultListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ConsumptionsDAO{

    private Context context;
    private DatabaseHelper databaseHelper;
    private FirebaseHelper firebaseHelper;

    public ConsumptionsDAO(Context context) {
        this.context = context;
        databaseHelper = new DatabaseHelper(context);
        firebaseHelper = new FirebaseHelper();
    }

    public void addConsumptionToDatabases(Consumption consumption) {
        addConsumptionToLocalDB(consumption);
        addConsumptionToFirebase(consumption);
    }

    public Consumption getConsumptionFromLocalDB(String consumptionID){

        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + DatabaseHelper.TABLECONSUMPTIONS + " WHERE " + DatabaseHelper.ID + " = " + '"' + consumptionID + '"';

        Cursor cursor = database.rawQuery(selectQuery, null);
        if(cursor.moveToNext()){

            Consumption consumption = new Consumption();
            consumption.setID(cursor.getString(cursor.getColumnIndex(DatabaseHelper.ID)));
            consumption.setDate(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.DATE)));
            consumption.setItemID(cursor.getString(cursor.getColumnIndex(DatabaseHelper.ITEMID)));

            cursor.close();
            database.close();
            return consumption;
        }

        return null;
    }

    private void addConsumptionToFirebase(Consumption consumption){


        DatabaseReference myRef = firebaseHelper.getUserDB().child(DatabaseHelper.TABLECONSUMPTIONS).child(consumption.getID());
        myRef.child(DatabaseHelper.ID).setValue(consumption.getID());
        myRef.child(DatabaseHelper.DATE).setValue(consumption.getDate());
        myRef.child(DatabaseHelper.ITEMID).setValue(consumption.getItemID());
        myRef.child(DatabaseHelper.PRICE).setValue(consumption.getPrice());

    }

    public Integer getItemConsumptionRate(String itemID){

        SQLiteDatabase database = new DatabaseHelper(context).getReadableDatabase();

        String sqlQuery = "SELECT * FROM " + DatabaseHelper.TABLECONSUMPTIONS
                + " WHERE " + DatabaseHelper.ITEMID
                + " = " + '"' + itemID + '"';

        Cursor cursor = database.rawQuery(sqlQuery, null);

        List<Long> dates = new ArrayList();

        while(cursor.moveToNext()){

            dates.add(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.DATE)));
        }

        cursor.close();

        Collections.sort(dates);

        List<Integer> differences = new ArrayList<>();

        for(Integer i = 1; i < dates.size(); i++){

            differences.add(fromMiliseccondsToInteger(dates.get(i) - dates.get(i-1)));

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

    public Integer fromMiliseccondsToInteger(Long miliseconds){
       return (Integer) (int) (miliseconds/1000/60/60/24);
    }

    public void addConsumptionToLocalDB(Consumption consumption) {

        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        ContentValues row = new ContentValues();

        row.put(DatabaseHelper.ID, consumption.getID());
        row.put(DatabaseHelper.DATE, consumption.getDate());
        row.put(DatabaseHelper.ITEMID, consumption.getItemID());
        row.put(DatabaseHelper.PRICE, consumption.getPrice());

        database.insert(DatabaseHelper.TABLECONSUMPTIONS, null, row);

        database.close();

    }

    public void deleteConsumption(Consumption consumption) {

        deleteConsumptionFromLocalDatabase(consumption);
        deleteConsumptionFromFirebase(consumption);
        ItemsController itemsController = new ItemsController();
        itemsController.increaseItemStock(context, consumption.getItemID());
        itemsController.updateItemConsumptionRate(context, consumption.getItemID());


    }

    private void deleteConsumptionFromLocalDatabase(Consumption consumption) {
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        try {
            database.delete(DatabaseHelper.TABLECONSUMPTIONS, DatabaseHelper.ID + " = " + '"' + consumption.getID() + '"', null);
            database.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void deleteConsumptionFromFirebase(Consumption consumption) {
        DatabaseReference userDB = firebaseHelper.getUserDB();
        userDB.child(DatabaseHelper.TABLECONSUMPTIONS).child(consumption.getID()).removeValue();
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

    public List<Consumption> getConsumptions(){
        return getConsumptionsFromLocalDB();
    }

    public List<Consumption> getConsumptionsFromLocalDB(){

        List<Consumption> consumptions = new ArrayList<>();

        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        String sqlQuerry = "SELECT * FROM " + DatabaseHelper.TABLECONSUMPTIONS;

        Cursor cursor = database.rawQuery(sqlQuerry, null);

        while(cursor.moveToNext()){

            Consumption consumption = new Consumption();
            consumption.setID(cursor.getString(cursor.getColumnIndex(DatabaseHelper.ID)));
            consumption.setDate(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.DATE)));
            consumption.setItemID(cursor.getString(cursor.getColumnIndex(DatabaseHelper.ITEMID)));

            consumptions.add(consumption);
        }

        cursor.close();
        database.close();

        return consumptions;

    }

    public void getConsumptionsFromFirebase(final ResultListener<List<Consumption>> resultListener){

        final List<Consumption> consumptions = new ArrayList<>();

        DatabaseReference dbRef = firebaseHelper.getUserDB().child(DatabaseHelper.TABLECONSUMPTIONS);

        dbRef.addListenerForSingleValueEvent(new ValueEventListener()   {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()){
                    Consumption consumption = data.getValue(Consumption.class);
                    consumptions.add(consumption);
                }
                resultListener.finish(consumptions);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Toast.makeText(context, "itemsDAO.getConsumptionsFromFirebase FAILED", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public List<Consumption> getItemConsumptionList(String itemID){

        List<Consumption> consumptionList = new ArrayList<>();

        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + DatabaseHelper.TABLECONSUMPTIONS
                + " WHERE " + DatabaseHelper.ITEMID
                + " = " + '"' + itemID + '"';

        Cursor cursor = database.rawQuery(selectQuery, null);
        while(cursor.moveToNext()) {

            Consumption consumption = new Consumption();
            consumption.setID(cursor.getString(cursor.getColumnIndex(DatabaseHelper.ID)));
            consumption.setDate(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.DATE)));
            consumption.setItemID(cursor.getString(cursor.getColumnIndex(DatabaseHelper.ITEMID)));

            consumptionList.add(consumption);
        }

            cursor.close();
            database.close();
            return consumptionList;
        }

    public List<Consumption> getSortedConsumptionsByDateDescending(List<Consumption> consumptions){
        List<Consumption> sortedConsumptions = new ArrayList<>();
        if(consumptions.size() > 1){
            Collections.sort(consumptions, new Comparator<Consumption>(){
                public int compare (Consumption o1, Consumption o2){
                    return o1.getDate().compareTo(o2.getDate());
                }
            });
            for(Integer i = consumptions.size() - 1; i>= 0; i--){
                sortedConsumptions.add(consumptions.get(i));
            }
            return  sortedConsumptions;
        }
        return consumptions;
    }
}


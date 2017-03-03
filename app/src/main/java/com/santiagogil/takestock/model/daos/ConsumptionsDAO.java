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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.santiagogil.takestock.model.pojos.Consumption;
import com.santiagogil.takestock.model.pojos.Item;
import com.santiagogil.takestock.util.FirebaseHelper;
import com.santiagogil.takestock.util.ResultListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class ConsumptionsDAO{

    private Context context;
    private DatabaseHelper databaseHelper;
    private FirebaseHelper firebaseHelper;

    public ConsumptionsDAO(Context context) {
        this.context = context;
        databaseHelper = new DatabaseHelper(context);
        firebaseHelper = new FirebaseHelper();

    }

    public void addConsumptionToDatabases(String itemID) {
        String consumptionID = addConsumptionToLocalDB(itemID);
        AddConsumptionToFirebaseTask addConsumptionToFirebaseTask = new AddConsumptionToFirebaseTask(getConsumptionFromLocalDB(consumptionID));
        addConsumptionToFirebaseTask.execute();
    }

    public Consumption getConsumptionFromLocalDB(String consumptionID){

        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + DatabaseHelper.TABLECONSUMPTIONS + " WHERE " + DatabaseHelper.ID + " = " + '"' + consumptionID + '"';

        Cursor cursor = database.rawQuery(selectQuery, null);
        if(cursor.moveToNext()){

            Consumption consumption = new Consumption();
            consumption.setID(cursor.getString(cursor.getColumnIndex(DatabaseHelper.ID)));
            consumption.setDate(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.DATE)));
            consumption.setItemID(cursor.getString(cursor.getColumnIndex(DatabaseHelper.ITEMID)));

            cursor.close();
            database.close();
            return consumption;
        }

        return null;
    }

    public Integer currentDateInDays(){

        return (Integer) (int) (System.currentTimeMillis()/1000/60/60/24);
    }

    public String addConsumptionToLocalDB(String itemID){

        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        ContentValues row = new ContentValues();

        String consumptionID = UUID.randomUUID().toString();

        row.put(DatabaseHelper.ID, consumptionID);
        row.put(DatabaseHelper.DATE, currentDateInDays());
        row.put(DatabaseHelper.ITEMID, itemID);

        database.insert(DatabaseHelper.TABLECONSUMPTIONS, null, row);

        database.close();

        return consumptionID;

    }

    private void addConsumptionToFirebase(Consumption consumption){


        DatabaseReference myRef = firebaseHelper.getUserDB();
        myRef.child(DatabaseHelper.TABLECONSUMPTIONS).child(consumption.getID()).child(DatabaseHelper.ID).setValue(consumption.getID());
        myRef.child(DatabaseHelper.TABLECONSUMPTIONS).child(consumption.getID()).child(DatabaseHelper.DATE).setValue(consumption.getDate());
        myRef.child(DatabaseHelper.TABLECONSUMPTIONS).child(consumption.getID()).child(DatabaseHelper.ITEMID).setValue(consumption.getItemID());

    }

    public Integer getItemConsumptionRate(String itemID){

        SQLiteDatabase database = new DatabaseHelper(context).getReadableDatabase();

        String sqlQuery = "SELECT * FROM " + DatabaseHelper.TABLECONSUMPTIONS
                + " WHERE " + DatabaseHelper.ITEMID
                + " = " + '"' + itemID + '"';

        Cursor cursor = database.rawQuery(sqlQuery, null);

        List<Integer> dates = new ArrayList();

        while(cursor.moveToNext()){

            dates.add(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.DATE)));
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

    public void addConsumptionToLocalDB(Consumption consumption) {

        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        ContentValues row = new ContentValues();

        row.put(DatabaseHelper.ID, consumption.getID());
        row.put(DatabaseHelper.DATE, consumption.getDate());
        row.put(DatabaseHelper.ITEMID, consumption.getItemID());

        database.insert(DatabaseHelper.TABLECONSUMPTIONS, null, row);

        database.close();

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
            consumption.setDate(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.DATE)));
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
            consumption.setDate(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.DATE)));
            consumption.setItemID(cursor.getString(cursor.getColumnIndex(DatabaseHelper.ITEMID)));

            consumptionList.add(consumption);
        }

            cursor.close();
            database.close();
            return consumptionList;
        }

}


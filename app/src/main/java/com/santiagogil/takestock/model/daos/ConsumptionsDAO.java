package com.santiagogil.takestock.model.daos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.SystemClock;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.santiagogil.takestock.model.pojos.Consumption;

/**
 * Created by digitalhouse on 24/12/16.
 */
public class ConsumptionsDAO extends SQLiteOpenHelper {

    private static final String DATABASENAME = "ConsumptionsDB";
    private static final Integer DATABASEVERSION = 1;

    private static final String TABLECONSUMPTIONS = "Consumptions";
    private static final String ID = "ID";
    private static final String DATE = "Date";
    private static final String ITEMID = "ItemID";

    private Context context;

    public ConsumptionsDAO(Context context) {
        super(context, DATABASENAME, null , DATABASEVERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String createTable = "CREATE TABLE " + TABLECONSUMPTIONS + "("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + DATE + " TEXT,"
                + ITEMID + " NUMBER "
                + ")";

        sqLiteDatabase.execSQL(createTable);


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void addConsumptionToDatabases(Integer itemID) {
        Long consumptionID = addConsumptionToLocalDB(itemID);
        AddConsumptionToFirebaseTask addConsumptionToFirebaseTask = new AddConsumptionToFirebaseTask(getConsumptionFromLocalDB(consumptionID));
        addConsumptionToFirebaseTask.execute();
    }

    public Consumption getConsumptionFromLocalDB(Long consumptionID){

        SQLiteDatabase database = getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLECONSUMPTIONS + " WHERE " + ID + " = " + consumptionID;

        Cursor cursor = database.rawQuery(selectQuery, null);
        if(cursor.moveToNext()){

            Consumption consumption = new Consumption();
            consumption.setID(cursor.getLong(cursor.getColumnIndex(ID)));
            consumption.setDateOfConsumption(cursor.getInt(cursor.getColumnIndex(DATE)));
            consumption.setItemID(cursor.getInt(cursor.getColumnIndex(ITEMID)));

            return consumption;
        }

        return null;
    }

    public Integer currentDateInDays(){

        Integer date = (int) (System.currentTimeMillis()/1000/60/60/24);

        return date;
    }

    public Long addConsumptionToLocalDB(Integer itemID){

        SQLiteDatabase database = getWritableDatabase();

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


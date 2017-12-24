package com.santiagogil.takestock.model.daos;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;

import com.google.firebase.database.DatabaseReference;
import com.santiagogil.takestock.controller.ItemsController;
import com.santiagogil.takestock.model.pojos.Consumption;
import com.santiagogil.takestock.model.pojos.Item;
import com.santiagogil.takestock.model.pojos.Purchace;
import com.santiagogil.takestock.util.DatabaseHelper;
import com.santiagogil.takestock.util.FirebaseHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class PurchacesDAO {

    public Purchace addPurchaceToLocalDB(Context context, Purchace purchace) {

        DatabaseHelper databaseHelper = new DatabaseHelper(context);

        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        ContentValues row = new ContentValues();

        row.put(DatabaseHelper.ID, purchace.getID());
        row.put(DatabaseHelper.DATE, purchace.getDate());
        row.put(DatabaseHelper.ITEMID, purchace.getItemID());
        row.put(DatabaseHelper.AMMOUNT, purchace.getAmmount());
        row.put(DatabaseHelper.PRICE, purchace.getPrice());

        database.insert(DatabaseHelper.TABLEPURCHACES, null, row);

        database.close();

        return purchace;
    }

    public void addPurchaceToFirebaseDB(Purchace purchace){

        FirebaseHelper firebaseHelper = new FirebaseHelper();

        DatabaseReference myRef = firebaseHelper.getUserDB().child(DatabaseHelper.TABLEPURCHACES).child(purchace.getID());
        myRef.child(DatabaseHelper.ID).setValue(purchace.getID());
        myRef.child(DatabaseHelper.DATE).setValue(purchace.getDate());
        myRef.child(DatabaseHelper.ITEMID).setValue(purchace.getItemID());
        myRef.child(DatabaseHelper.AMMOUNT).setValue(purchace.getAmmount());
        myRef.child(DatabaseHelper.PRICE).setValue(purchace.getPrice());

    }

    public List<Purchace> getItemPurchacesList(Context context, String itemID) {

        List<Purchace> purchaceList = new ArrayList<>();

        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + DatabaseHelper.TABLEPURCHACES
                + " WHERE " + DatabaseHelper.ITEMID
                + " = " + '"' + itemID + '"';

        Cursor cursor = database.rawQuery(selectQuery, null);
        while(cursor.moveToNext()) {

            Purchace purchace = new Purchace();
            purchace.setID(cursor.getString(cursor.getColumnIndex(DatabaseHelper.ID)));
            purchace.setDate(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.DATE)));
            purchace.setItemID(cursor.getString(cursor.getColumnIndex(DatabaseHelper.ITEMID)));
            purchace.setPrice(cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.PRICE)));
            purchace.setAmmount(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.AMMOUNT)));

            purchaceList.add(purchace);
        }

        cursor.close();
        database.close();
        return purchaceList;
    }

    public List<Purchace> getSortedPurchacesByDateDescending(List<Purchace> purchaceList) {

        List<Purchace> sortedPurchaces = new ArrayList<>();

        if(purchaceList.size() > 1){

            Collections.sort(purchaceList, new Comparator<Purchace>(){
                public int compare (Purchace o1, Purchace o2){
                    return o1.getDate().compareTo(o2.getDate());
                }
            });


            for(Integer i = 0; i < purchaceList.size(); i++){
                sortedPurchaces.add(purchaceList.get(i));
            }

            return  sortedPurchaces;
        }

        return purchaceList;

    }

    public void deletePurchace(Context context, Purchace purchace) {

        deletePurchaceFromLocalDatabase(context, purchace);
        deletePurchaceFromFirebase(purchace);
        ItemsController itemsController = new ItemsController();
        itemsController.decreaseItemStock(context, itemsController.getItemFromLocalDatabase(context, purchace.getItemID()));

    }

    private void deletePurchaceFromLocalDatabase(Context context, Purchace purchace) {

        DatabaseHelper databaseHelper = new DatabaseHelper(context);

        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        try {

            database.delete(DatabaseHelper.TABLEPURCHACES, DatabaseHelper.ID + " = " + '"' + purchace.getID() + '"', null);
            database.close();

        } catch (Exception e){

            e.printStackTrace();

        }
    }

    private void deletePurchaceFromFirebase(Purchace purchace) {

        FirebaseHelper firebaseHelper = new FirebaseHelper();
        DatabaseReference userDB = firebaseHelper.getUserDB();
        userDB.child(DatabaseHelper.TABLEPURCHACES).child(purchace.getID()).removeValue();
    }
}

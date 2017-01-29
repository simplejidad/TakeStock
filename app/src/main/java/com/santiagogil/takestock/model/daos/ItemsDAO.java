package com.santiagogil.takestock.model.daos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.santiagogil.takestock.model.pojos.Item;
import com.santiagogil.takestock.util.ResultListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class ItemsDAO{

    //private List<Item> items = new ArrayList<>();
    private Context context;
    private DatabaseHelper databaseHelper;

    public ItemsDAO (Context context){
        this.context = context;
        databaseHelper = new DatabaseHelper(context);
    }

    public String addItemToDatabases(final Item itemWithoutID) {

        addItemToLocalDB(itemWithoutID);
        Item itemWithID = getItemFromLocalDBUsingItemName(itemWithoutID.getName());
        addItemToFirebase(itemWithID);
        return itemWithID.getID();
    }

    public void updateItemConsumptionRateInLocalDB(String itemID, Integer consumptionRate){

        SQLiteDatabase database = new DatabaseHelper(context).getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.CONSUMPTIONRATE, consumptionRate);

        database.update(DatabaseHelper.TABLEITEMS, contentValues, DatabaseHelper.ID + " = " + '"' + itemID + '"', null);

    }


    public void getAllItemsFromLocalDBSortedAlphabetically(final ResultListener<List<Item>> listenerFromController){

        List<Item> itemList = getAllItemsFromLocalDB();

        listenerFromController.finish(sortItemsAlphabetically(itemList));

    }

    public void getActiveItemsFromLocalDBSortedAlphabetically(final ResultListener<List<Item>> listenerFromController){

        List<Item> itemList = getActiveItemsFromLocalDB();

        listenerFromController.finish(sortItemsAlphabetically(itemList));

    }



    public void addItemToLocalDB(Item item){

        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        ContentValues row = new ContentValues();

        if(item.getID() != null){
            row.put(DatabaseHelper.ID, item.getID());
        } else {
            row.put(DatabaseHelper.ID, UUID.randomUUID().toString());
        }
        row.put(DatabaseHelper.NAME, item.getName());
        row.put(DatabaseHelper.STOCK, item.getStock());
        row.put(DatabaseHelper.IMAGE, item.getImage());
        row.put(DatabaseHelper.MINIMUMPURCHACEQUANTITY, item.getMinimumPurchaceQuantity());
        row.put(DatabaseHelper.CONSUMPTIONRATE, item.getConsumptionRate());
        row.put(DatabaseHelper.ACTIVE, booleanToInteger(item.getActive()));

        database.insert(DatabaseHelper.TABLEITEMS, null, row);

        database.close();
    }

    public void addItemToFirebase(Item item){

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase.getReference();
        myRef.child(DatabaseHelper.TABLEITEMS).child(item.getID()).child(DatabaseHelper.ID).setValue(item.getID());
        updateItemDetails(item.getID(), item.getName(), item.getStock(), item.getConsumptionRate(), item.getMinimumPurchaceQuantity(), item.getActive());

    }

    public List<Item> getAllItemsFromLocalDBSortedAlphabetically(){

        List<Item> itemList = getAllItemsFromLocalDB();

        return sortItemsAlphabetically(itemList);
    }

    public List<Item> sortItemsAlphabetically(List<Item> itemList) {

        if(itemList.size() > 1){

            Collections.sort(itemList, new Comparator<Item>(){
                public int compare (Item o1, Item o2){
                    return o1.getName().compareTo(o2.getName());
                }
            });
        }


        return itemList;

    }

    public Item getItemFromLocalDBUsingItemName(String itemName){

        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + DatabaseHelper.TABLEITEMS + " WHERE " + DatabaseHelper.NAME + " = " + '"'
                +  itemName + '"';
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToNext()) {

            Item item = new Item();
            item.setID(cursor.getString(cursor.getColumnIndex(DatabaseHelper.ID)));
            item.setImage(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.IMAGE)));
            item.setMinimumPurchaceQuantity(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.MINIMUMPURCHACEQUANTITY)));
            item.setName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.NAME)));
            item.setStock(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.STOCK)));
            item.setConsumptionRate(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.CONSUMPTIONRATE)));
            item.setActive(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.ACTIVE)) > 0);

            cursor.close();
            database.close();

            return item;
        }
        Toast.makeText(context, "getItemFromLocalDBUsingItemName FAILED", Toast.LENGTH_SHORT).show();
        return null;
    }

    public Item getItemFromLocalDB(String itemID){

        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + DatabaseHelper.TABLEITEMS + " WHERE " + DatabaseHelper.ID + " = " + '"'
                +  itemID + '"';
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToNext()){

            Item item = new Item();
            item.setID(cursor.getString(cursor.getColumnIndex(DatabaseHelper.ID)));
            item.setImage(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.IMAGE)));
            item.setMinimumPurchaceQuantity(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.MINIMUMPURCHACEQUANTITY)));
            item.setName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.NAME)));
            item.setStock(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.STOCK)));
            item.setConsumptionRate(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.CONSUMPTIONRATE)));
            item.setActive(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.ACTIVE)) > 0);

            cursor.close();

            return item;

        }

        return null;
    }

    public void getItemsFromFirebase(final ResultListener<List<Item>> listenerFromController){

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = firebaseDatabase.getReference().child(DatabaseHelper.TABLEITEMS);
        final List<Item> itemList = new ArrayList<>();

        dbRef.addListenerForSingleValueEvent(new ValueEventListener()   {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()){
                    Item item = data.getValue(Item.class);
                    itemList.add(item);
                }
                listenerFromController.finish(itemList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Toast.makeText(context, "itemsDAO.getItemsFromFirebase FAILED", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public List<Item> getAllItemsFromLocalDB() {

        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + DatabaseHelper.TABLEITEMS;
        Cursor cursor = database.rawQuery(selectQuery, null);

        List<Item> items = new ArrayList<>();

        while (cursor.moveToNext()) {

            Item item = new Item();
            item.setID(cursor.getString(cursor.getColumnIndex(DatabaseHelper.ID)));
            item.setImage(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.IMAGE)));
            item.setMinimumPurchaceQuantity(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.MINIMUMPURCHACEQUANTITY)));
            item.setName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.NAME)));
            item.setStock(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.STOCK)));
            item.setConsumptionRate(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.CONSUMPTIONRATE)));
            item.setActive(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.ACTIVE))>0);

            items.add(item);

        }
        cursor.close();
        database.close();
        return items;
    }

    public List<Item> getActiveItemsFromLocalDB() {

        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + DatabaseHelper.TABLEITEMS + " WHERE " + DatabaseHelper.ACTIVE
                + " = " + '"' + DatabaseHelper.ACTIVE_TRUE + '"';
        Cursor cursor = database.rawQuery(selectQuery, null);

        List<Item> items = new ArrayList<>();

        while (cursor.moveToNext()) {

            Item item = new Item();
            item.setID(cursor.getString(cursor.getColumnIndex(DatabaseHelper.ID)));
            item.setImage(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.IMAGE)));
            item.setMinimumPurchaceQuantity(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.MINIMUMPURCHACEQUANTITY)));
            item.setName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.NAME)));
            item.setStock(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.STOCK)));
            item.setConsumptionRate(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.CONSUMPTIONRATE)));
            item.setActive(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.ACTIVE))>0);

            items.add(item);

        }
        cursor.close();
        database.close();
        return items;
    }

    public void increaseItemStock(Item item){

        Integer newStock = item.getStock() + 1;

        updateItemStock(item, newStock);

    }

    public void decreaseItemStock(Item item){

        Integer newStock = item.getStock() - 1;

        updateItemStock(item, newStock);

    }

    public void updateItemStock(final Item item, final Integer newStock){

        SQLiteDatabase database =  databaseHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.STOCK, newStock);

        database.update(DatabaseHelper.TABLEITEMS,  contentValues, DatabaseHelper.ID + " = " + '"' +  item.getID() + '"' , null);

        database.close();

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.getReference().child(DatabaseHelper.TABLEITEMS).child(item.getID()).child(DatabaseHelper.STOCK).setValue(newStock);

    }

    public void updateItemDetails(String itemID, String updatedItemName, Integer updatedItemStock, Integer updatedConsumptionRate, Integer updatedMinimumPurchace, Boolean active){

        updateItemDetailsOnLocalDatabase(itemID, updatedItemName, updatedItemStock, updatedConsumptionRate,updatedMinimumPurchace, active);
        updateItemDetailsOnFirebase(itemID, updatedItemName, updatedItemStock, updatedConsumptionRate,updatedMinimumPurchace, active);

    }

    public void updateItemDetailsOnLocalDatabase(String itemID, String updatedItemName, Integer updatedItemStock, Integer updatedConsumptionRate, Integer updatedMinimumPurchace, Boolean active){

        SQLiteDatabase database =  databaseHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.NAME, updatedItemName);
        contentValues.put(DatabaseHelper.STOCK, updatedItemStock);
        contentValues.put(DatabaseHelper.CONSUMPTIONRATE, updatedConsumptionRate);
        contentValues.put(DatabaseHelper.MINIMUMPURCHACEQUANTITY, updatedMinimumPurchace);
        contentValues.put(DatabaseHelper.ACTIVE, booleanToInteger(active));

        database.update(DatabaseHelper.TABLEITEMS,  contentValues, DatabaseHelper.ID + " = " + '"' + itemID + '"' , null);

        database.close();
    }

    private Integer booleanToInteger(Boolean activeStatus){
        if(activeStatus){
            return 1;
        } else {
            return 0;
        }
    }

    public void updateItemDetailsOnFirebase(String itemID, String updatedItemName, Integer updatedItemStock, Integer updatedConsumptionRate, Integer updatedMinimumPurchace, Boolean active){

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase.getReference().child(DatabaseHelper.TABLEITEMS).child(itemID);
        myRef.child(DatabaseHelper.ID).setValue(itemID);
        myRef.child(DatabaseHelper.NAME).setValue(updatedItemName);
        myRef.child(DatabaseHelper.STOCK).setValue(updatedItemStock);
        myRef.child(DatabaseHelper.CONSUMPTIONRATE).setValue(updatedConsumptionRate);
        myRef.child(DatabaseHelper.MINIMUMPURCHACEQUANTITY).setValue(updatedMinimumPurchace);
        myRef.child(DatabaseHelper.ACTIVE).setValue(active);

    }


    public void addItemsToLocalDatabase(List<Item> items){
        for (Item item : items){
            addItemToLocalDB(item);
        }
    }

    public void deleteItemFromDatabases(String itemID){

        deleteItemFromLocalDB(itemID);
        deleteItemFromFirebase(itemID);

    }

    public void deleteItemFromFirebase(String itemID) {

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase.getReference();
        myRef.child(DatabaseHelper.TABLEITEMS).child(itemID).child(DatabaseHelper.ACTIVE).setValue(false);
    }

    public void deleteItemFromLocalDB(String itemID){

        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        try {

            database.delete(DatabaseHelper.TABLEITEMS, DatabaseHelper.ID + " = " + '"' + itemID + '"', null);
            database.close();

        } catch (Exception e){

            e.printStackTrace();

        }
    }

    public void updateItemConsumptionRateInDatabases(String itemID, Integer consumptionRate){

        updateItemConsumptionRateInLocalDB(itemID, consumptionRate);
        updateItemConsumptionRateInFirebase(itemID, consumptionRate);
    }

    public void updateItemConsumptionRateInFirebase(String itemID, Integer consumptionRate){

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.getReference().child(DatabaseHelper.TABLEITEMS).child(itemID).child(DatabaseHelper.CONSUMPTIONRATE).setValue(consumptionRate);

    }
}
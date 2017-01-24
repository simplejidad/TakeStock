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

    private List<Item> items = new ArrayList<>();
    private Context context;
    private DatabaseHelper databaseHelper;

    public ItemsDAO (Context context){
        this.context = context;
        databaseHelper = new DatabaseHelper(context);
    }

    public String addItemToDatabases(final Item itemWithoutID) {

        addItemToLocalDB(itemWithoutID);
        Item itemWithID = getItemFromLocalDB(itemWithoutID.getName());
        //AddItemToFirebaseTask addItemToFirebaseTask = new AddItemToFirebaseTask(itemWithID);
        //addItemToFirebaseTask.execute();
        addItemToFirebase(itemWithID);
        return itemWithID.getID();
    }

    public void addItemToFirebase(Item item){

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase.getReference();
        myRef.child(DatabaseHelper.TABLEITEMS).child(item.getID()).child(DatabaseHelper.ID).setValue(item.getID());
        updateItemDetails(item.getID(), item.getName(), item.getStock(), item.getConsumptionRate(), item.getMinimumPurchaceQuantity());

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

        database.insert(DatabaseHelper.TABLEITEMS, null, row);

        database.close();
    }

    public void getAllItemsFromLocalDBSortedAlphabetically(final ResultListener<List<Item>> listenerFromController){

        List<Item> items = getItemsFromLocalDBSortedByID();
        if(items.size() > 1) {
            Collections.sort(items, new Comparator<Item>() {
                public int compare(Item o1, Item o2) {
                    return o1.getName().compareTo(o2.getName());
                }
            });
        }

        listenerFromController.finish(items);

    }

    public List<Item> getAllItemsFromLocalDBSortedAlphabetically(){

        List<Item> items = getItemsFromLocalDBSortedByID();
        if(items.size() > 1){

            Collections.sort(items, new Comparator<Item>(){
                public int compare (Item o1, Item o2){
                    return o1.getName().compareTo(o2.getName());
                }
            });
        }


        return items;

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

            cursor.close();

            return item;

        }

        return null;
    }

    public void getItemsFromFirebase(final ResultListener<List<Item>> listenerFromController){

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = firebaseDatabase.getReference().child(DatabaseHelper.TABLEITEMS);

        dbRef.addListenerForSingleValueEvent(new ValueEventListener()   {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()){
                    Item item = data.getValue(Item.class);
                    items.add(item);
                }
                listenerFromController.finish(items);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Toast.makeText(context, "itemsDAO.getItemsFromFirebase FAILED", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public List<Item> getItemsFromLocalDBSortedByID() {

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

    public void updateItemDetails(String itemID, String updatedItemName, Integer updatedItemStock, Integer updatedConsumptionRate, Integer updatedMinimumPurchace){

        updateItemDetailsOnLocalDatabase(itemID, updatedItemName, updatedItemStock, updatedConsumptionRate,updatedMinimumPurchace);
        updateItemDetailsOnFirebase(itemID, updatedItemName, updatedItemStock, updatedConsumptionRate,updatedMinimumPurchace);

    }

    public void updateItemDetailsOnLocalDatabase(String itemID, String updatedItemName, Integer updatedItemStock, Integer updatedConsumptionRate, Integer updatedMinimumPurchace){

        SQLiteDatabase database =  databaseHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.NAME, updatedItemName);
        contentValues.put(DatabaseHelper.STOCK, updatedItemStock);
        contentValues.put(DatabaseHelper.CONSUMPTIONRATE, updatedConsumptionRate);
        contentValues.put(DatabaseHelper.MINIMUMPURCHACEQUANTITY, updatedMinimumPurchace);

        database.update(DatabaseHelper.TABLEITEMS,  contentValues, DatabaseHelper.ID + " = " + '"' + itemID + '"' , null);

        database.close();
    }

    public void updateItemDetailsOnFirebase(String itemID, String updatedItemName, Integer updatedItemStock, Integer updatedConsumptionRate, Integer updatedMinimumPurchace){

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase.getReference().child(DatabaseHelper.TABLEITEMS).child(itemID);
        myRef.child(DatabaseHelper.NAME).setValue(updatedItemName);
        myRef.child(DatabaseHelper.STOCK).setValue(updatedItemStock);
        myRef.child(DatabaseHelper.CONSUMPTIONRATE).setValue(updatedConsumptionRate);
        myRef.child(DatabaseHelper.MINIMUMPURCHACEQUANTITY).setValue(updatedMinimumPurchace);

    }


    public void addItemsToLocalDatabase(List<Item> items){
        for (Item item : items){
            addItemToLocalDB(item);
        }
    }

    public void deleteItemFromDatabases(String itemID){

        deleteItemFromLocalDB(itemID);

    }

    public void deleteItemFromLocalDB(String itemID){

        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        try {
            database.delete(DatabaseHelper.TABLEITEMS, DatabaseHelper.ID + " = " + itemID, null);
        } catch (Exception e){

            e.printStackTrace();

        }
    }

    public void updateItemConsumptionRateInDatabases(String itemID, Integer consumptionRate){

        updateItemConsumptionRateInLocalDB(itemID, consumptionRate);
        updateItemConsumptionRateInFirebase(itemID, consumptionRate);


    }

    public void updateItemConsumptionRateInFirebase(String itemID, Integer consumptionRate){

        //UpdateItemConsumptionRateFirebase updateItemConsumptionRateFirebase = new UpdateItemConsumptionRateFirebase(itemID, consumptionRate);
        //updateItemConsumptionRateFirebase.execute();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.getReference().child(DatabaseHelper.TABLEITEMS).child(itemID).child(DatabaseHelper.CONSUMPTIONRATE).setValue(consumptionRate);

    }

    public void updateItemConsumptionRateInLocalDB(String itemID, Integer consumptionRate){

        SQLiteDatabase database = new DatabaseHelper(context).getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.CONSUMPTIONRATE, consumptionRate);

        database.update(DatabaseHelper.TABLEITEMS, contentValues, DatabaseHelper.ID + " = " + '"' + itemID + '"', null);

    }
}
package com.santiagogil.takestock.model.daos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.santiagogil.takestock.controller.PurchacesController;
import com.santiagogil.takestock.model.pojos.Item;
import com.santiagogil.takestock.util.DatabaseHelper;
import com.santiagogil.takestock.util.FirebaseHelper;
import com.santiagogil.takestock.util.ResultListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class ItemsDAO {

    private Context context;
    private DatabaseHelper databaseHelper;
    private FirebaseHelper firebaseHelper;


    public ItemsDAO(Context context){
        this.context = context;
        databaseHelper = new DatabaseHelper(context);
        firebaseHelper = new FirebaseHelper();
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
        database.update(
                DatabaseHelper.TABLEITEMS,
                contentValues, DatabaseHelper.ID + " = " + '"' + itemID + '"',
                null
            );
        database.close();
    }

    public void addItemToLocalDB(Item item){
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        ContentValues row = new ContentValues();

        if(item.getID() != null)
            row.put(DatabaseHelper.ID, item.getID());
        else
            row.put(DatabaseHelper.ID, UUID.randomUUID().toString());

        row.put(DatabaseHelper.USERID, firebaseHelper.getmAuth().getCurrentUser().getUid());
        row.put(DatabaseHelper.NAME, item.getName());
        row.put(DatabaseHelper.STOCK, item.getStock());
        row.put(DatabaseHelper.IMAGE, item.getImage());
        row.put(DatabaseHelper.MINIMUMPURCHACEQUANTITY, item.getMinimumPurchaceQuantity());
        row.put(DatabaseHelper.CONSUMPTIONRATE, item.getConsumptionRate());
        row.put(DatabaseHelper.ACTIVE, booleanToInteger(item.isActive()));
        row.put(DatabaseHelper.PRICE, item.getPrice());

        database.insert(DatabaseHelper.TABLEITEMS, null, row);
        database.close();
    }


    public void addItemToFirebase(Item item){
        firebaseHelper
                .getUserDB()
                .child(DatabaseHelper.TABLEITEMS)
                .child(item.getID())
                .child(DatabaseHelper.ID)
                .setValue(item.getID());
        firebaseHelper
                .getUserDB()
                .child(DatabaseHelper.TABLEITEMS)
                .child(item.getID())
                .child(DatabaseHelper.USERID)
                .setValue(firebaseHelper.getmAuth().getCurrentUser().getUid());
        updateItemDetails(
                item.getID(), item.getName(), item.getStock(), item.getConsumptionRate(),
                item.getMinimumPurchaceQuantity(), item.isActive(), item.getPrice(), item.getUnitsInCart()
        );
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

    public Item buildItemFromQuery(String query){
        SQLiteDatabase database = databaseHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery(query, null);
        if (cursor.moveToNext()) {
            Item item = buildItemFromCursor(cursor);
            cursor.close();
            database.close();
            return item;
        } else {
            cursor.close();
            database.close();
            return null;
        }
    }

    public Item getItemFromLocalDBUsingItemName(String itemName){
        String selectQuery = "SELECT * FROM " + DatabaseHelper.TABLEITEMS + " WHERE " + DatabaseHelper.NAME + " = " + '"'
                +  itemName + '"' + " AND " + DatabaseHelper.USERID + " = " + '"'
                +  firebaseHelper.getCurrentUserID() + '"'  ;

        return buildItemFromQuery(selectQuery);
    }

    public Item getItemFromLocalDB(String itemID){
        String selectQuery = "SELECT * FROM " + DatabaseHelper.TABLEITEMS + " WHERE " + DatabaseHelper.ID + " = " + '"'
                +  itemID + '"' + " AND " + DatabaseHelper.USERID + " = " + '"'
                +  firebaseHelper.getCurrentUserID() + '"';

        return buildItemFromQuery(selectQuery);
    }

    public void getAllItemsFromFirebase(final ResultListener<List<Item>> listenerFromController){

        DatabaseReference dbRef = firebaseHelper.getUserDB().child(DatabaseHelper.TABLEITEMS);
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

                Toast.makeText(context, "itemsDAO.getAllItemsFromFirebase FAILED", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public List<Item> getAllItemsFromLocalDB() {

        String selectQuery = "SELECT * FROM " + DatabaseHelper.TABLEITEMS + " WHERE " + DatabaseHelper.USERID + " = " + '"'
                +  firebaseHelper.getCurrentUserID() + '"';

        return buildItemListFromQuery(selectQuery);
    }

    public Item buildItemFromCursor(Cursor cursor){
        Item item = new Item();

        item.setID(cursor.getString(cursor.getColumnIndex(DatabaseHelper.ID)));
        item.setUserID(cursor.getString(cursor.getColumnIndex(DatabaseHelper.USERID)));
        item.setImage(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.IMAGE)));
        item.setMinimumPurchaceQuantity(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.MINIMUMPURCHACEQUANTITY)));
        item.setName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.NAME)));
        item.setStock(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.STOCK)));
        item.setConsumptionRate(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.CONSUMPTIONRATE)));
        item.setPrice(cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.PRICE)));
        item.setUnitsInCart(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.CART)));
        item.setActive(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.ACTIVE)) > 0);

        return item;
    }

    public List<Item> getActiveItemsFromLocalDB() {

        String selectQuery = "SELECT * FROM " + DatabaseHelper.TABLEITEMS + " WHERE " + DatabaseHelper.ACTIVE
                + " = " + '"' + DatabaseHelper.ACTIVE_TRUE + '"' + " AND " + DatabaseHelper.USERID + " = " + '"'
                +  firebaseHelper.getCurrentUserID() + '"';

        return buildItemListFromQuery(selectQuery);
    }

    public List<Item> buildItemListFromQuery(String query){
        SQLiteDatabase database = databaseHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery(query, null);

        List<Item> items = new ArrayList<>();

        while (cursor.moveToNext()) {
            Item item = buildItemFromCursor(cursor);
            if(!(item.getName() == null))
                items.add(buildItemFromCursor(cursor));
        }
        cursor.close();
        database.close();

        return items;
    }

    public List<Item> getInactiveItemsFromLocalDB() {

        String selectQuery = "SELECT * FROM " + DatabaseHelper.TABLEITEMS + " WHERE " + DatabaseHelper.ACTIVE
                + " = " + '"'+ DatabaseHelper.ACTIVE_FALSE + '"' + " AND " + DatabaseHelper.USERID + " = " + '"'
                +  firebaseHelper.getCurrentUserID() + '"';

        return buildItemListFromQuery(selectQuery);
    }

    public void increaseItemStock(Item item){
        Integer newStock = item.getStock() + item.getMinimumPurchaceQuantity();
        updateItemStock(item, newStock);
        PurchacesController purchacesController = new PurchacesController();
        purchacesController.addPurchaceToDatabases(context, item);
    }

    public void increaseItemStock(String itemID){
        Item item = getItemFromLocalDB(itemID);
        Integer newStock = item.getStock() + item.getMinimumPurchaceQuantity();
        updateItemStock(item, newStock);
    }

    public void decreaseItemStock(Item item){
        Integer newStock = item.getStock() - 1;
        updateItemStock(item, newStock);
    }

    private void updateItemStock(final Item item, final Integer newStock){
        SQLiteDatabase database =  databaseHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.STOCK, newStock);

        database.update(DatabaseHelper.TABLEITEMS,  contentValues, DatabaseHelper.ID + " = " + '"' +  item.getID() + '"' , null);
        database.close();

        firebaseHelper.getUserDB().child(DatabaseHelper.TABLEITEMS).child(item.getID()).child(DatabaseHelper.STOCK).setValue(newStock);

    }

    private void updateItemCart(final Item item, final Integer itemCart){
        SQLiteDatabase database =  databaseHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.CART, itemCart);

        database.update(DatabaseHelper.TABLEITEMS,  contentValues, DatabaseHelper.ID + " = " + '"' +  item.getID() + '"' , null);
        database.close();

        firebaseHelper.getUserDB().child(DatabaseHelper.TABLEITEMS).child(item.getID()).child(DatabaseHelper.CART).setValue(itemCart);
    }

    public void updateItemDetails(String itemID, String updatedItemName, Integer updatedItemStock, Integer updatedConsumptionRate, Integer updatedMinimumPurchace, Boolean active, Double price, Integer cart){

        updateItemDetailsOnLocalDatabase(itemID, updatedItemName, updatedItemStock, updatedConsumptionRate,updatedMinimumPurchace, active, price, cart );
        updateItemDetailsOnFirebase(itemID, updatedItemName, updatedItemStock, updatedConsumptionRate,updatedMinimumPurchace, active, price, cart);

    }

    public void updateItemDetailsOnLocalDatabase(String itemID, String updatedItemName, Integer updatedItemStock, Integer updatedConsumptionRate, Integer updatedMinimumPurchace, Boolean active, Double price, Integer cart){

        SQLiteDatabase database =  databaseHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.NAME, updatedItemName);
        contentValues.put(DatabaseHelper.STOCK, updatedItemStock);
        contentValues.put(DatabaseHelper.CONSUMPTIONRATE, updatedConsumptionRate);
        contentValues.put(DatabaseHelper.MINIMUMPURCHACEQUANTITY, updatedMinimumPurchace);
        contentValues.put(DatabaseHelper.ACTIVE, booleanToInteger(active));
        contentValues.put(DatabaseHelper.PRICE, price);
        contentValues.put(DatabaseHelper.CART, cart);

        database.update(DatabaseHelper.TABLEITEMS,  contentValues, DatabaseHelper.ID + " = " + '"' + itemID + '"' , null);

        database.close();
    }

    private Integer booleanToInteger(Boolean activeStatus){
        if(activeStatus)
            return 1;
        else
            return 0;
    }

    private void updateItemDetailsOnFirebase(
            String itemID, String updatedItemName, Integer updatedItemStock, Integer updatedConsumptionRate,
            Integer updatedMinimumPurchace, Boolean active, Double price, Integer cart ){

        DatabaseReference myRef = firebaseHelper.getUserDB().child(DatabaseHelper.TABLEITEMS).child(itemID);
        myRef.child(DatabaseHelper.ID).setValue(itemID);
        myRef.child(DatabaseHelper.NAME).setValue(updatedItemName);
        myRef.child(DatabaseHelper.STOCK).setValue(updatedItemStock);
        myRef.child(DatabaseHelper.CONSUMPTIONRATE).setValue(updatedConsumptionRate);
        myRef.child(DatabaseHelper.MINIMUMPURCHACEQUANTITY).setValue(updatedMinimumPurchace);
        myRef.child(DatabaseHelper.ACTIVE).setValue(active);
        myRef.child(DatabaseHelper.PRICE).setValue(price);
        myRef.child(DatabaseHelper.CART).setValue(cart);

    }

    public void toggleItemIsActiveInDatabases(String itemID){
        Item item = getItemFromLocalDB(itemID);
        toggleItemIsActiveInLocalDB(item);
        toggleItemIsActiveOnFirebase(item);
    }

    private void toggleItemIsActiveOnFirebase(Item item) {
        DatabaseReference myRef = firebaseHelper.getUserDB();
        if (item.isActive())
            myRef.child(DatabaseHelper.TABLEITEMS).child(item.getID())
                    .child(DatabaseHelper.ACTIVE).setValue(false);
        else {
            myRef.child(DatabaseHelper.TABLEITEMS).child(item.getID())
                    .child(DatabaseHelper.ACTIVE).setValue(true);
        }
    }

    private void toggleItemIsActiveInLocalDB(Item item){
        SQLiteDatabase database =  databaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        if (item.isActive())
            contentValues.put(DatabaseHelper.ACTIVE, DatabaseHelper.ACTIVE_FALSE);
        else
            contentValues.put(DatabaseHelper.ACTIVE, DatabaseHelper.ACTIVE_TRUE);

        database.update(
                DatabaseHelper.TABLEITEMS,
                contentValues, DatabaseHelper.ID + " = " + '"' + item.getID() + '"' ,
                null
                );
        database.close();
    }

    public void updateItemConsumptionRateInDatabases(String itemID, Integer consumptionRate){
        updateItemConsumptionRateInLocalDB(itemID, consumptionRate);
        updateItemConsumptionRateInFirebase(itemID, consumptionRate);
    }

    private void updateItemConsumptionRateInFirebase(String itemID, Integer consumptionRate){
        firebaseHelper.getUserDB().child(DatabaseHelper.TABLEITEMS).child(itemID).child(DatabaseHelper.CONSUMPTIONRATE).setValue(consumptionRate);
    }

    public List<Item> getActiveItemsByIndependence(Integer independence) {

        List<Item> itemsByIndependence = new ArrayList<>();

        for(Item item : getActiveItemsFromLocalDB()){
              if(item.getStock() == 0 || (item.getIndependence()) < independence ){
                itemsByIndependence.add(item);
              }
        }
        return itemsByIndependence;
    }

    public List<Item> sortItemsByIndependence(List<Item> itemList) {

        if(itemList.size() > 1){

            Collections.sort(itemList, new Comparator<Item>(){
                public int compare (Item o1, Item o2){
                    return o1.getIndependence().compareTo(o2.getIndependence());
                }
            });
        }
        return itemList;
    }

    public Integer getItemConsumptionRate(String itemID) {
        return getItemFromLocalDB(itemID).getConsumptionRate();
    }

    public List<Item> getAllItemsWithStockZero() {

        String selectQuery = "SELECT * FROM " + DatabaseHelper.TABLEITEMS + " WHERE " + DatabaseHelper.ACTIVE
                + " = " + '"' + DatabaseHelper.ACTIVE_TRUE + '"' + " AND " + DatabaseHelper.USERID + " = " + '"'
                +  firebaseHelper.getCurrentUserID() + '"' + " AND " + DatabaseHelper.STOCK + " = " + '"'
                +  0 + '"' ;

        return buildItemListFromQuery(selectQuery);
    }

    public List<Item> sortItemsByConsumptionRate(List<Item> itemList) {
        if(itemList.size() > 1){
            Collections.sort(itemList, new Comparator<Item>(){
                public int compare (Item o1, Item o2){
                    return o1.getConsumptionRate().compareTo(o2.getConsumptionRate());
                }
            });
        }
        return itemList;
    }

    public void retrieveItemsFromDefaultFirebaseList(final ResultListener<List<Item>> listenerFromController) {

        DatabaseReference dbRef = firebaseHelper.getDefaultItemDatabase().child(DatabaseHelper.TABLEITEMS);
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

                Toast.makeText(context, "itemsDAO.retrieveItemsFromDefaultFirebaseList FAILED", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void increaseItemCart(Item item) {
        Integer newCartStock = item.getUnitsInCart() + item.getMinimumPurchaceQuantity();
        updateItemCart(item, newCartStock);
    }

    public void decreaseItemCart(Item item) {
        Integer newCart = item.getUnitsInCart() - 1;
        updateItemCart(item, newCart);
    }

    public void cartToStock(Item item) {
        PurchacesController purchacesController = new PurchacesController();
        purchacesController.addPurchaceFromCart(context, item, item.getUnitsInCart());
        updateItemStock(item, item.getStock() + item.getUnitsInCart());
        updateItemCart(item, 0);
    }

    public List<Item> getAllItems() {
        String selectQuery = "SELECT * FROM " + DatabaseHelper.TABLEITEMS ;
        return buildItemListFromQuery(selectQuery);
    }
}
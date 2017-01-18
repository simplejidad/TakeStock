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
import com.santiagogil.takestock.model.pojos.Item;
import com.santiagogil.takestock.util.ResultListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class ItemsDAO{

    private static final String TABLEITEMS = DatabaseHelper.TABLEITEMS;
    private static final String ID = "ID";
    private static final String NAME = "Name"  ;
    private static final String STOCK = "Stock" ;
    private static final String MINIMUMPURCHACEQUANTITY = "MinimumPurchaceQuantity" ;
    private static final String CONSUMPTIONRATE = "ConsumptionRate";
    private static final String IMAGE = "Image" ;

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
        AddItemToFirebaseTask addItemToFirebaseTask = new AddItemToFirebaseTask(itemWithID);
        addItemToFirebaseTask.execute();
        return itemWithID.getID();
    }

    public void addItemToFirebase(Item item){

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase.getReference();
        myRef.child("items").child(item.getID()).setValue(item);

    }

    public void addItemToLocalDB(Item item){

        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        ContentValues row = new ContentValues();

        if(item.getID() != null){
            row.put(ID, item.getID());
        } else {
            row.put(ID, UUID.randomUUID().toString());
        }
        row.put(NAME, item.getName());
        row.put(STOCK, item.getStock());
        row.put(IMAGE, item.getImage());
        row.put(MINIMUMPURCHACEQUANTITY, item.getMinimumPurchaceQuantity());
        row.put(CONSUMPTIONRATE, item.getConsumptionRate());

        database.insert(TABLEITEMS, null, row);

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

    public Item getItemFromLocalDB(String itemName){

        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLEITEMS + " WHERE " + NAME + " = " + '"'
                +  itemName + '"';
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToNext()){

            Item item = new Item();
            item.setID(cursor.getString(cursor.getColumnIndex(ID)));
            item.setImage(cursor.getInt(cursor.getColumnIndex(IMAGE)));
            item.setMinimumPurchaceQuantity(cursor.getInt(cursor.getColumnIndex(MINIMUMPURCHACEQUANTITY)));
            item.setName(cursor.getString(cursor.getColumnIndex(NAME)));
            item.setStock(cursor.getInt(cursor.getColumnIndex(STOCK)));
            item.setConsumptionRate(cursor.getInt(cursor.getColumnIndex(CONSUMPTIONRATE)));

            cursor.close();

            return item;

        }

        return null;
    }

    public void getItemsFromFirebase(final ResultListener<List<Item>> listenerFromController){

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = firebaseDatabase.getReference().child("items");

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

        //RetrieveItemsFromFirebaseAsync retrieveItemsFromFirebaseAsync = new RetrieveItemsFromFirebaseAsync(listenerFromController);
        //retrieveItemsFromFirebaseAsync.execute();

    }

    public void getItemsFromLocalDBSortedByID(final ResultListener<List<Item>> litenerFromController) {

        List<Item> items = getItemsFromLocalDBSortedByID();
        litenerFromController.finish(items);
    }



    public List<Item> getItemsFromLocalDBSortedByID() {

        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLEITEMS;
        Cursor cursor = database.rawQuery(selectQuery, null);

        List<Item> items = new ArrayList<>();

        while (cursor.moveToNext()) {

            Item item = new Item();
            item.setID(cursor.getString(cursor.getColumnIndex(ID)));
            item.setImage(cursor.getInt(cursor.getColumnIndex(IMAGE)));
            item.setMinimumPurchaceQuantity(cursor.getInt(cursor.getColumnIndex(MINIMUMPURCHACEQUANTITY)));
            item.setName(cursor.getString(cursor.getColumnIndex(NAME)));
            item.setStock(cursor.getInt(cursor.getColumnIndex(STOCK)));
            item.setConsumptionRate(cursor.getInt(cursor.getColumnIndex(CONSUMPTIONRATE)));

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


        UpdateItemStockFirebase updateItemStockFirebase = new UpdateItemStockFirebase(item, newStock);

        updateItemStockFirebase.execute();

        SQLiteDatabase database =  databaseHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(STOCK, newStock);

        database.update(TABLEITEMS,  contentValues, ID + " = " + '"' +  item.getID() + '"' , null);

        database.close();

    }

    private class UpdateItemConsumptionRateFirebase extends AsyncTask<String, Void, Void>{

        private String itemId;
        private Integer consumptionRate;

        public UpdateItemConsumptionRateFirebase(String itemId, Integer consumptionRate) {

            this.itemId = itemId;
            this.consumptionRate = consumptionRate;
        }

        @Override
        protected Void doInBackground(String... strings) {

            updateItemConsumptionRateInFirebase(itemId, consumptionRate);

            return null;
        }
    }

    private class UpdateItemStockFirebase extends AsyncTask<String, Void, Void>{

        private Item item;
        private Integer newStock;

        public UpdateItemStockFirebase(Item item, Integer newStock) {
            this.item = item;
            this.newStock = newStock;
        }

        @Override
        protected Void doInBackground(String... strings) {

            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            firebaseDatabase.getReference().child("items").child(item.getID()).child("stock").setValue(newStock);
            return null;
        }
    }

    private class AddItemToFirebaseTask extends AsyncTask <String, Void, Void>{

        private Item item;

        public AddItemToFirebaseTask(Item item) {
            this.item = item;
        }

        @Override
        protected Void doInBackground(String... strings) {

            addItemToFirebase(item);

            return null;
        }
    }

    public void addItemsToLocalDatabase(List<Item> items){
        for (Item item : items){
            addItemToLocalDB(item);
        }
    }

    public void deleteItemFromDatabases(Long ID){

        deleteItemFromLocalDB(ID);

    }

    public void deleteItemFromLocalDB(Long ID){

        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        try {
            database.delete(TABLEITEMS, ItemsDAO.ID + " = " + ID, null);
        } catch (Exception e){

            e.printStackTrace();

        }
    }


    public void updateItemConsumptionRateInDatabases(String itemID, Integer consumptionRate){

        updateItemConsumptionRateInLocalDB(itemID, consumptionRate);
        updateItemConsumptionRateInFirebase(itemID, consumptionRate);


    }

    public void updateItemConsumptionRateInFirebase(String itemID, Integer consumptionRate){

        UpdateItemConsumptionRateFirebase updateItemConsumptionRateFirebase = new UpdateItemConsumptionRateFirebase(itemID, consumptionRate);
        updateItemConsumptionRateFirebase.execute();

    }

    public void updateItemConsumptionRateInLocalDB(String itemID, Integer consumptionRate){

        SQLiteDatabase database = new DatabaseHelper(context).getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(CONSUMPTIONRATE, consumptionRate);

        database.update(TABLEITEMS, contentValues, ID + " = " + '"' + itemID + '"', null);

    }


}
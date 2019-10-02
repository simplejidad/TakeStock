package com.santiagogil.takestock.model.daos;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.santiagogil.takestock.model.pojos.Item;
import com.santiagogil.takestock.util.DatabaseHelper;
import com.santiagogil.takestock.util.FirebaseHelper;
import com.santiagogil.takestock.util.ResultListener;

import java.util.ArrayList;
import java.util.List;

public class ItemsFirebaseDAO {

    FirebaseHelper firebaseHelper = new FirebaseHelper();

    public void getAllItemsFromFirebaseWithUserID(String userID, final ResultListener<List<Item>> listenerFromController){

        DatabaseReference dbRef = firebaseHelper.getUserDBWithUserID(userID).child(DatabaseHelper.TABLEITEMS);
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

            }
        });
    }

    public void newUpdateItemDetailsOnFirebase(String userID,
            String itemID, String updatedItemName, Integer updatedItemStock, Integer updatedConsumptionRate,
            Integer updatedMinimumPurchace, Boolean active, Double price, Integer cart ){

        DatabaseReference myRef = firebaseHelper.getUserDBWithUserID(userID).child(DatabaseHelper.TABLEITEMS).child(itemID);
        myRef.child(DatabaseHelper.ID).setValue(itemID);
        myRef.child(DatabaseHelper.NAME).setValue(updatedItemName);
        myRef.child(DatabaseHelper.STOCK).setValue(updatedItemStock);
        myRef.child(DatabaseHelper.CONSUMPTIONRATE).setValue(updatedConsumptionRate);
        myRef.child(DatabaseHelper.MINIMUMPURCHACEQUANTITY).setValue(updatedMinimumPurchace);
        myRef.child(DatabaseHelper.ACTIVE).setValue(active);
        myRef.child(DatabaseHelper.PRICE).setValue(price);
        myRef.child(DatabaseHelper.CART).setValue(cart);

    }
}

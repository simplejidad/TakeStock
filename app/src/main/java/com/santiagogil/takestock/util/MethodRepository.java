package com.santiagogil.takestock.util;


import com.google.firebase.database.DatabaseReference;
import com.santiagogil.takestock.model.pojos.Item;

public class MethodRepository {

    private void createFirebaseDefaultItemListEnglish() {

        FirebaseHelper firebaseHelper = new FirebaseHelper();

        DatabaseReference defaultItemListEnglish = firebaseHelper.getDefaultItemDatabase().child(DatabaseHelper.TABLEITEMS);


        for(Integer i = 0; i < 25; i++){
            String itemID = "Default Item " + i;
            DatabaseReference defaultItem = defaultItemListEnglish.child(itemID);
            Item item = new Item(itemID);

            defaultItem.child(DatabaseHelper.ACTIVE).setValue(item.isActive());
            defaultItem.child(DatabaseHelper.CONSUMPTIONRATE).setValue(item.getConsumptionRate());
            defaultItem.child(DatabaseHelper.ID).setValue(itemID);
            defaultItem.child(DatabaseHelper.MINIMUMPURCHACEQUANTITY).setValue(item.getMinimumPurchaceQuantity());
            defaultItem.child(DatabaseHelper.NAME).setValue(item.getName());
            defaultItem.child(DatabaseHelper.STOCK).setValue(item.getStock());
            defaultItem.child(DatabaseHelper.USERID).setValue(FirebaseHelper.DEFAULT_ITEM_LIST);

        }
    }
}

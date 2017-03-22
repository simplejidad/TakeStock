package com.santiagogil.takestock.model.pojos.Behaviours;

import android.content.Context;

import com.santiagogil.takestock.controller.ItemsController;
import com.santiagogil.takestock.model.pojos.Item;

import java.util.List;

public class GetAllActiveItemsOrderedByIndependence implements BehaviourGetItemList {

    private Context context;

    public GetAllActiveItemsOrderedByIndependence(Context context) {
        this.context = context;
    }

    @Override
    public List<Item> getItemList() {

        ItemsController itemsController = new ItemsController();
        itemsController.sortItemsByIndependence(context, itemsController.getAllActiveItems(context));
        return null;

    }
}

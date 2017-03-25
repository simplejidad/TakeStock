package com.santiagogil.takestock.model.pojos.Behaviours;

import android.content.Context;

import com.santiagogil.takestock.controller.ItemsController;
import com.santiagogil.takestock.model.pojos.Item;

import java.io.Serializable;
import java.util.List;

public class GetAllActiveItemsSortedByIndependence implements BehaviourGetItemList {

    private Context context;

    public GetAllActiveItemsSortedByIndependence(Context context) {
        this.context = context;
    }

    @Override
    public List<Item> getItemList() {

        ItemsController itemsController = new ItemsController();
        return itemsController.sortItemsByIndependence(context, itemsController.getAllActiveItems(context));

    }
}

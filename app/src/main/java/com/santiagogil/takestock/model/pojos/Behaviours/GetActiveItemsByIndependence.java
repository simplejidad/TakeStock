package com.santiagogil.takestock.model.pojos.Behaviours;

import android.content.Context;

import com.santiagogil.takestock.controller.ItemsController;
import com.santiagogil.takestock.model.pojos.Item;

import java.io.Serializable;
import java.util.List;

public class GetActiveItemsByIndependence implements BehaviourGetItemList {

    private Context context;
    private Integer independence;

    public GetActiveItemsByIndependence(Context context, Integer independence) {
        this.independence = independence;
        this.context = context;
    }

    @Override
    public List<Item> getItemList() {

        ItemsController itemsController = new ItemsController();
        return itemsController.getActiveItemsByIndependence(context, independence);

    }
}

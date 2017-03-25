package com.santiagogil.takestock.model.pojos.Behaviours;

import android.content.Context;

import com.santiagogil.takestock.controller.ItemsController;
import com.santiagogil.takestock.model.pojos.Item;

import java.io.Serializable;
import java.util.List;

public class GetActiveItemsSortedByConsumptionRate implements BehaviourGetItemList {

    private Context context;

    public GetActiveItemsSortedByConsumptionRate(Context context) {
        this.context = context;
    }

    @Override
    public List<Item> getItemList() {

        ItemsController itemsController = new ItemsController();
        return itemsController.sortItemsByConsumptionRate(context, itemsController.getAllActiveItems(context));
    }
}

package com.santiagogil.takestock.model.pojos.Behaviours;

import android.content.Context;

import com.santiagogil.takestock.controller.ItemsController;
import com.santiagogil.takestock.model.pojos.Item;

import java.util.List;

public class GetAllItemsSortedByConsumptionRate implements BehaviourGetItemList {

    @Override
    public List<Item> getItemList(Context context) {
        ItemsController itemsController = new ItemsController();
        return itemsController.sortItemsByConsumptionRate(context, itemsController.getAllActiveItems(context));
    }
}

package com.santiagogil.takestock.model.pojos.Behaviours;

import android.content.Context;

import com.santiagogil.takestock.controller.ItemsController;
import com.santiagogil.takestock.model.pojos.Item;

import java.io.Serializable;
import java.util.List;

public class GetAllInactiveItemsAlphabetically implements BehaviourGetItemList {

    @Override
    public List<Item> getItemList(Context context) {

        ItemsController itemsController = new ItemsController();
        return itemsController.sortItemsAlphabetically(context, itemsController.getAllInactiveItems(context));

    }
}

package com.santiagogil.takestock.model.pojos.Behaviours;

import android.content.Context;

import com.santiagogil.takestock.controller.ItemsController;
import com.santiagogil.takestock.model.pojos.Item;

import java.util.List;

public class GetAllActiveItemsWithStockZero implements BehaviourGetItemList {

    private Context context;

    public GetAllActiveItemsWithStockZero(Context context) {
        this.context = context;
    }

    @Override
    public List<Item> getItemList() {

        ItemsController itemsController = new ItemsController();
        return itemsController.sortItemsAlphabetically(context,
                itemsController.getAllItemsWithStockZero(context));

    }
}

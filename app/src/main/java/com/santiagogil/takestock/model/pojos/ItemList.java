package com.santiagogil.takestock.model.pojos;

import com.google.firebase.database.PropertyName;
import com.santiagogil.takestock.util.DatabaseHelper;

import java.util.List;

public class ItemList {

    @PropertyName(DatabaseHelper.NAME)
    private String name;
    private List<String> itemIDs;
    @PropertyName(DatabaseHelper.OBJECTIVE)
    private Integer objective;
    @PropertyName(DatabaseHelper.ID)
    private String ID;
    private Integer warning;

    public String getName() {
        return name;
    }
}

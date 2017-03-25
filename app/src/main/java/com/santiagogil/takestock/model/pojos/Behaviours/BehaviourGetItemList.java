package com.santiagogil.takestock.model.pojos.Behaviours;

import android.os.Parcelable;

import com.santiagogil.takestock.model.pojos.Item;

import java.io.Serializable;
import java.util.List;

public interface BehaviourGetItemList extends Serializable{

    List<Item> getItemList();
}

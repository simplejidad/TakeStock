package com.santiagogil.takestock.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    //Database
    private static final String DATABASENAME = "TakeStockDB";
    private static final Integer DATABASEVERSION = 1;

    //Tables
    public static final String TABLE_DATABASES = "Databases";
    public static final String TABLEITEMS = "Items";
    public static final String TABLECONSUMPTIONS = "Consumptions";
    public static final String TABLEUSERS = "Users";

    //Columns
    public static final String ID = "ID";
    public static final String NAME = "Name"  ;
    public static final String STOCK = "Stock" ;
    public static final String MINIMUMPURCHACEQUANTITY = "MinimumPurchaceQuantity" ;
    public static final String CONSUMPTIONRATE = "ConsumptionRate";
    public static final String IMAGE = "Image" ;
    public static final String ACTIVE = "Active";
    public static final String DATE = "Date";
    public static final String ITEMID = "ItemID";
    public static final String USERID = "UserID";;
    public static final String PRICE = "Price";
    public static final String CART = "Cart";
    public static final String OBJECTIVE = "Objective";

    //Values
    public static final String ACTIVE_TRUE = "1";
    public static final String ACTIVE_FALSE = "0";

    public DatabaseHelper(Context context) {
        super(context, DATABASENAME, null, DATABASEVERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_ITEMS);
        sqLiteDatabase.execSQL(CREATE_TABLE_CONSUMPTIONS);

    }

    private static final String CREATE_TABLE_ITEMS = "CREATE TABLE " + TABLEITEMS + "("
            + ID + " STRING PRIMARY KEY,"
            + USERID + " TEXT,"
            + NAME + " TEXT,"
            + STOCK + " NUMBER,"
            + IMAGE + " TEXT, "
            + CONSUMPTIONRATE + " NUMBER, "
            + MINIMUMPURCHACEQUANTITY + " NUMBER, "
            + PRICE + " NUMBER, "
            + CART + " NUMBER, "
            + ACTIVE + " BOOLEAN "
            + ")";


    private static final String CREATE_TABLE_CONSUMPTIONS = "CREATE TABLE " + TABLECONSUMPTIONS + "("
            + ID + " STRING PRIMARY KEY,"
            + USERID + " TEXT,"
            + DATE + " TEXT,"
            + ITEMID + " STRING, "
            + PRICE + " NUMBER "
            + ")";



    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}

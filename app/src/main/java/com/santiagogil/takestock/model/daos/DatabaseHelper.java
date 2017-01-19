package com.santiagogil.takestock.model.daos;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASENAME = "TakeStockDB";
    private static final Integer DATABASEVERSION = 1;



    public static final String TABLEITEMS = "Item";
    public static final String ID = "ID";
    public static final String NAME = "Name"  ;
    public static final String STOCK = "Stock" ;
    public static final String MINIMUMPURCHACEQUANTITY = "MinimumPurchaceQuantity" ;
    public static final String CONSUMPTIONRATE = "ConsumptionRate";
    public static final String IMAGE = "Image" ;

    private static final String TABLECONSUMPTIONS = "Consumptions";
    private static final String DATE = "Date";
    private static final String ITEMID = "ItemID";

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
            + NAME + " TEXT,"
            + STOCK + " NUMBER,"
            + IMAGE + " TEXT, "
            + CONSUMPTIONRATE + " NUMBER, "
            + MINIMUMPURCHACEQUANTITY + " NUMBER "
            + ")";


    private static final String CREATE_TABLE_CONSUMPTIONS = "CREATE TABLE " + TABLECONSUMPTIONS + "("
            + ID + " STRING PRIMARY KEY,"
            + DATE + " TEXT,"
            + ITEMID + " STRING "
            + ")";



    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {



    }
}

package com.santiagogil.takestock.model.daos;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASENAME = "TakeStockDB";
    private static final Integer DATABASEVERSION = 1;



    public static final String TABLEITEMS = "Item";
    private static final String ID = "ID";
    private static final String NAME = "Name"  ;
    private static final String STOCK = "Stock" ;
    private static final String MINIMUMPURCHACEQUANTITY = "MinimumPurchaceQuantity" ;
    private static final String CONSUMPTIONRATE = "ConsumptionRate";
    private static final String IMAGE = "Image" ;

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
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + NAME + " TEXT,"
            + STOCK + " NUMBER,"
            + IMAGE + " TEXT, "
            + CONSUMPTIONRATE + " NUMBER, "
            + MINIMUMPURCHACEQUANTITY + " NUMBER "
            + ")";


    private static final String CREATE_TABLE_CONSUMPTIONS = "CREATE TABLE " + TABLECONSUMPTIONS + "("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + DATE + " TEXT,"
            + ITEMID + " NUMBER "
            + ")";



    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {



    }
}

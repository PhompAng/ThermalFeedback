package com.example.phompang.thermalfeedback.app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by phompang on 12/30/2016 AD.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "CallingForDeafDB.db";
    private static final int DB_VERSION = 5;


    public static final String TABLE_ADMIN = "Admin_Account";
    public static final String COL_USERNAME = "Username";
    public static final String COL_PASSWORD = "Password";

    public static final String TABLE_CON = "Contacts";
    public static final String COL_CO_ID = "Category_ID";
    public static final String COL_NAME = "Name";
    public static final String COL_TELEPHON = "Telephon";

    public static final String TABLE_LOG_EXP1 = "Log_EXP1";
    public static final String COL_LO_ID = "Log_ID";
    public static final String COL_ACTIVITY = "Activity";
    public static final String COL_CAT = "Category";
    public static final String COL_IS_FRIEND = "Is_Friend";
    public static final String COL_THERMAL_STI = "Thermal_Sti";
    public static final String COL_VIBRO_STI = "Vibro_Sti";
    public static final String COL_ST_CO = "Start_Contact";
    public static final String COL_AC_CO = "Acknowledge_Contact";
    public static final String COL_IDLE_CO = "Idle_Contact";

    //Sql for creating all tables
    private static final String CREATE_ADMIN_ACCOUNT_TABLE_SQL = "CREATE TABLE " + TABLE_ADMIN
            + "(" + COL_USERNAME + " TEXT PRIMARY KEY, " + COL_PASSWORD + " TEXT);";

    private static final String CREATE_CONTACT_TABLE_SQL = "CREATE TABLE "+TABLE_CON
            + " ("+COL_CO_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_NAME+" TEXT, "
            + COL_TELEPHON+" TEXT);";

    private static final String CREATE_LOG_EXP1_TABLE_SQL = "CREATE TABLE " + TABLE_LOG_EXP1
            + " (" + COL_LO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_ACTIVITY + " TEXT, "
            + COL_CAT + " TEXT, "
            + COL_IS_FRIEND + " TEXT, "
            + COL_THERMAL_STI + " TEXT, "
            + COL_VIBRO_STI + " TEXT, "
            + COL_ST_CO + " TEXT, "
            + COL_AC_CO + " TEXT, "
            + COL_IDLE_CO + " TEXT);";

    public DatabaseHelper(Context context) {
        super(context,  DB_NAME, null,  DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_ADMIN_ACCOUNT_TABLE_SQL);
//        Log.d("DataBaseHelper", "Table Admin is succesfully created.");

//        //load admin account from properties and add to database
//        AssetsPropertyReader assetsPropertyReader = new AssetsPropertyReader(ThermalFeedbackApp.getContext());
//        String username = assetsPropertyReader.getProperty(PropertyConfig.ADMIN_USERNAME_PROPERTY);
//        String password = assetsPropertyReader.getProperty(PropertyConfig.ADMIN_PASSWORD_PROPERTY);
        //Log.i("DataBaseHelper", "Properties File is succesfully loaded with username and password of admin.");
        this.insertIntoAdminAccount(db, "admin", "admin");
//        Log.d("DataBaseHelper", "Admin account is succesfully created.");

        db.execSQL(CREATE_CONTACT_TABLE_SQL);
//        Log.d("DataBaseHelper", "Table CONTACT is succesfully created.");
        db.execSQL(CREATE_LOG_EXP1_TABLE_SQL);
//        Log.d("DataBaseHelper", "Table LOG_EXP1 is succesfully created.");


        //Log.d("CREATE Mutiple Table", "Success");
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Log the version upgrade.
//        Log.w("TaskDBAdapter", "Upgrading from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");

        // Upgrade the existing database to conform to the new version. Multiple
        // previous versions can be handled by comparing _oldVersion and _newVersion
        // values.
        // The simplest case is to drop the old table and create a new one.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ADMIN);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CON);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOG_EXP1);
        onCreate(db);

    }

    private void insertIntoAdminAccount(SQLiteDatabase db, String userName, String password) {
        ContentValues newValues = new ContentValues();
        // Assign values for each row.
        newValues.put(COL_USERNAME, userName);
        newValues.put(COL_PASSWORD, password);

        // Insert the row into your table
        db.insert(TABLE_ADMIN, null, newValues);
        ///Toast.makeText(context, "Reminder Is Successfully Saved", Toast.LENGTH_LONG).show();
    }

    public String getPasswordFromAdminAccount(String userName)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor=db.query(TABLE_ADMIN, null, " " + COL_USERNAME + "=?", new String[]{userName}, null, null, null);
        if(cursor.getCount()<1) // UserName Not Exist
        {
            cursor.close();
            return "NOT EXIST";
        }
        cursor.moveToFirst();
        String password = cursor.getString(cursor.getColumnIndex(COL_PASSWORD));
        cursor.close();
        return password;
    }

}

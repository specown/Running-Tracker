package example.com.runningtracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import example.com.runningtracker.entities.RunEntry;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper
{
    public static final String DATABASE_NAME = "RunEntries";
    public static final String RUN_TABLE = "RunEntryList";
    public static final String RUN_ID = "ID";
    public static final String RUN_DURATION = "Duration";
    public static final String RUN_DISTANCE =  "Distance";
    public static final String RUN_DATE =  "Date";

    public DatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    //Creates SQL Table with run ID, duration, distance and date
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("CREATE TABLE " + RUN_TABLE + "(ID INTEGER PRIMARY KEY AUTOINCREMENT,  Duration TEXT, Distance TEXT, Date TEXT)");
    }

    @Override
    //When DB is updated
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + RUN_TABLE);
        onCreate(db);
    }

    //Adds new entry to the database
    public boolean addRun (RunEntry run)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        //Gets latest run values and puts into table
        contentValues.put(RUN_DURATION, run.getDuration());
        contentValues.put(RUN_DISTANCE, run.getDistance());
        contentValues.put(RUN_DATE, run.getDate().toString());

        long result = db.insert(RUN_TABLE, null, contentValues);

        if(result == -1) {
            return false;
        }

        else {
            return true;
        }
    }

    //Delete all values from table
    public void deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + RUN_TABLE);
    }

    //Used to return run values
    public ArrayList<RunEntry> getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        //Selects runs' from Database
        String query = "SELECT ID, Duration, Distance, Date FROM " + RUN_TABLE;
        Cursor result = db.rawQuery(query, null);
        ArrayList<RunEntry> runEntries = new ArrayList<>();
        //Date format
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy",Locale.US);
        //Check if it is the last run from database
        while(result.moveToNext())
        {
            try {
                runEntries.add(new RunEntry(result.getInt(0), Long.parseLong(result.getString(1)), Double.parseDouble(result.getString(2)), dateFormat.parse(result.getString(3))));
            } catch (Exception e) {
                Log.e("DB", e.toString());
            }
        }
        return runEntries;
    }
}
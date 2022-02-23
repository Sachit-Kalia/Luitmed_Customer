package com.luitmed.prashantimedicos;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {MainData.class}, version = 3, exportSchema = false)
public abstract class RoomDB extends RoomDatabase {

    //create database instance
    private static RoomDB database;

    //Define database name
    private static String DATABASE_NAME = "cart";

    public synchronized static  RoomDB getInstance(Context context){

        if(database == null){
            // initialize database
            database = Room.databaseBuilder(context.getApplicationContext(), RoomDB.class, DATABASE_NAME)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }

        // Return database
        return database;
    }

    // create dao

    public abstract mainDao mainDao();

}

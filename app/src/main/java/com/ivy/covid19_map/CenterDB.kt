package com.ivy.covid19_map

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [CenterData::class], version = 1)
abstract class CenterDB : RoomDatabase() {
    abstract fun getCenterDAO(): CenterDAO

    companion object{
        private var DB_INSTANCE: CenterDB? = null

        fun getDatabase(context: Context): CenterDB {
            if (DB_INSTANCE == null) DB_INSTANCE = Room.databaseBuilder(context.applicationContext, CenterDB::class.java, "CenterDB").build()
            return DB_INSTANCE as CenterDB
        }
    }


}
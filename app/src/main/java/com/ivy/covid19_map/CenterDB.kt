package com.ivy.covid19_map

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [CenterData::class], version = 1)
abstract class CenterDB : RoomDatabase() {
    abstract fun getCenterDAO(): CenterDAO

}
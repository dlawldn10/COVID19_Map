package com.ivy.covid19_map.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ivy.covid19_map.CenterData

@Dao
interface CenterDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCenter(centerData: CenterData)

    @Query("SELECT * FROM CenterData")
    suspend fun selectAllCenter(): List<CenterData>

    @Query("DELETE FROM CenterData")
    suspend fun deleteAllCenter()
}
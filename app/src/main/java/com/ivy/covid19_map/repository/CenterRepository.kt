package com.ivy.covid19_map.repository

import com.ivy.covid19_map.room.CenterDAO
import com.ivy.covid19_map.dataClass.CenterData

/* Room Repository */
class CenterRepository(private val centerDAO: CenterDAO) {

    suspend fun insert(centerArray: ArrayList<CenterData>){
        centerArray.forEach { centerDAO.insertCenter(it) }
    }

    suspend fun deleteAll(){
        centerDAO.deleteAllCenter()
    }

    suspend fun selectAll(): List<CenterData> {
        return centerDAO.selectAllCenter()
    }

    suspend fun selectCount(): Int {
        return centerDAO.selectAllCount()
    }
}
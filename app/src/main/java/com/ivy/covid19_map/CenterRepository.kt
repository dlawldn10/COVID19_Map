package com.ivy.covid19_map

import androidx.annotation.WorkerThread

class CenterRepository(private val centerDAO: CenterDAO) {

    suspend fun insert(centerData: CenterData){
        centerDAO.insertCenter(centerData)
    }

    suspend fun deleteAll(){
        centerDAO.deleteAllCenter()
    }

    suspend fun selectAll(): List<CenterData> {
        return centerDAO.selectAllCenter()
    }
}
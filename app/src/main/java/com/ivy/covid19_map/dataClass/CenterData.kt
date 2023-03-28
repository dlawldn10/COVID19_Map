package com.ivy.covid19_map.dataClass

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

/* 예방 접종 센터 데이터 클래스 */
@Entity
data class CenterData(
    @PrimaryKey
    val id: Int,
    val address: String,
    val centerName: String,
    val facilityName: String,
    val phoneNumber: String,
    val updatedAt: String,
    val lat: String,
    val lng: String,
    val centerType: String
): Serializable

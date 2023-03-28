package com.ivy.covid19_map

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

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

package com.ivy.covid19_map

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CenterData(
    @PrimaryKey
    val id: Int,
    val address: String,
    val centerName: String,
    val facilityName: String,
    val phoneNumber: String,
    val updatedAt: String
)

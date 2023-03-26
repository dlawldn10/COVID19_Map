package com.ivy.covid19_map

data class GetCentersResponseData(
    val totalCount: Int,
    val data: ArrayList<CenterData>
)

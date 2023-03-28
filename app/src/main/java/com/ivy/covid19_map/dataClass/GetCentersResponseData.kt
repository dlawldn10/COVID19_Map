package com.ivy.covid19_map.dataClass

/* api 요청에 대한 response 객체 */
data class GetCentersResponseData(
    val totalCount: Int,
    val data: ArrayList<CenterData>
)

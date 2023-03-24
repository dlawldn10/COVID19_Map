package com.ivy.covid19_map

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface RequestInterface {
    @GET("centers")
    fun getCentersRequest(
        @Header("Authorization") authorization: String,
        @Query("serviceKey") serviceKey: String,
        @Query("page") page: Int,
        @Query("perPage") perPage: Int
    ) : Call<getCentersResponseData>
}
package com.ivy.covid19_map.repository

import android.app.Application
import android.widget.Toast
import com.ivy.covid19_map.R
import com.ivy.covid19_map.controller.SplashActivity
import com.ivy.covid19_map.remoteSource.RequestInterface
import kotlinx.coroutines.flow.flow
import retrofit2.awaitResponse

/* 네트워크 Repository */
class NetworkRepository(private val server: RequestInterface, val application: Application) {

    suspend fun getCenters(page: Int) = flow {
        val response = server.getCentersRequest(
            application.resources.getString(R.string.odcloud_header_authorization_key),
            application.resources.getString(R.string.odcloud_query_service_key),
            page,
            SplashActivity.PER_PAGE
        ).awaitResponse()

        if (response.code() == 200 && response.body() != null){
            if (response.body()!!.totalCount <= 0) {
                Toast.makeText(application, "검색 결과가 없습니다", Toast.LENGTH_SHORT).show()
            }else{
                emit(response.body()!!.data)
            }
        }
    }


}
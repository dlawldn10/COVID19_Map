package com.ivy.covid19_map

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.ivy.covid19_map.databinding.ActivityMainBinding
import com.naver.maps.map.MapFragment.newInstance
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(binding.mapFragmentContainer.id) as MapFragment?
            ?: newInstance().also {
                fm.beginTransaction().add(binding.mapFragmentContainer.id, it).commit()
            }

        getCenters()

    }

    fun getCenters(){
        val okHttpClient = OkHttpClient.Builder()
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.odcloud.kr/api/15077586/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

        var server: RequestInterface = retrofit.create(RequestInterface::class.java)
        server.getCentersRequest(
            resources.getString(R.string.odcloud_header_authorization_key),
            resources.getString(R.string.odcloud_query_service_key),
            1,
            10
        ).enqueue(object : Callback<getCentersResponseData>{
            override fun onResponse(call: Call<getCentersResponseData>, response: Response<getCentersResponseData>) {
                if (response.code() == 200){
                    if (response.body()?.totalCount!! <= 0) {
                        Toast.makeText(applicationContext, "검색 결과가 없습니다", Toast.LENGTH_SHORT).show()
                    }else{
                        for (res in response.body()?.data!!){
                            println("====== $res")
                        }
                    }
                }else{
                    Toast.makeText(applicationContext, "오류 코드: ${response.code()}", Toast.LENGTH_SHORT).show()
                }

            }

            override fun onFailure(call: Call<getCentersResponseData>, t: Throwable) {
                Toast.makeText(applicationContext, "검색 실패", Toast.LENGTH_SHORT).show()
            }

        })

    }
}
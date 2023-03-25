package com.ivy.covid19_map

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.ivy.covid19_map.databinding.ActivitySplashBinding
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import kotlin.concurrent.timer

class SplashActivity : AppCompatActivity() {
    lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        GlobalScope.launch {

            var getCentersCompleted = false
            val getCentersJob = async {
                for (x in 1..10) {
                    getCenters(x)
                }
                // 진행률 딜레이 테스트 코드
                //delay(4000)
                getCentersCompleted = true
            }

            /* period = 1000 = 1초 마다 반복.
            *  period = 10 = 0.01초 마다 반복.
            *  0.01초를 200번 반복하면 2초.
            *  즉, 200번 반복해서 진행률 100을 만드려면
            *  한번 반복에 0.5씩 증가해야함 */
            var progress = 0.0
            timer(period = 10) {
                // 진행률이 100%가 되면 지도 액티비티로 이동
                if (binding.progressBar.progress == 100) {
                    println("----- move to next activity")
                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                    cancel()
                    finish()
                }else{
                    progress += 0.5
                }


                // 80% 진행되었을 때 진행상황 확인
                if (binding.progressBar.progress  == 80){
                    // 덜 끝났다면
                    if (!getCentersCompleted){
                        // 끝날때까지 대기 후 다시 진행
                        runBlocking {
                            getCentersJob.join()
                        }
                    }
                }

                runOnUiThread {
                    binding.progressBar.progress = progress.toInt()
                    binding.timeTextView.text = if (progress >= 100) "100" else progress.toInt().toString()
                }
            }

        }




    }

    fun getCenters(page: Int){
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
            page,
            10
        ).enqueue(object : Callback<getCentersResponseData> {
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
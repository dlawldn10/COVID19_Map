package com.ivy.covid19_map

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.ivy.covid19_map.databinding.ActivitySplashBinding
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import okhttp3.OkHttpClient
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import kotlin.concurrent.timer

class SplashActivity : AppCompatActivity() {
    lateinit var binding: ActivitySplashBinding
    lateinit var server: RequestInterface
    lateinit var centerDB: CenterDB
    lateinit var centerRepository: CenterRepository

    companion object {
        private const val PER_PAGE = 10
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        centerDB = CenterDB.getDatabase(this)
        centerRepository = CenterRepository(centerDB.getCenterDAO())

        val okHttpClient = OkHttpClient.Builder()
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.odcloud.kr/api/15077586/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

        server = retrofit.create(RequestInterface::class.java)


        GlobalScope.launch {
            centerRepository.deleteAll()

            val getCentersJob = async {
                for (x in 1..10) {
                    getCenters(x).collect{ centerRepository.insert(it) }
                }
                // 진행률 딜레이 테스트 코드
                //delay(4000)
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
                if (progress == 80.0){
                    // 덜 끝났다면
                    // 끝날때까지 대기 후 다시 진행
                    runBlocking {
                        getCentersJob.join()
                        // 데이터 저장 테스트 코드
                        println("================")
                        for (i in centerRepository.selectAll()){
                            println(i)
                        }
                        println("================")
                    }

                }

                runOnUiThread {
                    binding.progressBar.progress = progress.toInt()
                    binding.timeTextView.text = if (progress >= 100) "100" else progress.toInt().toString()
                }
            }

        }




    }

    private suspend fun getCenters(page: Int) = flow {

        val response = server.getCentersRequest(
            resources.getString(R.string.odcloud_header_authorization_key),
            resources.getString(R.string.odcloud_query_service_key),
            page,
            PER_PAGE
        ).awaitResponse()

        if (response.code() == 200 && response.body() != null){
            if (response.body()!!.totalCount <= 0) {
                Toast.makeText(applicationContext, "검색 결과가 없습니다", Toast.LENGTH_SHORT).show()
            }else{
                emit(response.body()!!.data)
            }
        }

    }


}